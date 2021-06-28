package info.itsthesky.websky.tools;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.Skript;
import ch.njol.skript.SkriptEventHandler;
import ch.njol.skript.config.Config;
import ch.njol.skript.config.Node;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.lang.*;
import ch.njol.skript.log.CountingLogHandler;
import ch.njol.skript.log.SkriptLogger;
import ch.njol.util.NonNullPair;
import ch.njol.util.Pair;
import ch.njol.util.StringUtils;
import info.itsthesky.websky.WebSky;
import info.itsthesky.websky.skript.WebsiteRequest;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class WebSkyLoader {

    public static final String SITE_FOLDER = "website";
    private static final Pattern SKRIPT_BEACON = Pattern.compile("<\\?sk (.+) \\?>");

    public static File getSkHTMLFile(String name) {
        return new File(WebSky.INSTANCE.getDataFolder(), SITE_FOLDER + "/" + getFormattedName(name));
    }

    public static File getSkHTMLFolder() {
        return new File(WebSky.INSTANCE.getDataFolder(), SITE_FOLDER + "/");
    }

    public static void loadSkHTMLFiles() throws Exception {
        for (File file : getSkHTMLFolder().listFiles())
            if (file.isFile()) {
                loadSkHTMLFile(file.getName());
            }
    }

    public static boolean canLoad(String name) {
        final File skHTMLFile = getSkHTMLFile(name);
        if (!skHTMLFile.exists() || skHTMLFile.isDirectory()) return false;
        return skHTMLFile.canWrite() && skHTMLFile.canRead();
    }

    public static String getFormattedName(String name) {
        return name.endsWith(".skhtml") ? name : name + ".skhtml";
    }

    public static boolean skHTMLFileExist(String name) {
        return getSkHTMLFile(name).exists();
    }

    public static String getPath(String name) {
        return getSkHTMLFile(name).getPath();
    }

    public static CommandSender lastSender;

    public static void warn(String message) {
        lastSender.sendMessage(Utils.colored("&6&l» &e" + message));
    }

    public static int loadSkHTMLFile(String name) throws Exception {
        final File skHTMLFile = getSkHTMLFile(name);
        if (!canLoad(name)) throw new IllegalArgumentException("Cannot write or read the " + skHTMLFile.getName() + " file.");

        @NotNull final Pair<String, String> parseResult = parseSkriptFormatted(Utils.getFileContent(skHTMLFile));
        @NotNull final InputStream stream = Utils.getStreamFromString(parseResult.getFirst());
        @NotNull final List<TriggerItem> items = new ArrayList<>();
        @NotNull final WebSkyScriptManager.WebSkyScript script = new WebSkyScriptManager.WebSkyScript();

        if (stream.available() == 0) {
            warn("No Skript code were found, disabling the section node :)");
        } else {
            final Config config = new Config(stream, getFormattedName(name),
                    WebSky.INSTANCE.getDataFolder().toPath().resolve(SITE_FOLDER).resolve(getFormattedName(name)).toFile(), true, true, ":");
            final Config result = ScriptLoader.loadStructure(config);
            items.addAll(loadScript(result));
        }
        script
                .setSkriptContent(parseResult.getFirst())
                .setHtmlContent(parseResult.getSecond())
                .setItems(items);
        WebSkyScriptManager.put(skHTMLFile, script);
        return items.size();
    }

    public static List<TriggerItem> loadScript(Config config) {

        ParsedEventData parsedEventData = null;

        try {

            for (Node cnode : config.getMainNode()) {

                if (!(cnode instanceof SectionNode)) {
                    Skript.error("invalid line - all code has to be put into triggers");
                    continue;
                }

                final SectionNode node = ((SectionNode) cnode);
                String event = node.getKey();
                if (event == null)
                    continue;

                if (StringUtils.startsWithIgnoreCase(event, "on "))
                    event = "" + event.substring("on ".length());

                event = ScriptLoader.replaceOptions(event);
                final NonNullPair<SkriptEventInfo<?>, SkriptEvent> parsedEvent = SkriptParser.parseEvent(event, "Can't understand this event: '" + node.getKey() + "'");
                if (parsedEvent == null || !parsedEvent.getSecond().shouldLoadEvent())
                    continue;

                if (Skript.debug() || node.debug())
                    Skript.debug(event + " (" + parsedEvent.getSecond().toString(null, true) + "):");

                ScriptLoader.setCurrentEvent("" + parsedEvent.getFirst().getName().toLowerCase(Locale.ENGLISH), parsedEvent.getFirst().events);
                parsedEventData = new ParsedEventData(parsedEvent, event, node, ScriptLoader.loadItems(node));

                if (parsedEvent.getSecond() instanceof SelfRegisteringSkriptEvent) {
                    ((SelfRegisteringSkriptEvent) parsedEvent.getSecond()).afterParse(config);
                }
            }
        } catch (final Exception e) {
            WebSky.exception(e, "loading " + config.getFileName());
        } finally {
            SkriptLogger.setNode(null);
        }

        ScriptLoader.setCurrentEvent("website request", WebsiteRequest.class);

        final Trigger trigger;
        try {
            trigger = new Trigger(config.getFile(), parsedEventData.getEvent(), parsedEventData.getInfo().getSecond(), parsedEventData.getItems());
            trigger.setLineNumber(parsedEventData.getNode().getLine()); // Set line number for debugging
            trigger.setDebugLabel(config.getFileName() + ": line " + parsedEventData.getNode().getLine());
        } finally {
            ScriptLoader.deleteCurrentEvent();
        }

        if (parsedEventData.getInfo().getSecond() instanceof SelfRegisteringSkriptEvent) {
            ((SelfRegisteringSkriptEvent) parsedEventData.getInfo().getSecond()).register(trigger);
            SkriptEventHandler.addSelfRegisteringTrigger(trigger);
        } else {
            SkriptEventHandler.addTrigger(parsedEventData.getInfo().getFirst().events, trigger);
        }

        ScriptLoader.deleteCurrentEvent();

        // WebSky.success("Loaded " + config.getFileName() + " with a total of " + i.triggers + " triggers!");
        return parsedEventData.getItems();
    }

    @Deprecated
    private static String getNonSkriptContent(final String input) {
        return input.replaceAll("<\\?sk(.*?)\\?>", "");
    }

    /**
     * Parse a string into two different part:
     * - The Skript code with comment in no-skript line (first element of pair)
     * - The No-Skript code (webpage code) with delete Skript code & tag (<code><?sk</code> and <code>?></code>)
     * @param input The string input to be parsed
     */
    private static Pair<String, String> parseSkriptFormatted(String input) {
        final StringBuilder builder = new StringBuilder("on website request:").append("\n");
        final StringBuilder page = new StringBuilder();
        input = input.replaceAll("\t", "    ");

        boolean isSkriptCode = false;
        for (String line : input.split("\n")) {
            String formattedLine = line.replaceAll(" {4}", "");
            if (formattedLine.startsWith("<?sk") || formattedLine.startsWith("<?sk ")) isSkriptCode = true;
            if (isSkriptCode) {
                builder.append("    ").append(formattedLine.replaceFirst("<\\?sk ", "").replaceFirst("\\?>", "")).append("\n");
                page.append("\n");
            } else {
                builder.append("    # ").append(line).append("\n");
                page.append(line).append("\n");
            }
            if (formattedLine.startsWith("?>") || formattedLine.endsWith("?>") || formattedLine.contains("?>")) isSkriptCode = false;
        }

        return new Pair<>(builder.toString(), page.toString());
    }

    @Deprecated
    private static String oldGetSkriptContent(final String input) {
        Pattern pattern = Pattern.compile("<\\?sk(.*?)\\?>", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(input);
        final StringBuilder builder = new StringBuilder("on website request:").append("\n");

        while (matcher.find()) {
            String txt = matcher.group(1);
            boolean startWithLine =
                    String.valueOf(txt.charAt(1)).equals("\n") ||
                            (String.valueOf(txt.charAt(1)).equals(" ") && String.valueOf(txt.charAt(2)).equals("\n"));
            System.out.println(txt.charAt(2));
            boolean endWithLine = txt.endsWith("\n");
            System.out.println(startWithLine);
            System.out.println(endWithLine);
            builder.append("    ");
            if (startWithLine) {
                builder.append(txt.replaceFirst("\\n", ""));
            } else {
                builder.append(txt);
            }
            if (!endWithLine)
                builder.append("\n");
        }

        System.out.println("Final:");
        System.out.println("!!"+builder.toString()+"!!");
        return builder.toString();
    }

    /**
     * @author SkriptLang Team, Sky for the reflection things
     */
    public static class ParsedEventData {

        public ParsedEventData(NonNullPair<SkriptEventInfo<?>, SkriptEvent> info, String event, SectionNode node, List<TriggerItem> items) {
            this.info = info;
            this.event = event;
            this.node = node;
            this.items = items;
        }

        private final NonNullPair<SkriptEventInfo<?>, SkriptEvent> info;
        private final String event;
        private final SectionNode node;
        private final List<TriggerItem> items;

        public NonNullPair<SkriptEventInfo<?>, SkriptEvent> getInfo() {
            return info;
        }

        public String getEvent() {
            return event;
        }

        public SectionNode getNode() {
            return node;
        }

        public List<TriggerItem> getItems() {
            return items;
        }
    }
}
