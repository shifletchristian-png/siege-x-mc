#!/usr/bin/env python3
"""Preflight the sheets, then generate Java defs from rows. Usage: gen.py [--check]"""
import json, sys, pathlib

root = pathlib.Path(__file__).resolve().parent.parent
sheets = {p.stem: json.loads(p.read_text()) for p in (root / "sheets").glob("*.json")}
problems = []

REQ = {
    "operators": ["id", "name", "role", "maxHealth", "gadget", "speedBonus", "siegeAssetRef", "pickColor"],
    "gadgets": ["id", "name", "baseItem", "cooldownTicks", "effect", "radius", "amount", "durationTicks"],
    "enemies": ["id", "name", "entity", "maxHealth", "room", "count", "siegeAssetRef"],
    "fortress": ["id", "type", "dx", "dy", "dz", "size"],
    "objective": ["id", "winWhen", "loseWhen", "hostageRoom", "hostageHealth", "winTitle", "loseTitle"],
    "multiplayer": ["id", "maxPlayers", "hosting", "connectConfigured", "connectNote"],
}
EFFECTS = {"BREACH", "HEAL_ALLY", "PING_ENEMIES", "SHIELD_ALLIES"}

for name, cols in REQ.items():
    if name not in sheets:
        problems.append(f"missing sheet {name}")
        continue
    for i, row in enumerate(sheets[name]):
        for c in cols:
            if c not in row or row[c] in (None, ""):
                problems.append(f"{name}[{i}].{c} unfilled")

gadget_ids = {g["id"] for g in sheets.get("gadgets", [])}
room_ids = {r["id"] for r in sheets.get("fortress", [])}
for o in sheets.get("operators", []):
    if o.get("gadget") not in gadget_ids:
        problems.append(f"operator {o['id']}: unresolved gadget {o.get('gadget')}")
for g in sheets.get("gadgets", []):
    if g.get("effect") not in EFFECTS:
        problems.append(f"gadget {g['id']}: unknown effect {g.get('effect')}")
for e in sheets.get("enemies", []):
    if e.get("room") not in room_ids:
        problems.append(f"enemy {e['id']}: unresolved room {e.get('room')}")
for ob in sheets.get("objective", []):
    if ob.get("hostageRoom") not in room_ids:
        problems.append(f"objective: unresolved room {ob.get('hostageRoom')}")

if problems:
    print("PREFLIGHT FAILED:")
    for p in problems:
        print(" -", p)
    sys.exit(1)
print("PREFLIGHT CLEAN:", {k: len(v) for k, v in sheets.items()})
if "--check" in sys.argv:
    sys.exit(0)

def q(s): return json.dumps(s)

out = root / "src/main/java/gg/melty/siegeblocksquad/gen"
out.mkdir(parents=True, exist_ok=True)
pkg = "package gg.melty.siegeblocksquad.gen;\n\n"

def write(fname, body):
    (out / fname).write_text("// GENERATED from sheets/ by tools/gen.py - edit the sheet, not this file\n" + pkg + body)

write("OperatorDef.java", """public record OperatorDef(String id, String name, String role, int maxHealth, String gadget, double speedBonus, String siegeAssetRef, String pickColor) {
    public static final OperatorDef[] ALL = {
""" + ",\n".join(
    f"        new OperatorDef({q(o['id'])}, {q(o['name'])}, {q(o['role'])}, {o['maxHealth']}, {q(o['gadget'])}, {o['speedBonus']}, {q(o['siegeAssetRef'])}, {q(o['pickColor'])})"
    for o in sheets["operators"]) + """
    };
    public static OperatorDef byId(String id) { for (OperatorDef o : ALL) if (o.id.equals(id)) return o; return null; }
}
""")

write("GadgetDef.java", """public record GadgetDef(String id, String name, String baseItem, int cooldownTicks, String effect, int radius, int amount, int durationTicks) {
    public static final GadgetDef[] ALL = {
""" + ",\n".join(
    f"        new GadgetDef({q(g['id'])}, {q(g['name'])}, {q(g['baseItem'])}, {g['cooldownTicks']}, {q(g['effect'])}, {g['radius']}, {g['amount']}, {g['durationTicks']})"
    for g in sheets["gadgets"]) + """
    };
    public static GadgetDef byId(String id) { for (GadgetDef g : ALL) if (g.id.equals(id)) return g; return null; }
}
""")

write("EnemyDef.java", """public record EnemyDef(String id, String name, String entity, int maxHealth, String room, int count, String siegeAssetRef) {
    public static final EnemyDef[] ALL = {
""" + ",\n".join(
    f"        new EnemyDef({q(e['id'])}, {q(e['name'])}, {q(e['entity'])}, {e['maxHealth']}, {q(e['room'])}, {e['count']}, {q(e['siegeAssetRef'])})"
    for e in sheets["enemies"]) + """
    };
}
""")

write("RoomDef.java", """public record RoomDef(String id, String type, int dx, int dy, int dz, int size) {
    public static final RoomDef[] ALL = {
""" + ",\n".join(
    f"        new RoomDef({q(r['id'])}, {q(r['type'])}, {r['dx']}, {r['dy']}, {r['dz']}, {r['size']})"
    for r in sheets["fortress"]) + """
    };
    public static RoomDef byId(String id) { for (RoomDef r : ALL) if (r.id.equals(id)) return r; return null; }
}
""")

ob = sheets["objective"][0]
mp = sheets["multiplayer"][0]
write("Rules.java", f"""public final class Rules {{
    private Rules() {{}}
    public static final String HOSTAGE_ROOM = {q(ob['hostageRoom'])};
    public static final int HOSTAGE_HEALTH = {ob['hostageHealth']};
    public static final String WIN_TITLE = {q(ob['winTitle'])};
    public static final String LOSE_TITLE = {q(ob['loseTitle'])};
    public static final int MAX_PLAYERS = {mp['maxPlayers']};
}}
""")
print("generated Java into", out.relative_to(root))
