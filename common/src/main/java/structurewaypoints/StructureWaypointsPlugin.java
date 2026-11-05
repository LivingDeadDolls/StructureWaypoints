package structurewaypoints;

import java.util.Optional;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Items;
import structurewaypoints.config.StructureKindSettings;
import structurewaypoints.display.StructureIcons;
import structurewaypoints.display.StructureNames;
import structurewaypoints.locate.Locator;
import waypointsapi.api.KindDefinition;
import waypointsapi.api.WaypointKind;
import waypointsapi.api.WaypointRegistry;
import waypointsapi.api.WaypointsPlugin;

/**
 * Registered under the {@code waypointsapi} entrypoint: the kind of waypoints the structures are shown as, one group
 * per structure set (e.g. {@code minecraft:villages}).
 */
public final class StructureWaypointsPlugin implements WaypointsPlugin {
    public static final Identifier KIND_ID = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "structures");

    private static volatile WaypointKind kind;

    @Override
    public void register(WaypointRegistry registry) {
        WaypointKind registered = registry.register(KindDefinition.builder(KIND_ID, Component.translatable("structurewaypoints.kind.structures"))
                .icon(Items.FILLED_MAP)
                .groupName(StructureNames::set)
                .groupIcon(StructureIcons::of)
                .groupInitial(StructureNames::initial)
                .settings(new StructureKindSettings())
                .build());
        // Another server or seed: what is being worked out belongs to the old one.
        registered.addScopeListener(Locator::reset);
        kind = registered;
    }

    /** The kind, once Waypoints API has called {@link #register}. */
    public static Optional<WaypointKind> kind() {
        return Optional.ofNullable(kind);
    }
}
