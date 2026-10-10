// GENERATED from sheets/ by tools/gen.py - edit the sheet, not this file
package gg.melty.siegeblocksquad.gen;

public record RoomDef(String id, String type, int dx, int dy, int dz, int w, int d, String wall, String floor) {
    public static final RoomDef[] ALL = {
        new RoomDef("yard", "yard", 0, 0, -7, 12, 6, "minecraft:coarse_dirt", "minecraft:coarse_dirt"),
        new RoomDef("laundry", "room", 0, -5, 0, 12, 8, "minecraft:stone_bricks", "minecraft:stone_bricks"),
        new RoomDef("supply", "room", 13, -5, 0, 8, 8, "minecraft:stone_bricks", "minecraft:stone_bricks"),
        new RoomDef("basement_corridor", "room", 0, -5, 9, 21, 4, "minecraft:stone_bricks", "minecraft:stone_bricks"),
        new RoomDef("tower_b", "room", 22, -5, 0, 5, 7, "minecraft:stone_bricks", "minecraft:stone_bricks"),
        new RoomDef("dining_hall", "room", 0, 0, 0, 12, 8, "minecraft:spruce_planks", "minecraft:oak_planks"),
        new RoomDef("kitchen", "room", 13, 0, 0, 8, 8, "minecraft:spruce_planks", "minecraft:oak_planks"),
        new RoomDef("meeting_hall", "room", 0, 0, 9, 12, 8, "minecraft:spruce_planks", "minecraft:oak_planks"),
        new RoomDef("rear_stage", "room", 13, 0, 9, 8, 8, "minecraft:spruce_planks", "minecraft:oak_planks"),
        new RoomDef("tower_1", "room", 22, 0, 0, 5, 7, "minecraft:spruce_planks", "minecraft:oak_planks"),
        new RoomDef("dorm_main_hall", "room", 0, 5, 0, 12, 8, "minecraft:spruce_planks", "minecraft:oak_planks"),
        new RoomDef("kids_dorm", "room", 13, 5, 0, 8, 8, "minecraft:spruce_planks", "minecraft:oak_planks"),
        new RoomDef("generator", "room", 0, 5, 9, 12, 8, "minecraft:spruce_planks", "minecraft:oak_planks"),
        new RoomDef("armory_hall", "room", 13, 5, 9, 8, 8, "minecraft:spruce_planks", "minecraft:oak_planks"),
        new RoomDef("tower_2", "room", 22, 5, 0, 5, 7, "minecraft:spruce_planks", "minecraft:oak_planks"),
        new RoomDef("tower_3", "room", 22, 10, 0, 5, 7, "minecraft:spruce_planks", "minecraft:oak_planks")
    };
    public static RoomDef byId(String id) { for (RoomDef r : ALL) if (r.id.equals(id)) return r; return null; }
}
