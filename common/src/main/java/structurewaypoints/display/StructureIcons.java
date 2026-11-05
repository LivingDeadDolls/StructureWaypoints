package structurewaypoints.display;

import java.util.Map;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

/**
 * The items of the vanilla structure sets, drawn for their waypoints in Waypoints API's list and on JourneyMap's marks:
 * something found in or known for each. Other sets (data packs, mods) have none.
 */
public final class StructureIcons {
    private static final Map<String, Item> VANILLA = Map.ofEntries(
            Map.entry("villages", Items.BELL),
            Map.entry("pillager_outposts", Items.CROSSBOW),
            Map.entry("woodland_mansions", Items.TOTEM_OF_UNDYING),
            Map.entry("desert_pyramids", Items.CHISELED_SANDSTONE),
            Map.entry("jungle_temples", Items.MOSSY_COBBLESTONE),
            Map.entry("swamp_huts", Items.CAULDRON),
            Map.entry("igloos", Items.SNOW_BLOCK),
            Map.entry("ocean_monuments", Items.PRISMARINE_BRICKS),
            Map.entry("ocean_ruins", Items.SUSPICIOUS_SAND),
            Map.entry("shipwrecks", Items.OAK_BOAT),
            Map.entry("buried_treasures", Items.HEART_OF_THE_SEA),
            Map.entry("ruined_portals", Items.CRYING_OBSIDIAN),
            Map.entry("mineshafts", Items.RAIL),
            Map.entry("strongholds", Items.ENDER_EYE),
            Map.entry("ancient_cities", Items.ECHO_SHARD),
            Map.entry("trail_ruins", Items.BRUSH),
            Map.entry("trial_chambers", Items.TRIAL_KEY),
            Map.entry("nether_complexes", Items.NETHER_BRICKS),
            Map.entry("nether_fossils", Items.BONE_BLOCK),
            Map.entry("end_cities", Items.SHULKER_SHELL),
            Map.entry("abandoned_camp", Items.CAMPFIRE));

    private StructureIcons() {
    }

    /** The set's item, or null for a set that is not vanilla. */
    public static Item of(Identifier set) {
        if (!set.getNamespace().equals(Identifier.DEFAULT_NAMESPACE)) return null;
        return VANILLA.get(set.getPath());
    }
}
