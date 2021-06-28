package info.itsthesky.websky.skript;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

@Name("Requested URL")
@Description("Get the requested URL of the page, in a website request event.")
@Since("0.1")
public class ExprWebURL extends SimpleExpression<String> {
    static {
        Skript.registerExpression(ExprWebURL.class, String.class, ExpressionType.SIMPLE,
                "[the] web [ur(l|i)] [used]");
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        if (ScriptLoader.isCurrentEvent(WebsiteRequest.class))
            return true;
        Skript.error("Web-related expression and effect can only be used in a SkHTML file!");
        return false;
    }

    @Override
    protected String[] get(@NotNull Event e) {
        WebsiteRequest event = (WebsiteRequest) e;
        return new String[] {event.getHTTPEvent().getRequestURI().toString()};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends String> getReturnType() {
        return String.class;
    }

    @Override
    public String toString(Event e, boolean debug) {
        return "the web url used";
    }
}