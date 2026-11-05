package structurewaypoints.locate;

import java.util.List;
import java.util.function.Predicate;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import seedengine.api.FoundStructure;
import seedengine.api.SeedEngineApi;
import seedengine.api.StructureCandidate;

/**
 * Which world's generation to ask Seed Engine API about, for one dimension: the vanilla data for a seed and world type,
 * or a server's own generator (singleplayer's integrated server). {@link WorldSources} picks one. The calls work on the
 * calling thread, as the API does.
 *
 * @param worldType the world preset for the vanilla data; null with a server level
 * @param level     the server's level; null for the vanilla data
 */
public record WorldSource(ResourceKey<Level> dimension, long seed, Identifier worldType, ServerLevel level) {
    public static WorldSource vanilla(long seed, Identifier worldType, ResourceKey<Level> dimension) {
        return new WorldSource(dimension, seed, worldType, null);
    }

    public static WorldSource server(ServerLevel level) {
        return new WorldSource(level.dimension(), level.getSeed(), null, level);
    }

    public List<StructureCandidate> candidates(ChunkPos from, ChunkPos to) {
        return level != null ? SeedEngineApi.candidates(level, from, to, set -> true)
                : SeedEngineApi.candidates(seed, worldType, dimension, from, to, set -> true);
    }

    public List<FoundStructure> structures(ChunkPos from, ChunkPos to, Predicate<Identifier> sets) {
        return level != null ? SeedEngineApi.structures(level, from, to, sets) : SeedEngineApi.structures(seed, worldType, dimension, from, to, sets);
    }

    /** The structure world generation places for a candidate, if it does. */
    public List<FoundStructure> check(StructureCandidate candidate) {
        Identifier set = candidate.set();
        return structures(candidate.chunk(), candidate.chunk(), set::equals);
    }
}
