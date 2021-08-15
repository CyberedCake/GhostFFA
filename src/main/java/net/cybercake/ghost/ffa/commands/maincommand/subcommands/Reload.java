package net.cybercake.ghost.ffa.commands.maincommand.subcommands;

import net.cybercake.ghost.ffa.commands.maincommand.CommandManager;
import net.cybercake.ghost.ffa.commands.maincommand.SubCommand;
import net.cybercake.ghost.ffa.Main;
import net.cybercake.ghost.ffa.PlaceholderAPI;
import net.cybercake.ghost.ffa.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

public class Reload extends SubCommand {

    public Reload() {
        super("reload", "ghostffa.subcommand.reload", "Reloads the plugin's configuration files.", "/ghostffa reload", new String[]{"rl"});
    }

    @Override
    public void perform(CommandSender sender, String[] args, Command command) {
        long mss = System.currentTimeMillis();
        Exception ex = null;
        String exceptionFile = "";
        try { Main.getPlugin().reloadConfig(); } catch (Exception e) { ex = e; exceptionFile = "config.yml"; }
        try { PlaceholderAPI.reloadPAPI(); } catch (Exception e) { ex = e; exceptionFile = "default-items.yml"; }
        long msAfter = System.currentTimeMillis() - mss;
        if(ex != null) {
            sender.sendMessage(Utils.chat("&cAn error occurred whilst trying to reload the " + exceptionFile + " file!"));
            Bukkit.getLogger().severe("An error occurred whilst trying to reload the " + exceptionFile + " file!");
            Bukkit.getLogger().severe(" ");
            Bukkit.getLogger().severe("Stack trace is below:");
            Utils.printBetterStackTrace(ex);
        }else{
            sender.sendMessage(Utils.chat("&fSuccessfully reloaded the configuration files in &b" + msAfter + "&bms&f!"));
        }
    }

    @Override
    public List<String> tab(CommandSender sender, String[] args) {
        return CommandManager.emptyList;
    }
}
