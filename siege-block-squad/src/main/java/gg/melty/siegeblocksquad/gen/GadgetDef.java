// GENERATED from sheets/ by tools/gen.py - edit the sheet, not this file
package gg.melty.siegeblocksquad.gen;

public record GadgetDef(String id, String name, String baseItem, int cooldownTicks, String effect, int radius, int amount, int durationTicks) {
    public static final GadgetDef[] ALL = {
        new GadgetDef("breach_charge", "Breach Charge", "minecraft:tnt_minecart", 400, "BREACH", 2, 0, 0),
        new GadgetDef("stim_pistol", "Stim Pistol", "minecraft:golden_carrot", 300, "HEAL_ALLY", 6, 8, 0),
        new GadgetDef("recon_drone", "Recon Drone", "minecraft:spyglass", 600, "PING_ENEMIES", 24, 0, 200),
        new GadgetDef("deployable_shield", "Deployable Shield", "minecraft:shield", 500, "SHIELD_ALLIES", 5, 1, 200)
    };
    public static GadgetDef byId(String id) { for (GadgetDef g : ALL) if (g.id.equals(id)) return g; return null; }
}
