package net.cybercake.ghost.ffa.commands.defaultcommands;

import net.cybercake.ghost.ffa.commands.maincommand.CommandManager;
import net.cybercake.ghost.ffa.Main;
import net.cybercake.ghost.ffa.menus.kits.KitPreviewer;
import net.cybercake.ghost.ffa.menus.kits.KitViewer;
import net.cybercake.ghost.ffa.menus.kits.KitsMain;
import net.cybercake.ghost.ffa.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Kits implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player)) { Main.logError("Only players can execute this command!"); return true; }

        Player player = (Player) sender;

        if(args.length >= 1) {
            if(!Utils.isInteger(args[0])) { Utils.commandStatus(player, Utils.Status.FAILED, "Invalid integer, must be a number between &b1-7"); return true; }
            if(!Utils.isBetweenEquals(Integer.parseInt(args[0]), 1, 7)) { Utils.commandStatus(player, Utils.Status.FAILED, "Invalid kit number, must be a number between &b1-7"); return true; }
            if(!validateKitNumber(player, Integer.parseInt(args[0]))) { Utils.commandStatus(player, Utils.Status.FAILED, "You don't have access to that kit"); return true; }
        }

        if(args.length == 0) {
            KitsMain.openMenu(player);
        }else if(args.length == 1) {
            KitsMain.applyKit(player, Integer.parseInt(args[0]));
        }else if(args.length == 2) {
            switch(args[1]) {
                case "edit":
                    KitViewer.openMenu(player, Integer.parseInt(args[0]));
                    break;
                case "apply":
                    KitsMain.applyKit(player, Integer.parseInt(args[0]));
                    break;
                case "makePublic":
                    Utils.commandStatus(player, Utils.Status.FAILED, "Making kits public is currently not available");
                    break;
                case "preview":
                    KitPreviewer.openMenu(player, Integer.parseInt(args[0]));
                    break;
                default:
                    Utils.commandStatus(player, Utils.Status.FAILED, "Invalid argument");
            }
        }


        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if(!(sender instanceof Player)) { return CommandManager.emptyList; }
        Player player = (Player) sender;
        if(args.length == 1) {
            ArrayList<String> kits = new ArrayList<>();
            kits.add("1");
            kits.add("2");
            kits.add("3");
            if(player.hasPermission("ghostffa.kits.patron")) {
                kits.add("4");
                kits.add("5");
                kits.add("6");
                kits.add("7");
            }else if(player.hasPermission("ghostffa.kits.vip")) {
                kits.add("4");
                kits.add("5");
            }
            return CommandManager.createReturnList(kits, args[0]);
        }else if(args.length == 2) {
            if(Utils.isInteger(args[0]) && validateKitNumber(player, Integer.parseInt(args[0]))) {
                return CommandManager.createReturnList(Arrays.asList("apply", "edit", "makePublic", "preview"), args[1]);
            }
            return CommandManager.emptyList;
        }
        return CommandManager.emptyList;
    }

    public boolean validateKitNumber(Player player, int kitNumber) {
        if(!Utils.isBetweenEquals(kitNumber, 1, 7)) return false;
        if(Utils.isBetweenEquals(kitNumber,1, 3)) return true;
        if(Utils.isBetweenEquals(kitNumber, 4, 5)) {
            if(player.hasPermission("ghostffa.kits.vip")) return true; }
        if(Utils.isBetweenEquals(kitNumber, 6, 7)) {
            if(player.hasPermission("ghostffa.kits.patron")) return true; }
        return false;
    }
}
