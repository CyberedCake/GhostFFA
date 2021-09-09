package net.cybercake.ghost.ffa.commands.defaultcommands;

import net.cybercake.ghost.ffa.Main;
import net.cybercake.ghost.ffa.commands.worldscommand.CommandManager;
import net.cybercake.ghost.ffa.utils.Utils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.regex.Pattern;

public class Rename implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player)) {
            Main.logError("Only players can execute this command!"); return true;
        }

        Player player = (Player) sender;

        String message = Utils.getStringFromArguments(0, args);
        if(message.length() >= 1) {
            message = message.substring(0, message.length()-1);
        }

        if(args.length == 0) {
            Utils.commandStatus(player, Utils.Status.FAILED, "Invalid arguments"); return true;
        }else if(args[0].equals("clear") || args[0].equals("reset")) {
            if(player.getInventory().getItemInMainHand().getItemMeta().getDisplayName().equalsIgnoreCase(player.getInventory().getItemInMainHand().getI18NDisplayName())) {
                Utils.commandStatus(player, Utils.Status.FAILED, "Your item does not currently have a name!"); return true;
            }

            ItemStack playersHeldItem = player.getInventory().getItemInMainHand();
            ItemMeta playersHeldItemMeta = playersHeldItem.getItemMeta();
            playersHeldItemMeta.displayName(Component.text(ChatColor.WHITE + playersHeldItem.getI18NDisplayName()));
            playersHeldItem.setItemMeta(playersHeldItemMeta);

            player.getInventory().setItemInMainHand(playersHeldItem);

            Utils.commandStatus(player, Utils.Status.SUCCESS, "&fReset your held item's name back to normal");
            return true;
        }else if(message.length() >= 40) {
            Utils.commandStatus(player, Utils.Status.FAILED, "Item name cannot be over 40 characters"); return true;
        }else if(!message.matches("[a-zA-Z0-9&!@#$%^():~\";'-=+_ ]*")) {
            Utils.commandStatus(player, Utils.Status.FAILED, "Item name contains invalid characters"); return true;
        }

        ItemStack playersHeldItem = player.getInventory().getItemInMainHand();
        ItemMeta playersHeldItemMeta = playersHeldItem.getItemMeta();
        playersHeldItemMeta.displayName(Utils.component("&d" + message));
        playersHeldItem.setItemMeta(playersHeldItemMeta);

        player.getInventory().setItemInMainHand(playersHeldItem);

        Utils.commandStatus(player, Utils.Status.SUCCESS, "&fSet your held item's name to &b" + message);
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return CommandManager.emptyList;
    }
}
