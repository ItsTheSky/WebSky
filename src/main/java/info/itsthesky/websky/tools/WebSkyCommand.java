package info.itsthesky.websky.tools;

import ch.njol.skript.config.Config;
import info.itsthesky.websky.WebSky;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class WebSkyCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            sendHelp(sender);
            return false;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            if (args.length == 1) {
                sender.sendMessage(Utils.colored("&4&l» &cMust provide a valid SkHTML file to reload! (or 'all' to reload al of them)"));
                return false;
            }
            if (args[1].equalsIgnoreCase("all")) {

                sender.sendMessage(Utils.colored("&6&l» &eReloading &6ALL &efiles ..."));
                try {
                    WebSkyLoader.loadSkHTMLFiles();
                } catch (Exception ex) {
                    sender.sendMessage(Utils.colored("&4&l» &cCannot load every SkHTML files. Internal error occurred:"));
                    sender.sendMessage(Utils.colored("&4&l» &c" + ex.getMessage()));
                    return false;
                }
                sender.sendMessage(Utils.colored("&2&l» &aReload of all files complete!"));
            } else {
                String input = args[1];
                if (!WebSkyLoader.skHTMLFileExist(input)) {
                    sender.sendMessage(Utils.colored("&4&l» &cThis SkHTML file doesn't exist! ('"+ WebSkyLoader.getPath(input) +"')"));
                    return false;
                }
                if (!WebSkyLoader.canLoad(input)) {
                    sender.sendMessage(Utils.colored("&4&l» &cThis SkHTML file cannot be read or write in! ('"+ WebSkyLoader.getPath(input) +"')"));
                    return false;
                }
                sender.sendMessage(Utils.colored("&6&l» &eReloading &6" + WebSkyLoader.getFormattedName(input) + "&e ..."));
                int node;
                WebSkyLoader.lastSender = sender;
                try {
                    node = WebSkyLoader.loadSkHTMLFile(input);
                } catch (Exception ex) {
                    sender.sendMessage(Utils.colored("&4&l» &cCannot load the SkHTML file, internal error occurred! ('"+ WebSkyLoader.getPath(input) +"')"));
                    WebSky.exception(ex, "loading " + input + ".skhtml");
                    return false;
                }
                sender.sendMessage(Utils.colored("&2&l» &aReload complete! Loaded " + node + " items!"));
            }
            return true;
        }

        sendHelp(sender);
        return true;
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(new String[] {
                Utils.colored("&c&l→ &eWebSky &6v&e" + WebSky.INSTANCE.getDescription().getVersion() + " &6help:"),
                Utils.colored("&1"),
                Utils.colored("&e/websky help"),
                Utils.colored("    &7Show that (beautiful) page"),
                Utils.colored("&1"),
                Utils.colored("&e/websky reload (all|<skhtml>)"),
                Utils.colored("    &7Reload all or a specific SkHTML file present in the &8&o/plugins/WebSky/website/&7&o folder"),
                Utils.colored("&1"),
                Utils.colored("&e/websky info"),
                Utils.colored("    &7Show WebSky's version and the Discord link"),
                Utils.colored("&1"),
        });
    }
}
