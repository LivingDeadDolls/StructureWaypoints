package structurewaypoints;

import structurewaypoints.config.StructureWaypointsConfig;
import structurewaypoints.platform.Services;

/** Loader-independent startup, called from each loader's client entrypoint (the mod is client-only). */
public final class StructureWaypointsCommon {
    private StructureWaypointsCommon() {
    }

    public static void init() {
        Constants.LOG.info("{} loaded on {}", Constants.MOD_NAME, Services.PLATFORM.getPlatformName());
    }

    /** After {@link #init()}. */
    public static void initClient() {
        StructureWaypointsConfig.HANDLER.load();
    }
}
