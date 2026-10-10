// GENERATED from sheets/ by tools/gen.py - edit the sheet, not this file
package gg.melty.siegeblocksquad.gen;

public record EnemyDef(String id, String name, String entity, int maxHealth, String room, int count, String siegeAssetRef) {
    public static final EnemyDef[] ALL = {
        new EnemyDef("rusher_meeting", "Rusher", "minecraft:zombie", 20, "meeting_hall", 3, "companion:rainbow-six-siege:enemy-model"),
        new EnemyDef("rusher_laundry", "Rusher", "minecraft:zombie", 20, "laundry", 2, "companion:rainbow-six-siege:enemy-model"),
        new EnemyDef("gunner_kitchen", "Gunner", "minecraft:skeleton", 20, "kitchen", 2, "companion:rainbow-six-siege:enemy-model"),
        new EnemyDef("gunner_tower", "Gunner", "minecraft:skeleton", 20, "tower_2", 1, "companion:rainbow-six-siege:enemy-model"),
        new EnemyDef("guard_generator", "Guard", "minecraft:pillager", 24, "generator", 2, "companion:rainbow-six-siege:enemy-model")
    };
}
