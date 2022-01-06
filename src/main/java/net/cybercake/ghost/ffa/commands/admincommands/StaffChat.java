package net.cybercake.ghost.ffa.commands.admincommands;

import net.cybercake.ghost.ffa.Main;
import net.cybercake.ghost.ffa.commands.maincommand.CommandManager;
import net.cybercake.ghost.ffa.utils.PlayerDataUtils;
import net.cybercake.ghost.ffa.utils.Utils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class StaffChat implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length == 0) {
            if(!(sender instanceof Player)) {
                Main.logInfo(" ");
                Main.logInfo(ChatColor.AQUA + "Online Staff Members:");
                for(Player player : Bukkit.getOnlinePlayers()) {
                    if(player.hasPermission("ghostffa.command.staffchat")) {
                        Main.logInfo(Utils.getFormattedName(player));
                    }
                }
                Main.logInfo(" "); return true;
            }
            Player player = (Player) sender;
            Utils.commandStatus(sender, Utils.Status.INFO, "Staff chat is currently " + ((Boolean)PlayerDataUtils.getPlayerData(player, "staff.enabled") ? "&aENABLED" : "&cDISABLED")); return true;
        }

        if(args[0].equals("-toggle") || args[0].equals("toggle")) {
            if(!(sender instanceof Player)) {
                Main.logError("Console cannot toggle the staff chat!"); return true;
            }
            Player player = (Player) sender;
            boolean changeTo = !((Boolean) PlayerDataUtils.getPlayerData(player, "staff.enabled"));
            PlayerDataUtils.setPlayerData(player, "staff.enabled", changeTo);
            Utils.commandStatus(sender, Utils.Status.SUCCESS, "&fStaff chat is now " + (changeTo ? "&aENABLED" : "&cDISABLED")); return true;
        }

        if(sender instanceof Player && !((Boolean)PlayerDataUtils.getPlayerData(((Player) sender).getPlayer(), "staff.enabled"))) {
            Utils.commandStatus(sender, Utils.Status.FAILED, "Cannot send staff chat message when it is disabled"); return true;
        }

        String message = Utils.getStringFromArguments(0, args);
        for(Player player : Bukkit.getOnlinePlayers()) {
            if(player.hasPermission("ghostffa.command.staffchat") && (Boolean)PlayerDataUtils.getPlayerData(player, "staff.enabled")) {
                player.sendMessage(Utils.chat("&8[&bGFFA &a&lS&8] &e(SC) &f" + (sender instanceof Player ? Utils.getFormattedName(sender.getName()) : "&7" + sender.getName()) + "&f: ") + ChatColor.translateAlternateColorCodes('&', message));
            }
        }
        Main.logInfo(Utils.chat("&8[&bGFFA &a&lS&8] &e(SC) &f" + (sender instanceof Player ? Utils.getFormattedName(sender.getName()) : "&7" + sender.getName()) + "&f: ") + ChatColor.translateAlternateColorCodes('&', message));

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        ArrayList<String> players = new ArrayList<>();
        if(args.length == 1) {
            players.add("-toggle");
        }

        if(args[args.length-1].equals("")) {
            return CommandManager.createReturnListSearch(players, args[args.length-1]);
        }
        for(Player player : Bukkit.getOnlinePlayers()) {
            if(args[args.length-1].length() <= 11 &&
                    args[args.length-1].toLowerCase(Locale.ROOT).startsWith(player.getName().substring(0, args[args.length-1].length()).toLowerCase(Locale.ROOT))) {
                players.add(player.getName());
            }
        }
        return CommandManager.createReturnListSearch(players, args[args.length-1]);
    }
}
