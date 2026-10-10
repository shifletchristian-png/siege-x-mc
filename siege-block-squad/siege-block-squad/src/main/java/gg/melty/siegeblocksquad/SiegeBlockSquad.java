package gg.melty.siegeblocksquad;

import com.mojang.brigadier.arguments.StringArgumentType;
import gg.melty.siegeblocksquad.gen.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;

import java.util.*;

/** Siege Block Squad: co-op hostage rescue. Content comes from sheets/ via gen/. */
public class SiegeBlockSquad implements ModInitializer {
    static final String GADGET_TAG = "siege_gadget";

    // Active mission state (one mission per server).
    private BlockPos origin;
    private ServerLevel missionLevel;
    private final Set<UUID> enemies = new HashSet<>();
    private UUID hostage;
    private boolean running;
    private final Map<UUID, String> picks = new HashMap<>();

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, access, env) -> {
            dispatcher.register(Commands.literal("operator")
                .executes(ctx -> { listOperators(ctx.getSource().getPlayerOrException()); return 1; })
                .then(Commands.argument("id", StringArgumentType.word()).executes(ctx -> {
                    pick(ctx.getSource().getPlayerOrException(), StringArgumentType.getString(ctx, "id"));
                    return 1;
                })));
            dispatcher.register(Commands.literal("siege")
                .then(Commands.literal("start").executes(ctx -> {
                    start(ctx.getSource().getPlayerOrException()); return 1;
                }))
                .then(Commands.literal("stop").executes(ctx -> { end(ctx.getSource().getServer().overworld(), null); return 1; })));
        });
        UseItemCallback.EVENT.register((player, world, hand) -> {
            ItemStack stack = player.getItemInHand(hand);
            CustomData data = stack.get(DataComponents.CUSTOM_DATA);
            if (data != null && player instanceof ServerPlayer sp && data.copyTag().contains(GADGET_TAG)) {
                GadgetDef g = GadgetDef.byId(data.copyTag().getString(GADGET_TAG));
                if (g != null && !sp.getCooldowns().isOnCooldown(stack.getItem())) {
                    use(sp, g);
                    sp.getCooldowns().addCooldown(stack.getItem(), g.cooldownTicks());
                    return InteractionResultHolder.success(stack);
                }
            }
            return InteractionResultHolder.pass(stack);
        });
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            if (running && server.getTickCount() % 20 == 0) checkObjective();
        });
    }

    // ---- operator menu (clickable chat) ----
    private void listOperators(ServerPlayer p) {
        p.sendSystemMessage(Component.literal("== Choose your operator =="));
        for (OperatorDef o : OperatorDef.ALL) {
            GadgetDef g = GadgetDef.byId(o.gadget());
            p.sendSystemMessage(Component.literal("[" + o.name() + "] " + o.role() + " - " + g.name())
                .withStyle(s -> s.withClickEvent(new net.minecraft.network.chat.ClickEvent(
                    net.minecraft.network.chat.ClickEvent.Action.RUN_COMMAND, "/operator " + o.id()))));
        }
    }

    private void pick(ServerPlayer p, String id) {
        OperatorDef o = OperatorDef.byId(id);
        if (o == null) { listOperators(p); return; }
        picks.put(p.getUUID(), id);
        Objects.requireNonNull(p.getAttribute(Attributes.MAX_HEALTH)).setBaseValue(o.maxHealth());
        p.setHealth(o.maxHealth());
        Objects.requireNonNull(p.getAttribute(Attributes.MOVEMENT_SPEED)).setBaseValue(0.1 + o.speedBonus() * 0.1);
        GadgetDef g = GadgetDef.byId(o.gadget());
        Item base = BuiltInRegistries.ITEM.get(ResourceLocation.parse(g.baseItem()));
        ItemStack gadget = new ItemStack(base);
        gadget.set(DataComponents.CUSTOM_NAME, Component.literal(g.name()));
        CustomData.update(DataComponents.CUSTOM_DATA, gadget, tag -> tag.putString(GADGET_TAG, g.id()));
        p.getInventory().add(gadget);
        p.sendSystemMessage(Component.literal("You are the " + o.name() + ". Right-click your " + g.name() + "."));
    }

    // ---- gadgets ----
    private void use(ServerPlayer p, GadgetDef g) {
        ServerLevel lvl = p.serverLevel();
        switch (g.effect()) {
            case "BREACH" -> {
                BlockPos t = p.blockPosition().relative(p.getDirection(), 2);
                for (int x = -g.radius(); x <= g.radius(); x++)
                    for (int y = 0; y <= g.radius(); y++)
                        for (int z = -g.radius(); z <= g.radius(); z++) {
                            BlockPos b = t.offset(x, y, z);
                            if (!lvl.getBlockState(b).is(Blocks.BEDROCK)) lvl.removeBlock(b, false);
                        }
            }
            case "HEAL_ALLY" -> {
                for (ServerPlayer o : lvl.players())
                    if (o != p && o.distanceTo(p) <= g.radius()) o.heal(g.amount());
            }
            case "PING_ENEMIES" -> {
                AABB box = p.getBoundingBox().inflate(g.radius());
                for (Mob m : lvl.getEntitiesOfClass(Mob.class, box, m -> enemies.contains(m.getUUID())))
                    m.addEffect(new MobEffectInstance(MobEffects.GLOWING, g.durationTicks()));
            }
            case "SHIELD_ALLIES" -> {
                for (ServerPlayer o : lvl.players())
                    if (o.distanceTo(p) <= g.radius())
                        o.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, g.durationTicks(), g.amount()));
            }
            default -> { }
        }
    }

    // ---- mission ----
    private static net.minecraft.world.level.block.Block block(String id) {
        return BuiltInRegistries.BLOCK.get(ResourceLocation.parse(id));
    }

    private void start(ServerPlayer starter) {
        if (running) { starter.sendSystemMessage(Component.literal("A mission is already running.")); return; }
        if (starter.serverLevel().players().size() > Rules.MAX_PLAYERS) {
            starter.sendSystemMessage(Component.literal("Squads are limited to " + Rules.MAX_PLAYERS + " players."));
            return;
        }
        missionLevel = starter.serverLevel();
        origin = starter.blockPosition().offset(0, 0, 10);
        enemies.clear();
        for (RoomDef r : RoomDef.ALL) buildRoom(r);
        for (ConnDef c : ConnDef.ALL) {
            if (c.type().equals("door")) carveDoor(c); else buildStairs(c);
        }
        RoomDef yard = RoomDef.byId("yard");
        for (ServerPlayer p : missionLevel.players()) {
            p.teleportTo(origin.getX() + yard.dx() + yard.w() / 2.0, origin.getY() + 1, origin.getZ() + yard.dz() + yard.d() / 2.0);
            if (!picks.containsKey(p.getUUID())) listOperators(p);
        }
        for (EnemyDef e : EnemyDef.ALL) {
            RoomDef r = RoomDef.byId(e.room());
            EntityType<?> type = EntityType.byString(e.entity().replace("minecraft:", "")).orElse(EntityType.ZOMBIE);
            for (int i = 0; i < e.count(); i++) {
                Entity ent = type.create(missionLevel);
                if (ent == null) continue;
                ent.moveTo(origin.getX() + r.dx() + 2.5 + (i % 3), origin.getY() + r.dy() + 1, origin.getZ() + r.dz() + 2.5 + (i % 2), 0, 0);
                if (ent instanceof LivingEntity le) {
                    Objects.requireNonNull(le.getAttribute(Attributes.MAX_HEALTH)).setBaseValue(e.maxHealth());
                    le.setHealth(e.maxHealth());
                }
                ent.setCustomName(Component.literal(e.name()));
                if (ent instanceof Mob mob) mob.setPersistenceRequired();
                missionLevel.addFreshEntity(ent);
                enemies.add(ent.getUUID());
            }
        }
        RoomDef cell = RoomDef.byId(Rules.HOSTAGE_ROOM);
        Entity h = EntityType.VILLAGER.create(missionLevel);
        h.moveTo(origin.getX() + cell.dx() + cell.w() / 2.0, origin.getY() + cell.dy() + 1, origin.getZ() + cell.dz() + cell.d() / 2.0, 0, 0);
        h.setCustomName(Component.literal("Hostage"));
        ((net.minecraft.world.entity.npc.Villager) h).setNoAi(true);
        missionLevel.addFreshEntity(h);
        hostage = h.getUUID();
        running = true;
        missionLevel.getServer().getPlayerList().broadcastSystemMessage(
            Component.literal("Rescue the hostage in the kids' dorm. Clear every enemy."), false);
    }

    private void buildRoom(RoomDef r) {
        BlockPos base = origin.offset(r.dx(), r.dy(), r.dz());
        net.minecraft.world.level.block.Block wall = block(r.wall());
        net.minecraft.world.level.block.Block floor = block(r.floor());
        if (r.type().equals("yard")) {
            for (int x = 0; x < r.w(); x++)
                for (int z = 0; z < r.d(); z++) {
                    missionLevel.setBlock(base.offset(x, -1, z), floor.defaultBlockState(), 3);
                    for (int y = 0; y <= 3; y++) missionLevel.setBlock(base.offset(x, y, z), Blocks.AIR.defaultBlockState(), 3);
                }
            return;
        }
        for (int x = -1; x <= r.w(); x++)
            for (int z = -1; z <= r.d(); z++)
                for (int y = -1; y <= 4; y++) {
                    boolean edge = x == -1 || z == -1 || x == r.w() || z == r.d();
                    net.minecraft.world.level.block.Block b;
                    if (y == -1 || y == 4) b = floor;
                    else if (edge) b = wall;
                    else b = Blocks.AIR;
                    missionLevel.setBlock(base.offset(x, y, z), b.defaultBlockState(), 3);
                }
        missionLevel.setBlock(base.offset(r.w() / 2, 3, r.d() / 2), Blocks.SEA_LANTERN.defaultBlockState(), 3);
    }

    private void carveDoor(ConnDef c) {
        RoomDef r = RoomDef.byId(c.room());
        int x, z;
        switch (c.side()) {
            case "north" -> { x = r.dx() + c.offset(); z = r.dz() - 1; }
            case "south" -> { x = r.dx() + c.offset(); z = r.dz() + r.d(); }
            case "east" -> { x = r.dx() + r.w(); z = r.dz() + c.offset(); }
            default -> { x = r.dx() - 1; z = r.dz() + c.offset(); }
        }
        for (int y = 0; y <= 1; y++)
            missionLevel.setBlock(origin.offset(x, r.dy() + y, z), Blocks.AIR.defaultBlockState(), 3);
    }

    /** Steps of full blocks (jump up one per step) climbing along +z into the room above. */
    private void buildStairs(ConnDef c) {
        RoomDef r = RoomDef.byId(c.room());
        int x = r.dx() + c.offset();
        net.minecraft.world.level.block.Block step = block(r.floor());
        for (int i = 1; i <= 5; i++)
            for (int y = 0; y < i; y++)
                missionLevel.setBlock(origin.offset(x, r.dy() + y, r.dz() + i), step.defaultBlockState(), 3);
        for (int i = 3; i <= 4; i++)
            missionLevel.setBlock(origin.offset(x, r.dy() + 4, r.dz() + i), Blocks.AIR.defaultBlockState(), 3);
    }

    private void checkObjective() {
        Entity h = missionLevel.getEntity(hostage);
        boolean hostageAlive = h != null && h.isAlive();
        enemies.removeIf(id -> { Entity e = missionLevel.getEntity(id); return e == null || !e.isAlive(); });
        boolean anyPlayerUp = missionLevel.players().stream().anyMatch(p -> p.isAlive() && !p.isSpectator());
        if (!hostageAlive || !anyPlayerUp) end(missionLevel, Rules.LOSE_TITLE);
        else if (enemies.isEmpty()) end(missionLevel, Rules.WIN_TITLE);
    }

    private void end(ServerLevel lvl, String title) {
        running = false;
        if (title != null)
            lvl.getServer().getPlayerList().broadcastSystemMessage(Component.literal("== " + title + " =="), false);
        if (missionLevel != null && hostage != null) {
            Entity h = missionLevel.getEntity(hostage);
            if (h != null) h.discard();
        }
        enemies.clear();
    }
}
