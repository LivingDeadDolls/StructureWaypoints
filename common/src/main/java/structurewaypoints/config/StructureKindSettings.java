package structurewaypoints.config;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.Identifier;
import structurewaypoints.display.StructureColors;
import waypointsapi.api.KindSettings;

/**
 * The structures kind's settings for Waypoints API, kept in {@link StructureWaypointsConfig} (read from its current
 * instance each time, as a reload replaces it). Groups are structure sets.
 */
public final class StructureKindSettings implements KindSettings {
    public static final int DEFAULT_DISPLAY_DISTANCE = 1024;
    public static final int DEFAULT_MAX_SHOWN = 16;

    /** Sets with many structures close together, whose beams would crowd the view: only the nearest is shown by default. */
    private static final Set<Identifier> CROWDED = Set.of(
            Identifier.withDefaultNamespace("mineshafts"),
            Identifier.withDefaultNamespace("buried_treasures"),
            Identifier.withDefaultNamespace("shipwrecks"),
            Identifier.withDefaultNamespace("ocean_ruins"),
            Identifier.withDefaultNamespace("ruined_portals"));

    private static StructureWaypointsConfig config() {
        return StructureWaypointsConfig.get();
    }

    @Override
    public boolean shown() {
        return config().shown;
    }

    /** Off while structures are not worked out, so that those found before are not shown either. */
    @Override
    public boolean active() {
        return config().enabled;
    }

    @Override
    public void setShown(boolean shown) {
        config().shown = shown;
    }

    @Override
    public int displayDistance() {
        return config().displayDistance;
    }

    @Override
    public void setDisplayDistance(int blocks) {
        config().displayDistance = blocks;
    }

    @Override
    public int defaultDisplayDistance() {
        return DEFAULT_DISPLAY_DISTANCE;
    }

    @Override
    public int maxShown() {
        return config().maxShown;
    }

    @Override
    public void setMaxShown(int count) {
        config().maxShown = count;
    }

    @Override
    public int defaultMaxShown() {
        return DEFAULT_MAX_SHOWN;
    }

    @Override
    public boolean groupShown(Identifier group) {
        return !config().hiddenGroups.contains(group.toString());
    }

    @Override
    public void setGroupShown(Identifier group, boolean shown) {
        config().hiddenGroups.remove(group.toString());
        if (!shown) config().hiddenGroups.add(group.toString());
    }

    @Override
    public int groupColor(Identifier group) {
        Integer chosen = config().groupColors.get(group.toString());
        return chosen != null ? chosen : defaultGroupColor(group);
    }

    @Override
    public void setGroupColor(Identifier group, int rgb) {
        if (rgb == -1) {
            config().groupColors.remove(group.toString());
        } else {
            config().groupColors.put(group.toString(), rgb & 0xFFFFFF);
        }
    }

    @Override
    public int defaultGroupColor(Identifier group) {
        return StructureColors.of(group);
    }

    @Override
    public boolean groupNearestOnly(Identifier group) {
        Boolean chosen = config().groupNearestOnly.get(group.toString());
        return chosen != null ? chosen : defaultGroupNearestOnly(group);
    }

    @Override
    public void setGroupNearestOnly(Identifier group, boolean nearestOnly) {
        if (nearestOnly == defaultGroupNearestOnly(group)) {
            config().groupNearestOnly.remove(group.toString());
        } else {
            config().groupNearestOnly.put(group.toString(), nearestOnly);
        }
    }

    @Override
    public boolean defaultGroupNearestOnly(Identifier group) {
        return CROWDED.contains(group);
    }

    @Override
    public Collection<Identifier> groups() {
        return StructureColors.vanillaSets();
    }

    @Override
    public void save() {
        StructureWaypointsConfig.HANDLER.save();
    }

    @Override
    public Optional<Screen> settingsScreen(Screen parent) {
        return Optional.of(StructureWaypointsConfig.createScreen(parent));
    }
}
