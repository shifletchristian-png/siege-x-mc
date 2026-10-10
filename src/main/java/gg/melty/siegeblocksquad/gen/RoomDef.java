// GENERATED from sheets/ by tools/gen.py - edit the sheet, not this file
package gg.melty.siegeblocksquad.gen;

public record RoomDef(String id, String type, int dx, int dy, int dz, int size) {
    public static final RoomDef[] ALL = {
        new RoomDef("entry", "spawn", 0, 0, 0, 5),
        new RoomDef("hall", "room", 0, 0, 14, 9),
        new RoomDef("armory", "room", 14, 0, 14, 9),
        new RoomDef("vault", "room", 0, 0, 28, 9),
        new RoomDef("hostage_cell", "hostage", 14, 0, 28, 5)
    };
    public static RoomDef byId(String id) { for (RoomDef r : ALL) if (r.id.equals(id)) return r; return null; }
}
