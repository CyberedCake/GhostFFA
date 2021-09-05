package net.cybercake.ghost.ffa.commands.admincommands;

import net.cybercake.ghost.ffa.Main;
import net.cybercake.ghost.ffa.commands.maincommand.CommandManager;
import net.cybercake.ghost.ffa.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Broadcast implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if(args.length == 0) {
            Utils.commandStatus(sender, Utils.Status.FAILED, "Invalid arguments"); return true;
        }
        String who = "self";
        if(args.length >= 1) {
            if(args[0].equals("-toEveryone") || args[0].equals("-toAll")) {
                who = "everyone";
            }else if(args[0].equals("-toStaff")) {
                who = "staff";
            }else if(args[0].equals("-toAdmin")) {
                who = "admin";
            }else if(args[0].equals("-toSelf")) {
                who = "self";
            }else if(args[0].equals("-toConsole")) {
                who = "console";
            }else{
                if(Bukkit.getPlayerExact(args[0]) == null) {
                    Utils.commandStatus(sender, Utils.Status.FAILED, "Invalid audience type or online player");
                }
            }
        }

        if(args.length >= 2) {
            String message = Utils.getStringFromArguments(1, args);

            if(who.equals("self")) {
                sender.sendMessage(Utils.component(message));
            }else if(who.equals("console")) {
                Main.logInfo(Utils.chat(message));
            }else if(who.equals("admin")) {
                for(Player player : Bukkit.getOnlinePlayers()) {
                    if(player.hasPermission("*")) {
                        player.sendMessage(Utils.component(message));
                    }
                }
            }else if(who.equals("staff")) {
                for(Player player : Bukkit.getOnlinePlayers()) {
                    if(player.hasPermission("ghostffa.staff")) {
                        player.sendMessage(Utils.component(message));
                    }
                }
            }else if(who.equals("everyone")) {
                Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(Utils.component(message)));
            }else{
                Player target = Bukkit.getPlayerExact(who);
                target.sendMessage(Utils.component(message));
            }
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if(args.length == 1) {
            ArrayList<String> tabCompletions = new ArrayList<>();
            tabCompletions.add("-toAll");
            tabCompletions.add("-toStaff");
            tabCompletions.add("-toAdmin");
            tabCompletions.add("-toSelf");
            tabCompletions.add("-toConsole");
            tabCompletions.addAll(CommandManager.getPlayerNames());
            return CommandManager.createReturnList(tabCompletions, args[0]);
        }
        return CommandManager.emptyList;
    }
}
