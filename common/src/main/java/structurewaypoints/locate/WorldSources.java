package structurewaypoints.locate;

import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import seedengine.api.SeedEngineApi;
import seedengine.api.WorldCheck;

/**
 * Decides which generation the structures of a dimension are worked out with. In singleplayer, the integrated
 * server's own generator (its data packs and world settings included). On a server, the vanilla data with the world
 * type Seed Engine API worked out from the terrain; nothing while it is not known, or while the seed and world type do
 * not fit the terrain: what would be worked out then is not where the world has it, and it would stay as waypoints.
 */
public final class WorldSources {
    private WorldSources() {
    }

    /** Empty when nothing should be worked out for the dimension now. Client thread. */
    public static Optional<WorldSource> of(Minecraft mc, long seed, ResourceKey<Level> dimension) {
        IntegratedServer integrated = mc.getSingleplayerServer();
        if (integrated != null) {
            ServerLevel level = integrated.getLevel(dimension);
            return level == null ? Optional.empty() : Optional.of(WorldSource.server(level));
        }
        Optional<Identifier> worldType = SeedEngineApi.currentWorldType();
        if (worldType.isEmpty() || SeedEngineApi.currentWorldCheck() == WorldCheck.MISMATCH) return Optional.empty();
        return Optional.of(WorldSource.vanilla(seed, worldType.get(), dimension));
    }
}
