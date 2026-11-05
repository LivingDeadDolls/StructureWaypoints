package structurewaypoints.locate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import structurewaypoints.StructureWaypointsPlugin;
import structurewaypoints.config.StructureWaypointsConfig;
import waypointsapi.api.WaypointKind;
import waypointsapi.api.WaypointScope;

/**
 * Feeds {@link Locator} from the client tick: the scope to give the structures to, and where the player is in each
 * dimension. The dimensions the player is not in are worked out too, without going there: around where the player
 * would be in each (the Nether at 1/8 of the Overworld's coordinates). Only looks; the work is on the locator's threads.
 */
public final class LocateDriver {
    private static final int INTERVAL_TICKS = 10;
    private static final List<ResourceKey<Level>> DIMENSIONS = List.of(Level.OVERWORLD, Level.NETHER, Level.END);
    private static int ticks;

    private LocateDriver() {
    }

    public static void tick(Minecraft mc) {
        if (++ticks < INTERVAL_TICKS) return;
        ticks = 0;
        Optional<WaypointScope> scope = StructureWaypointsPlugin.kind().flatMap(WaypointKind::scope);
        if (mc.player == null || mc.level == null || scope.isEmpty() || !StructureWaypointsConfig.get().enabled) {
            Locator.reset();
            return;
        }
        ResourceKey<Level> here = mc.level.dimension();
        double hereScale = DimensionScale.of(mc, here);
        int radius = StructureWaypointsConfig.get().searchRadius();
        List<ResourceKey<Level>> order = new ArrayList<>();
        order.add(here);
        DIMENSIONS.stream().filter(dimension -> dimension != here).forEach(order::add);
        List<Locator.Job> jobs = new ArrayList<>();
        for (ResourceKey<Level> dimension : order) {
            WorldSources.of(mc, scope.get().seed(), dimension).ifPresent(source -> jobs.add(new Locator.Job(dimension, source,
                    DimensionScale.chunkIn(mc.player.getX(), mc.player.getZ(), hereScale, DimensionScale.of(mc, dimension)), radius)));
        }
        Locator.update(scope.get(), jobs);
    }
}
