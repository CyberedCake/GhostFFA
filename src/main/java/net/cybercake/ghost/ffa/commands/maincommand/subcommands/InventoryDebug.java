package net.cybercake.ghost.ffa.commands.maincommand.subcommands;

import net.cybercake.ghost.ffa.commands.maincommand.CommandManager;
import net.cybercake.ghost.ffa.commands.maincommand.SubCommand;
import net.cybercake.ghost.ffa.menus.kits.KitViewer;
import net.cybercake.ghost.ffa.menus.kits.VirtualKitRoom;
import net.cybercake.ghost.ffa.utils.ItemUtils;
import net.cybercake.ghost.ffa.utils.Utils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class InventoryDebug extends SubCommand {

    public InventoryDebug() {
        super("inventorydebug", "ghostffa.subcommand.inventorydebug", "Provides the developer with useful inventory & GUI knowledge", "/ghostffa inventorydebug", new String[]{""});
    }

    @Override
    public void perform(CommandSender sender, String[] args, Command command) {
        if(sender instanceof Player) {
            Player player = (Player) sender;

            if(args.length == 1) {
                sendInvDebug(player, player);
            }else{
                Player target = Bukkit.getPlayerExact(args[1]);
                if(target == null) { sender.sendMessage(Utils.chat("&cWe could not find a player by the name: &8" + args[1])); return; }

                sendInvDebug(player, target);
            }
        }else{
            if(args.length == 1) {
                sender.sendMessage(Utils.chat("&cInvalid usage! &7/ghostffa inventorydebug <player>"));
            }else{
                Player target = Bukkit.getPlayerExact(args[1]);
                if(target == null) { sender.sendMessage(Utils.chat("&cWe could not find a player by the name: &8" + args[1])); return; }

                sendInvDebug(sender, target);
            }
        }
    }

    private void sendInvDebug(CommandSender to, Player dataFrom) {
        to.sendMessage(Utils.getSeperator(ChatColor.BLUE));
        Utils.sendCenteredMessage(to, "&d&lINVENTORY DEBUG");
        to.sendMessage(Utils.chat(""));
        if(to.equals(dataFrom)) {
            to.sendMessage(Utils.chat("&fPlayer"));
        }else{
            to.sendMessage(Utils.chat("&fPlayer &e(" + dataFrom.getName() + "&e)"));
        }
        to.sendMessage(Utils.chat("  &8> &fGUIs"));
        to.sendMessage(Utils.chat("      &8> &bCurrent Menu: &7" + ItemUtils.currentMenu.get(dataFrom.getName())));
        to.sendMessage(Utils.chat("      &8> &bCurrent Cooldown: &7" + ItemUtils.invClickCooldown.get(dataFrom.getName())));
        to.sendMessage(Utils.chat("      &8> &bEditing Kit: &7#" + KitViewer.currentKit.get(dataFrom.getName())));
        try {
            to.sendMessage(Utils.chat("      &8> &bCurrent Kit Room Category: &7#" + VirtualKitRoom.currentCategory.get(dataFrom.getName()) + " &8(slot" + VirtualKitRoomAdmin.getSlotFromCategory(VirtualKitRoom.currentCategory.get(dataFrom.getName())) + "&8)"));
        } catch (Exception exception) {
            to.sendMessage(Utils.chat("      &8> &bCurrent Kit Room Category: &7null"));
        }
        to.sendMessage(Utils.getSeperator(ChatColor.BLUE));
    }

    @Override
    public List<String> tab(CommandSender sender, String[] args) {
        if(args.length == 2) {
            return CommandManager.createReturnList(CommandManager.getPlayerNames(), args[1]);
        }
        return CommandManager.emptyList;
    }
}
