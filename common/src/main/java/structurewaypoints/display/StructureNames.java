package structurewaypoints.display;

import java.util.Locale;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

/** The names of structures and structure sets, from the language files. */
public final class StructureNames {
    private StructureNames() {
    }

    /**
     * {@code structurewaypoints.structure.<namespace>.<path>} from the language files, falling back to the id made
     * readable ("village_plains" becomes "Village Plains"), so structures from data packs and other mods get a name too.
     */
    public static Component structure(Identifier id) {
        return Component.translatableWithFallback("structurewaypoints.structure." + id.getNamespace() + "." + id.getPath(), readable(id.getPath()));
    }

    /** The name of a structure set, the group of its waypoints ("villages" becomes "Villages"). */
    public static Component set(Identifier id) {
        return Component.translatableWithFallback("structurewaypoints.structure_set." + id.getNamespace() + "." + id.getPath(), readable(id.getPath()));
    }

    /**
     * The letter in the diamond of a structure set's waypoints: {@code structurewaypoints.initial.<namespace>.<path>}
     * from the language files, given where the first letters of two sets' names would be the same; null for the others,
     * which get the first letter of their names.
     */
    public static String initial(Identifier id) {
        String key = "structurewaypoints.initial." + id.getNamespace() + "." + id.getPath();
        Language language = Language.getInstance();
        return language.has(key) ? language.getOrDefault(key) : null;
    }

    /** The last part of a path, its words split at underscores and capitalized. */
    static String readable(String path) {
        String last = path.substring(path.lastIndexOf('/') + 1);
        StringBuilder text = new StringBuilder();
        for (String word : last.split("_")) {
            if (word.isEmpty()) continue;
            if (!text.isEmpty()) text.append(' ');
            text.append(word.substring(0, 1).toUpperCase(Locale.ROOT)).append(word.substring(1));
        }
        return text.toString();
    }
}
