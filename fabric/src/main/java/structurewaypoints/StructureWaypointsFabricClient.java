package structurewaypoints;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import structurewaypoints.locate.LocateDriver;

/**
 * The client entrypoint (the mod is client-only): settings, and the tick that hands the work of finding structures to
 * the mod's own threads. The waypoints kind is registered by {@link StructureWaypointsPlugin} (the waypointsapi entrypoint).
 */
public final class StructureWaypointsFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        StructureWaypointsCommon.init();
        StructureWaypointsCommon.initClient();
        ClientTickEvents.END_CLIENT_TICK.register(LocateDriver::tick);
    }
}
