package net.cybercake.ghost.ffa.commands.admincommands;

import net.cybercake.ghost.ffa.Main;
import net.cybercake.ghost.ffa.commands.maincommand.CommandManager;
import net.cybercake.ghost.ffa.utils.ItemUtils;
import net.cybercake.ghost.ffa.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Clear implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player)) {
            Main.logError("Only players can execute this command!"); return true;
        }

        Player player = (Player) sender;

        if(args.length == 0) {
            clearInventory(player, player, "all");
        }else if(args.length == 1) {
            Player target = Bukkit.getPlayerExact(args[0]);
            if(target == null) {
                Utils.commandStatus(player, Utils.Status.FAILED, "Invalid online player");
            }else{
                clearInventory(player, target, "all");
            }
        }else if(args.length == 2) {
            Player target = Bukkit.getPlayerExact(args[0]);
            if(target == null) {
                Utils.commandStatus(player, Utils.Status.FAILED, "Invalid online player");
            }else{
                String argument = args[1];
                if(!argument.startsWith("minecraft:")) {
                    argument = "minecraft:" + argument;
                }
                clearInventory(player, target, argument);
            }
        }


        return true;
    }

    public static void clearInventory(Player msgTo, Player clearWho, String specificItem) {
        boolean isNull = true;
        for(ItemStack itemStack : clearWho.getInventory()) {
            if(itemStack != null) {
                isNull = false;
                break;
            }
        }

        if(specificItem.equalsIgnoreCase("all")) {
            if(isNull) {
                Utils.commandStatus(msgTo, Utils.Status.FAILED, (msgTo == clearWho ? "You have" : clearWho.getName() + " has") + " no items to remove from " + (msgTo == clearWho ? "your" : "their") + " inventory");
            }else{
                Utils.commandStatus(msgTo, Utils.Status.INFO, "You cleared &b" + (msgTo == clearWho ? "your" : clearWho.getName() + "&f's") + " &finventory");
                clearWho.getInventory().clear();
            }
        }else {
            if (isNull) {
                Utils.commandStatus(msgTo, Utils.Status.FAILED, (msgTo == clearWho ? "You have" : clearWho.getName() + " has") + " no items to remove from " + (msgTo == clearWho ? "your" : "their") + " inventory");
            }else if(!ItemUtils.mcItems().contains(ItemUtils.toKey(specificItem))) {
                Utils.commandStatus(msgTo, Utils.Status.FAILED, "Invalid MC item key");
            }else if(!clearWho.getInventory().contains(ItemUtils.mcKeyToMaterial(specificItem))) {
                Utils.commandStatus(msgTo, Utils.Status.FAILED, (msgTo == clearWho ? "You don't " : clearWho.getName() + " doesn't ") + "have any of that item in their inventory");
            }else{
                Utils.commandStatus(msgTo, Utils.Status.INFO, "You removed all &b" + new ItemStack(ItemUtils.mcKeyToMaterial(specificItem), 1).getI18NDisplayName() + " &ffrom " + (msgTo == clearWho ? "yourself" : clearWho.getName()));
                clearWho.getInventory().remove(ItemUtils.mcKeyToMaterial(specificItem));
            }
        }
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if(args.length == 2){
            ArrayList<String> withoutMinecraftColon = new ArrayList<>();
            withoutMinecraftColon.add("minecraft:");
            for(String material : ItemUtils.mcItems()) {
                if(args[1].startsWith("minecraft:")) {
                    withoutMinecraftColon.add(material);
                } else{
                    withoutMinecraftColon.add(material.replace("minecraft:", ""));
                }
            }
            return CommandManager.createReturnList(withoutMinecraftColon, args[1]);
        }else if(args.length == 1) {
            return CommandManager.createReturnList(CommandManager.getPlayerNames(), args[0]);
        }
        return CommandManager.emptyList;
    }
}
