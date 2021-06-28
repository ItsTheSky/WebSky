package info.itsthesky.websky.skript;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.util.SimpleEvent;
import ch.njol.skript.registrations.EventValues;
import ch.njol.skript.util.Getter;
import com.sun.net.httpserver.HttpExchange;
import info.itsthesky.websky.tools.BukkitEvent;
import org.jetbrains.annotations.NotNull;

public class WebsiteRequest extends BukkitEvent {

    static {
        Skript.registerEvent("Website Request",
                SimpleEvent.class, WebsiteRequest.class,
                "website request"
        )
                .description(
                        "Fired when someone request something to the website (aka when the website is hosted).",
                        "This event is also used in the main SectionNode of the file."
                )
                .examples(
                        "on website request",
                        "# Or just check the wiki for writing Skript code in files"
                )
                .since("1.0");

        EventValues.registerEventValue(WebsiteRequest.class, String.class, new Getter<String, WebsiteRequest>() {
            @Override
            public @NotNull String get(@NotNull WebsiteRequest event) {
                return "to do lmao";
            }
        }, 0);
    }

    private final HttpExchange event;

    public WebsiteRequest(
            final HttpExchange httpEvent
    ) {
        event = httpEvent;
    }

    public HttpExchange getHTTPEvent() {
        return event;
    }
}
