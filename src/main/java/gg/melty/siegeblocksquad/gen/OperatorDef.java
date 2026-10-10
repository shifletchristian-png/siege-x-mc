// GENERATED from sheets/ by tools/gen.py - edit the sheet, not this file
package gg.melty.siegeblocksquad.gen;

public record OperatorDef(String id, String name, String role, int maxHealth, String gadget, double speedBonus, String siegeAssetRef, String pickColor) {
    public static final OperatorDef[] ALL = {
        new OperatorDef("breacher", "Breacher", "Breach", 20, "breach_charge", 0.0, "companion:rainbow-six-siege:operator-icon", "red"),
        new OperatorDef("medic", "Medic", "Support", 20, "stim_pistol", 0.0, "companion:rainbow-six-siege:operator-icon", "green"),
        new OperatorDef("intel", "Intel", "Recon", 16, "recon_drone", 0.1, "companion:rainbow-six-siege:operator-icon", "aqua"),
        new OperatorDef("anchor", "Anchor", "Defense", 28, "deployable_shield", -0.05, "companion:rainbow-six-siege:operator-icon", "gold")
    };
    public static OperatorDef byId(String id) { for (OperatorDef o : ALL) if (o.id.equals(id)) return o; return null; }
}
