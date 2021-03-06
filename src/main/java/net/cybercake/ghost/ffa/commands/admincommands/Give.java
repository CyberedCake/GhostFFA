package net.cybercake.ghost.ffa.commands.admincommands;

import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTContainer;
import de.tr7zw.changeme.nbtapi.NBTItem;
import de.tr7zw.changeme.nbtapi.NbtApiException;
import net.cybercake.ghost.ffa.Main;
import net.cybercake.ghost.ffa.commands.maincommand.CommandManager;
import net.cybercake.ghost.ffa.utils.ItemUtils;
import net.cybercake.ghost.ffa.utils.Utils;
import org.bukkit.Bukkit;
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

public class Give implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length == 0) {
            Utils.commandStatus(sender, Utils.Status.FAILED, "Invalid arguments");
        }else if(args.length == 1) {
            if(!ItemUtils.mcItems().contains(ItemUtils.toKey(args[0]))) {
                Utils.commandStatus(sender, Utils.Status.FAILED, "Invalid MC item key");
            }else{
                if(!(sender instanceof Player)) {
                    Utils.commandStatus(sender, Utils.Status.FAILED, "Invalid arguments"); return true;
                }
                Player player = (Player) sender;
                giveItemToPlayer(player, player, new ItemStack(ItemUtils.mcKeyToMaterial(args[0]), 1));
            }
        }else if(args.length == 2) {
            if(!ItemUtils.mcItems().contains(ItemUtils.toKey(args[0]))) {
                Utils.commandStatus(sender, Utils.Status.FAILED, "Invalid MC item");
            }else if(!Utils.isInteger(args[1])) {
                Utils.commandStatus(sender, Utils.Status.FAILED, "Invalid integer");
            }else if(Integer.parseInt(args[1]) < 1 || Integer.parseInt(args[1]) > 64) {
                Utils.commandStatus(sender, Utils.Status.FAILED, "Integer must be between &b1 - 64");
            }else{
                if(!(sender instanceof Player)) {
                    Utils.commandStatus(sender, Utils.Status.FAILED, "Invalid arguments"); return true;
                }
                Player player = (Player) sender;
                giveItemToPlayer(player, player, new ItemStack(ItemUtils.mcKeyToMaterial(args[0]), Integer.parseInt(args[1])));
            }
        }else if(args.length == 3) {
            Player target = Bukkit.getPlayerExact(args[2]);
            if(target == null) {
                Utils.commandStatus(sender, Utils.Status.FAILED, "Invalid online player");
            }else if(!ItemUtils.mcItems().contains(ItemUtils.toKey(args[0]))) {
                Utils.commandStatus(sender, Utils.Status.FAILED, "Invalid MC item");
            }else if(!Utils.isInteger(args[1])) {
                Utils.commandStatus(sender, Utils.Status.FAILED, "Invalid integer");
            }else if(Integer.parseInt(args[1]) < 1 || Integer.parseInt(args[1]) > 64) {
                Utils.commandStatus(sender, Utils.Status.FAILED, "Integer must be between &b1 - 64");
            }else{
                giveItemToPlayer(sender, target, new ItemStack(ItemUtils.mcKeyToMaterial(args[0]), Integer.parseInt(args[1])));
            }
        }else if(args.length >= 4) {
            Player target = Bukkit.getPlayerExact(args[2]);
            if(target == null) {
                Utils.commandStatus(sender, Utils.Status.FAILED, "Invalid online player");
            }else if(!ItemUtils.mcItems().contains(ItemUtils.toKey(args[0]))) {
                Utils.commandStatus(sender, Utils.Status.FAILED, "Invalid MC item");
            }else if(!Utils.isInteger(args[1])) {
                Utils.commandStatus(sender, Utils.Status.FAILED, "Invalid integer");
            }else if(Integer.parseInt(args[1]) < 1 || Integer.parseInt(args[1]) > 64) {
                Utils.commandStatus(sender, Utils.Status.FAILED, "Integer must be between &b1 - 64");
            }else{
                try {
                    ItemStack itemStack = new ItemStack(ItemUtils.mcKeyToMaterial(args[0]), Integer.parseInt(args[1]));
                    NBTItem nbtItem = new NBTItem(itemStack);
                    nbtItem.mergeCompound(new NBTContainer(Utils.getStringFromArguments(3, args)));

                    itemStack = nbtItem.getItem();

                    itemStack.setAmount(Integer.parseInt(args[1]));

                    giveItemToPlayer(sender, target, itemStack);
                } catch (NbtApiException exception) {
                    Utils.commandStatus(sender, Utils.Status.FAILED, "Invalid NBT container"); return true;
                } catch (Exception exception) {
                    Utils.commandStatus(sender, Utils.Status.TECHNICAL_FAULT, "An internal error occurred, sorry");
                    Utils.error(sender, "whilst trying to parse NBT text", exception);
                }
            }
        }


        return true;
    }

    public static void giveItemToPlayer(CommandSender msgTo, Player giveTo, ItemStack itemStack) {
        if(!ItemUtils.hasEnoughRoom(giveTo, itemStack)) {
            Utils.commandStatus(msgTo, Utils.Status.FAILED, (msgTo == giveTo ? "You don't" : giveTo.getName() + " doesn't") + " have enough room for that item!");
        }else{
            Utils.commandStatus(msgTo, Utils.Status.INFO, "You gave &bx" + itemStack.getAmount() + " " + itemStack.getI18NDisplayName() + " &fto " + (msgTo == giveTo ? "yourself" : giveTo.getName()));
            giveTo.getInventory().addItem(itemStack);
        }
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if(args.length == 3) {
            return CommandManager.createReturnList(CommandManager.getPlayerNames(), args[2]);
        }else if (args.length == 2) {
            ArrayList<String> returnList = new ArrayList<>();
            for(int i=1; i<65; i++) {
                if(i <= 9) {
                    returnList.add("0" + i);
                }else{
                    returnList.add(i + "");
                }
            }
            return CommandManager.createReturnList(returnList, args[1]);
        }else if(args.length == 1) {
            return ItemUtils.tabCompleteItem(args[0]);
        }
        return CommandManager.emptyList;
    }
}
