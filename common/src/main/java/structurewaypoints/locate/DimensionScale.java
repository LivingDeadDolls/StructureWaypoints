package structurewaypoints.locate;

import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;

/**
 * Where the player would be in another dimension: the Nether at 1/8 of the Overworld's coordinates, as the dimension
 * types' {@code coordinate_scale} says (so a data pack that changes it is followed). The End's scale is 1, as the
 * Overworld's.
 */
public final class DimensionScale {
    private static final double VANILLA_NETHER_SCALE = 8.0;

    private DimensionScale() {
    }

    /** The chunk at block {@code (x, z)} of a dimension of scale {@code from}, carried over to one of scale {@code to}. */
    public static ChunkPos chunkIn(double x, double z, double from, double to) {
        double factor = from / to;
        return new ChunkPos((int) Math.floor(x * factor) >> 4, (int) Math.floor(z * factor) >> 4);
    }

    /**
     * How many Overworld blocks one block of a dimension stands for: its dimension type's {@code coordinate_scale}. In
     * singleplayer the server's own; otherwise what the server sent. Client thread.
     */
    public static double of(Minecraft mc, ResourceKey<Level> dimension) {
        IntegratedServer server = mc.getSingleplayerServer();
        ServerLevel serverLevel = server == null ? null : server.getLevel(dimension);
        if (serverLevel != null) return serverLevel.dimensionType().coordinateScale();
        ClientLevel level = mc.level;
        if (level != null) {
            if (level.dimension() == dimension) return level.dimensionType().coordinateScale();
            Optional<DimensionType> type = level.registryAccess().lookup(Registries.DIMENSION_TYPE)
                    .flatMap(types -> types.getOptional(dimension.identifier()));
            if (type.isPresent()) return type.get().coordinateScale();
        }
        return dimension == Level.NETHER ? VANILLA_NETHER_SCALE : 1.0;
    }
}
