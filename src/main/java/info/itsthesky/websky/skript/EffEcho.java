package info.itsthesky.websky.skript;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.log.SkriptLogger;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

@Name("Echo")
@Description("Echo / Show a specific string into a webpage")
@Examples("echo \"Hello World!\"")
@Since("0.1")
public class EffEcho extends Effect {

    static {
        Skript.registerEffect(EffEcho.class,
                "(echo|show|share) %object% [(to|on) the [web](page|site)]");
    }

    private String value;
    private int line;
    private Expression<Object> exprObject;

    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        if (ScriptLoader.isCurrentEvent(WebsiteRequest.class)) {
            line = SkriptLogger.getNode().getLine();
            value = "null";
            exprObject = (Expression<Object>) exprs[0];
            return true;
        }
        Skript.error("The 'echo' effect can only be used in a SkHTML page!");
        return false;
    }

    @Override
    public @NotNull String toString(@NotNull Event e, boolean debug) {
        return "echo " + exprObject.toString(e, debug);
    }

    public int getLine() {
        return line;
    }

    public String getValue() {
        return value;
    }

    @Override
    public void execute(Event e) {
        Object string = exprObject.getSingle(e);
        if (string == null) return;
        value = string.toString();
    }
}
