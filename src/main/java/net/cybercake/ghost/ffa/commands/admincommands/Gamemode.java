package net.cybercake.ghost.ffa.commands.admincommands;

import net.cybercake.ghost.ffa.Main;
import net.cybercake.ghost.ffa.commands.maincommand.CommandManager;
import net.cybercake.ghost.ffa.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class Gamemode implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player)) {
            Main.logError("Only players can execute this command!"); return true;
        }

        Player player = (Player) sender;

        if(args.length == 0) {
            Utils.commandStatus(player, Utils.Status.FAILED, "Invalid usage");
        }else if(args.length == 1) {
            if(args[0].equalsIgnoreCase("creative") || args[0].equalsIgnoreCase("1") || args[0].equalsIgnoreCase("c")) {
                switchGamemode(player, player, GameMode.CREATIVE, "Creative");
            }else if(args[0].equalsIgnoreCase("survival") || args[0].equalsIgnoreCase("0") || args[0].equalsIgnoreCase("s")) {
                switchGamemode(player, player, GameMode.SURVIVAL, "Survival");
            }else if(args[0].equalsIgnoreCase("spectator") || args[0].equalsIgnoreCase("3") || args[0].equalsIgnoreCase("sp")) {
                switchGamemode(player, player, GameMode.SPECTATOR, "Spectator");
            }else if(args[0].equalsIgnoreCase("adventure") || args[0].equalsIgnoreCase("2") || args[0].equalsIgnoreCase("a")) {
                switchGamemode(player, player, GameMode.ADVENTURE, "Adventure");
            }else{
                Utils.commandStatus(player, Utils.Status.FAILED, "Invalid gamemode");
            }
        }else if(args.length >= 2) {
            Player target = Bukkit.getPlayerExact(args[1]);
            if(target == null) {
                Utils.commandStatus(player, Utils.Status.FAILED, "Invalid online player");
            }else if(args[0].equalsIgnoreCase("creative") || args[0].equalsIgnoreCase("1") || args[0].equalsIgnoreCase("c")) {
                switchGamemode(player, target, GameMode.CREATIVE, "Creative");
            }else if(args[0].equalsIgnoreCase("survival") || args[0].equalsIgnoreCase("0") || args[0].equalsIgnoreCase("s")) {
                switchGamemode(player, target, GameMode.SURVIVAL, "Survival");
            }else if(args[0].equalsIgnoreCase("spectator") || args[0].equalsIgnoreCase("3") || args[0].equalsIgnoreCase("sp") || args[0].equalsIgnoreCase("spec")) {
                switchGamemode(player, target, GameMode.SPECTATOR, "Spectator");
            }else if(args[0].equalsIgnoreCase("adventure") || args[0].equalsIgnoreCase("2") || args[0].equalsIgnoreCase("a")) {
                switchGamemode(player, target, GameMode.ADVENTURE, "Adventure");
            }else{
                Utils.commandStatus(player, Utils.Status.FAILED, "Invalid gamemode");
            }
        }

        return true;
    }

    public static void switchGamemode(Player msgTo, Player switchGamemode, GameMode gameMode, String gamemodeStr){
        if(!switchGamemode.getGameMode().equals(gameMode)) {
            switchGamemode.setGameMode(gameMode);
            Utils.commandStatus(msgTo, Utils.Status.INFO, "Set " + (msgTo == switchGamemode ? "own" : switchGamemode.getName() + "'s") + " gamemode to &b" + gamemodeStr + " &fmode!");
        }else{
            Utils.commandStatus(msgTo, Utils.Status.FAILED, (msgTo == switchGamemode ? "You are" : switchGamemode.getName() + " is") + " already in " + gamemodeStr + " mode!");
        }
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if(args.length == 2) {
            return CommandManager.createReturnList(CommandManager.getPlayerNames(), args[1]);
        }else if(args.length == 1) {
            return CommandManager.createReturnList(Arrays.asList("creative", "survival", "adventure", "spectator"), args[0]);
        }
        return CommandManager.emptyList;
    }
}
