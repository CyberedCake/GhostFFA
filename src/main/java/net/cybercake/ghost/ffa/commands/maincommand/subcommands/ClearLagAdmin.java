package net.cybercake.ghost.ffa.commands.maincommand.subcommands;

import net.cybercake.ghost.ffa.commands.maincommand.CommandManager;
import net.cybercake.ghost.ffa.commands.maincommand.SubCommand;
import net.cybercake.ghost.ffa.Main;
import net.cybercake.ghost.ffa.repeatingtasks.ClearLagTask;
import net.cybercake.ghost.ffa.utils.Utils;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class ClearLagAdmin extends SubCommand {

    public ClearLagAdmin() {
        super("clearlagnow", "ghostffa.subcommand.clearlagnow", "Set the clearlag time or set to instant clearlag.", "/ghostffa clearlagnow", new String[]{"clearlag", "cl", "clearlagadmin"});
    }

    @Override
    public void perform(CommandSender sender, String[] args, Command command) {
        try {
            int itemsCleared = ClearLagTask.clearGroundItems();
            Utils.commandStatus(sender, Utils.Status.INFO, "&fYou have cleared &b" + itemsCleared + " &fground items manually");
            if(sender instanceof Player) {
                Player player = (Player) sender;
                player.playSound(player.getLocation(), Sound.ITEM_FIRECHARGE_USE, 1F, 1F);
            }
            Main.logInfo("(ClearLag) Cleared " + itemsCleared + " ground items, manually triggered by " + sender.getName());
        } catch (Exception exception) {
            Utils.error(sender, "whilst trying to clear dropped items manually (triggered by {name})", exception);
        }
    }

    @Override
    public List<String> tab(CommandSender sender, String[] args) {
        return CommandManager.emptyList;
    }
}
