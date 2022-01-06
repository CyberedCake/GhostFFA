package net.cybercake.ghost.ffa.commands.maincommand.subcommands;

import net.cybercake.ghost.ffa.DuelsLang;
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
        try { PlaceholderAPI.reloadPAPI(); } catch (Exception e) { ex = e; exceptionFile = "placeholderapi.yml"; }
        try { DuelsLang.reloadDuelsLang(); } catch (Exception e) { ex = e; exceptionFile = "duelsLang.yml"; }
        long msAfter = System.currentTimeMillis() - mss;
        if(ex != null) {
            Utils.error(sender, "whilst trying to reload " + exceptionFile, ex);
        }else{
            Utils.commandStatus(sender, Utils.Status.INFO, "&fSuccessfully reloaded the configuration files in &b" + msAfter + "&bms");
        }
    }

    @Override
    public List<String> tab(CommandSender sender, String[] args) {
        return CommandManager.emptyList;
    }
}
