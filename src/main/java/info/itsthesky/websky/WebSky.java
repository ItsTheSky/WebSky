package info.itsthesky.websky;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptAddon;
import info.itsthesky.websky.tools.Utils;
import info.itsthesky.websky.tools.WebServer;
import info.itsthesky.websky.tools.WebSkyCommand;
import org.bukkit.Server;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public final class WebSky extends JavaPlugin {

    /* Variables */
    public static final HashMap<String, WebServer> WEB_SERVERS = new HashMap<>();
    public static PluginManager PLUGIN_MANAGER;
    public static WebSky INSTANCE;
    public static Server SERVER;
    public static WebServer server;

    @Override
    public void onEnable() {

        PLUGIN_MANAGER = getServer().getPluginManager();
        INSTANCE = this;
        SERVER = getServer();

        if ((PLUGIN_MANAGER.getPlugin("Skript") != null) && Skript.isAcceptRegistrations()) {
            log("Skript found! Starting registration ...");
            SkriptAddon addon = Skript.registerAddon(this);
            try {
                addon.loadClasses("info.itsthesky.websky.skript");
            } catch (IOException e) {
                Skript.error("Wait, this is anormal. Please report this error on GitHub.");
                e.printStackTrace();
            }
        } else {
            Skript.error("Skript isn't installed or doesn't accept registrations.");
            PLUGIN_MANAGER.disablePlugin(this);
            return;
        }

        getCommand("websky").setExecutor(new WebSkyCommand());
        // example server
        server = new WebServer(5050, new File(WebSky.INSTANCE.getDataFolder(), "website/index.skhtml"), "test");
        log("Starting example server");
        server.start();

        log("Skript syntax seems to be loaded correctly!");
        log("WebSky, made by ItsTheSky v" + getDescription().getVersion() + " has been enabled!");
    }

    public static void log(final String message) {
        SERVER.getConsoleSender().sendMessage(Utils.colored("&9[WebSky] &b" + message));
    }

    public static void success(final String message) {
        SERVER.getConsoleSender().sendMessage(Utils.colored("&2[WebSky] &a" + message));
    }

    public static void warn(final String message) {
        SERVER.getConsoleSender().sendMessage(Utils.colored("&6[WebSky] &e" + message));
    }

    public static void error(final String message) {
        SERVER.getConsoleSender().sendMessage(Utils.colored("&4[WebSky] &c" + message));
    }

    public static void exception(final Exception ex, String message) {
        ex.printStackTrace();
        error("An internal error occurred while " + message + ":");
        error(ex.getMessage());
    }

    @Override
    public void onDisable() {
        server.stop();
        for (Map.Entry<String, WebServer> server : WEB_SERVERS.entrySet()) {
            if (server.getValue().isStarted()) server.getValue().stop();
        }
        INSTANCE = null;
        PLUGIN_MANAGER = null;
    }
}
