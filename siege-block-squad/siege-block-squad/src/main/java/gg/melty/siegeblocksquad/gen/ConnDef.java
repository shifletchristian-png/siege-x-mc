// GENERATED from sheets/ by tools/gen.py - edit the sheet, not this file
package gg.melty.siegeblocksquad.gen;

public record ConnDef(String id, String type, String room, String side, int offset) {
    public static final ConnDef[] ALL = {
        new ConnDef("front_door", "door", "dining_hall", "north", 5),
        new ConnDef("dining_kitchen", "door", "dining_hall", "east", 3),
        new ConnDef("dining_meeting", "door", "dining_hall", "south", 6),
        new ConnDef("meeting_rear", "door", "meeting_hall", "east", 3),
        new ConnDef("kitchen_rear", "door", "kitchen", "south", 3),
        new ConnDef("kitchen_tower", "door", "kitchen", "east", 3),
        new ConnDef("dorm_kids", "door", "dorm_main_hall", "east", 3),
        new ConnDef("dorm_generator", "door", "dorm_main_hall", "south", 6),
        new ConnDef("kids_armory", "door", "kids_dorm", "south", 3),
        new ConnDef("kids_tower", "door", "kids_dorm", "east", 3),
        new ConnDef("laundry_supply", "door", "laundry", "east", 3),
        new ConnDef("laundry_corridor", "door", "laundry", "south", 3),
        new ConnDef("supply_corridor", "door", "supply", "south", 3),
        new ConnDef("supply_tower", "door", "supply", "east", 3),
        new ConnDef("main_stairs_down", "stairs", "laundry", "up", 1),
        new ConnDef("main_stairs_up", "stairs", "meeting_hall", "up", 1),
        new ConnDef("tower_up_0", "stairs", "tower_b", "up", 1),
        new ConnDef("tower_up_1", "stairs", "tower_1", "up", 3),
        new ConnDef("tower_up_2", "stairs", "tower_2", "up", 1)
    };
}
