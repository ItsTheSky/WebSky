package info.itsthesky.websky.tools;

import ch.njol.skript.config.SectionNode;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.util.StringUtils;
import info.itsthesky.websky.WebSky;
import info.itsthesky.websky.skript.EffEcho;
import org.bukkit.event.Event;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Parsing {

    Parsing() {}

    public static String parse(Event event, File file) {
        if (!WebSkyScriptManager.SCRIPTS_LOADED.containsKey(file)) return "<h1>This webpage is not loaded through WebSky!</h1>";
        WebSkyScriptManager.WebSkyScript script = WebSkyScriptManager.get(file);
        final List<TriggerItem> items = script.getItems();
        final ArrayList<String> content = new ArrayList<>(Arrays.asList(script.getHtmlContent().split("\n")));
        for (TriggerItem item : items) {
            if (item instanceof EffEcho) {
                EffEcho echoEffect = (EffEcho) item;
                content.add(echoEffect.getLine() - 1, echoEffect.getValue());
            } else {
                if (!item.getTrigger().execute(event)) {
                    WebSky.error("Error while executing a trigger item!");
                }
            }

            item.setNext(null);
        }

        return StringUtils.join(content, "\n");
    }
}
