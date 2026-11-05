package structurewaypoints.config;

import structurewaypoints.Constants;
import structurewaypoints.StructureWaypointsPlugin;
import structurewaypoints.platform.Services;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import waypointsapi.api.KindSettingsOptions;

/**
 * Settings saved to config/structurewaypoints.json5: finding structures, and how their waypoints are shown (the kind's
 * settings Waypoints API reads through {@link StructureKindSettings}). Add a field + option per feature.
 */
public final class StructureWaypointsConfig {
    public static final ConfigClassHandler<StructureWaypointsConfig> HANDLER = ConfigClassHandler.createBuilder(StructureWaypointsConfig.class)
            .id(Identifier.fromNamespaceAndPath(Constants.MOD_ID, "config"))
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(Services.PLATFORM.getConfigDir().resolve(Constants.MOD_ID + ".json5"))
                    .setJson5(true)
                    .build())
            .build();

    public static final int MIN_SEARCH_RADIUS = 8;
    public static final int MAX_SEARCH_RADIUS = 128;

    /** Whether structures are worked out (read by {@link structurewaypoints.locate.LocateDriver}). Off also hides the waypoints. */
    @SerialEntry
    public boolean enabled = true;

    /** How far from the player structures are looked for, in chunks. */
    @SerialEntry
    public int searchRadiusChunks = 64;

    // The kind's settings for Waypoints API (see StructureKindSettings).
    @SerialEntry
    public boolean shown = true;

    @SerialEntry
    public int displayDistance = StructureKindSettings.DEFAULT_DISPLAY_DISTANCE;

    @SerialEntry
    public int maxShown = StructureKindSettings.DEFAULT_MAX_SHOWN;

    /** Structure sets whose waypoints are hidden, by id. */
    @SerialEntry
    public List<String> hiddenGroups = new ArrayList<>();

    /** Colors the player chose, opaque RGB by structure set id. */
    @SerialEntry
    public Map<String, Integer> groupColors = new HashMap<>();

    /** Whether only the nearest is shown, by structure set id, where the player chose otherwise than the default. */
    @SerialEntry
    public Map<String, Boolean> groupNearestOnly = new HashMap<>();

    public static StructureWaypointsConfig get() {
        return HANDLER.instance();
    }

    /** The search radius, kept within the range the screen offers (a file edited by hand may hold anything). */
    public int searchRadius() {
        return Math.clamp(searchRadiusChunks, MIN_SEARCH_RADIUS, MAX_SEARCH_RADIUS);
    }

    public static Screen createScreen(Screen parent) {
        return YetAnotherConfigLib.create(HANDLER, (defaults, config, builder) -> {
            ConfigCategory.Builder general = ConfigCategory.createBuilder()
                    .name(Component.translatable("structurewaypoints.config.category.general"))
                    .option(Option.<Boolean>createBuilder()
                            .name(Component.translatable("structurewaypoints.config.enabled"))
                            .description(OptionDescription.of(Component.translatable("structurewaypoints.config.enabled.desc")))
                            .binding(defaults.enabled, () -> config.enabled, value -> config.enabled = value)
                            .controller(TickBoxControllerBuilder::create)
                            .build())
                    .option(Option.<Integer>createBuilder()
                            .name(Component.translatable("structurewaypoints.config.search_radius"))
                            .description(OptionDescription.of(Component.translatable("structurewaypoints.config.search_radius.desc")))
                            .binding(defaults.searchRadiusChunks, () -> config.searchRadiusChunks, value -> config.searchRadiusChunks = value)
                            .controller(option -> IntegerSliderControllerBuilder.create(option).range(MIN_SEARCH_RADIUS, MAX_SEARCH_RADIUS).step(8))
                            .build());
            // The kind's options, the same as in every mod showing waypoints through Waypoints API.
            StructureWaypointsPlugin.kind().ifPresent(kind -> general.groups(KindSettingsOptions.of(kind)));
            return builder.title(Component.translatable("structurewaypoints.config.title")).category(general.build());
        }).generateScreen(parent);
    }
}
