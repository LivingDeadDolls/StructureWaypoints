package structurewaypoints.locate;

import java.util.List;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import seedengine.api.FoundStructure;
import structurewaypoints.display.StructureNames;
import waypointsapi.api.Place;

/** A structure found, as a waypoint for Waypoints API. */
public final class Places {
    private Places() {
    }

    /**
     * The beam in the middle of the structure's box. Named after the structure (e.g. "Plains Village"), grouped by its
     * set, with its box as the area that marks it explored once the player is inside.
     */
    public static Place of(ResourceKey<Level> dimension, FoundStructure structure) {
        return Place.builder(id(structure.set(), structure.chunk()), dimension, structure.box().getCenter())
                .name(StructureNames.structure(structure.id()))
                .group(structure.set())
                .areas(List.of(structure.box()))
                .build();
    }

    /**
     * The same for the same structure on every run, e.g. {@code minecraft:villages@15,-8}: a set starts at most one
     * structure in a chunk.
     */
    public static String id(Identifier set, ChunkPos chunk) {
        return set + "@" + chunk.x() + "," + chunk.z();
    }
}
