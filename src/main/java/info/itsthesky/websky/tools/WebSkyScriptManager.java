package info.itsthesky.websky.tools;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.util.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.HashMap;
import java.util.List;

public final class WebSkyScriptManager {

    public static final HashMap<File, WebSkyScript> SCRIPTS_LOADED = new HashMap<>();

    public static void add(final File file, final WebSkyScript script) {
        if (SCRIPTS_LOADED.containsKey(file)) return;
        SCRIPTS_LOADED.put(file, script);
    }

    public static void put(final File file, final WebSkyScript script) {
        SCRIPTS_LOADED.put(file, script);
    }

    public static @Nullable WebSkyScript retrieve(final File file) {
        if (!SCRIPTS_LOADED.containsKey(file)) return null;
        WebSkyScript values = SCRIPTS_LOADED.get(file);
        SCRIPTS_LOADED.remove(file, values);
        return values;
    }

    public static @Nullable WebSkyScript get(final File file) {
        if (!SCRIPTS_LOADED.containsKey(file)) return null;
        return SCRIPTS_LOADED.get(file);
    }

    public static class WebSkyScript {

        /**
         * First: Line number
         * Second: TriggerItem according to the line number
         */
        private List<TriggerItem> items;
        private String skriptContent;
        private String htmlContent;

        public WebSkyScript() { }

        public @NotNull List<TriggerItem> getItems() {
            return items;
        }

        public WebSkyScript setItems(List<TriggerItem> items) {
            this.items = items;
            return this;
        }

        public String getSkriptContent() {
            return skriptContent;
        }

        public WebSkyScript setSkriptContent(String skriptContent) {
            this.skriptContent = skriptContent;
            return this;
        }

        public String getHtmlContent() {
            return htmlContent;
        }

        public WebSkyScript setHtmlContent(String htmlContent) {
            this.htmlContent = htmlContent;
            return this;
        }
    }

}
