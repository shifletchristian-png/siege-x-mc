// GENERATED from sheets/ by tools/gen.py - edit the sheet, not this file
package gg.melty.siegeblocksquad.gen;

public record EnemyDef(String id, String name, String entity, int maxHealth, String room, int count, String siegeAssetRef) {
    public static final EnemyDef[] ALL = {
        new EnemyDef("rusher", "Rusher", "minecraft:zombie", 20, "hall", 4, "companion:rainbow-six-siege:enemy-model"),
        new EnemyDef("gunner", "Gunner", "minecraft:skeleton", 20, "armory", 3, "companion:rainbow-six-siege:enemy-model"),
        new EnemyDef("guard", "Guard", "minecraft:pillager", 24, "vault", 2, "companion:rainbow-six-siege:enemy-model")
    };
}
