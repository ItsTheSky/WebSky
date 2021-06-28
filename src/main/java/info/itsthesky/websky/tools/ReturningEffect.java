package info.itsthesky.websky.tools;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.log.SkriptLogger;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

/**
 * Effect that return something special, mainly used in the echo effect
 */
public abstract class ReturningEffect<T> extends Effect {

    private T value = null;
    private int line;

    @Override
    protected void execute(@NotNull Event e) {
        value = executeAndReturn(e);
    }

    public abstract boolean initEffect(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult);

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        line = SkriptLogger.getNode().getLine();
        return initEffect(exprs, matchedPattern, isDelayed, parseResult);
    }

    public abstract T executeAndReturn(Event e);

    /**
     * WARNING: Will be null if the effect is not fired correctly!
     * @return The getter of the value
     */
    public T getValue() {
        return value;
    }

    public int getLine() {
        return line;
    }
}
