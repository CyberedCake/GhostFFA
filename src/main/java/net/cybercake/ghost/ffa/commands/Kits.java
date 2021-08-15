package net.cybercake.ghost.ffa.commands;

import net.cybercake.ghost.ffa.commands.maincommand.CommandManager;
import net.cybercake.ghost.ffa.Main;
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
            if(!Utils.isNumeric(args[0])) { player.sendMessage(Utils.chat("&cIncorrect kit number, must be numeric!")); return true; }
            if(!Utils.isBetweenEquals(Integer.parseInt(args[0]), 1, 7)) { player.sendMessage(Utils.chat("&cIncorrect kit number, must be between &b1-7")); return true; }
            if(!validateKitNumber(player, Integer.parseInt(args[0]))) { player.sendMessage(Utils.chat("&cYou must buy that kit from the store!")); return true; }
        }

        if(args.length == 0) {
            KitsMain.openMenu(player);
        }else if(args.length == 1) {
            KitsMain.applyKit(player, Integer.parseInt(args[0]));
        }else if(args.length == 2) {
            switch(args[1]) {
                case "edit":
                    KitViewer.openMenu(player, Integer.parseInt(args[0]));
                case "apply":
                    KitsMain.applyKit(player, Integer.parseInt(args[0]));
                default:
                    player.sendMessage(Utils.chat("&cIncorrect argument: &8" + args[1]));
            }
        }


        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
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
            return CommandManager.createReturnList(Arrays.asList("apply", "edit", "clear", "makePublic"), args[1]);
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
