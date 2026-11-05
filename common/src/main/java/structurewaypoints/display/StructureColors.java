package structurewaypoints.display;

import java.util.List;
import java.util.Map;
import net.minecraft.resources.Identifier;

/**
 * The colors of the vanilla structure sets' waypoints, chosen to tell them apart and to hint at what they are. Other
 * sets (data packs, mods) get -1, for one Waypoints API picks from the id. The player can change them in the
 * settings (see StructureKindSettings).
 */
public final class StructureColors {
    private static final Map<String, Integer> VANILLA = Map.ofEntries(
            Map.entry("villages", 0x8BC34A),
            Map.entry("pillager_outposts", 0x9E9E9E),
            Map.entry("woodland_mansions", 0x6D4C41),
            Map.entry("desert_pyramids", 0xFFD54F),
            Map.entry("jungle_temples", 0x2E7D32),
            Map.entry("swamp_huts", 0x7E57C2),
            Map.entry("igloos", 0xE0F7FA),
            Map.entry("ocean_monuments", 0x26C6DA),
            Map.entry("ocean_ruins", 0x4DB6AC),
            Map.entry("shipwrecks", 0xA1887F),
            Map.entry("buried_treasures", 0xFFB300),
            Map.entry("ruined_portals", 0xAB47BC),
            Map.entry("mineshafts", 0xBCAAA4),
            Map.entry("strongholds", 0x1DE9B6),
            Map.entry("ancient_cities", 0x37474F),
            Map.entry("trail_ruins", 0xD7A86E),
            Map.entry("trial_chambers", 0xFF7043),
            Map.entry("nether_complexes", 0xC62828),
            Map.entry("nether_fossils", 0xEEEEEE),
            Map.entry("end_cities", 0xE1BEE7));

    private StructureColors() {
    }

    /** The vanilla structure sets, the groups the settings list even before any waypoint of them was found. */
    public static List<Identifier> vanillaSets() {
        return VANILLA.keySet().stream().sorted().map(Identifier::withDefaultNamespace).toList();
    }

    /** Opaque RGB, or -1 for a color picked from the set's id. */
    public static int of(Identifier set) {
        if (!set.getNamespace().equals(Identifier.DEFAULT_NAMESPACE)) return -1;
        return VANILLA.getOrDefault(set.getPath(), -1);
    }
}
