package net.cybercake.ghost.ffa.commands.admincommands.worldscommand.subcommands;

import net.cybercake.ghost.ffa.Main;
import net.cybercake.ghost.ffa.commands.admincommands.worldscommand.CommandManager;
import net.cybercake.ghost.ffa.commands.admincommands.worldscommand.SubCommand;
import net.cybercake.ghost.ffa.utils.DataUtils;
import net.cybercake.ghost.ffa.utils.Utils;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class Delete extends SubCommand {

    public Delete() {
        super("delete", "ghostffa.worlds.deleteworld", "Deletes a specific world.", "/worlds delete <worldName>", "remove");
    }

    @Override
    public void perform(CommandSender sender, String[] args, Command command) {
        if (args.length < 2) {
            Utils.commandStatus(sender, Utils.Status.FAILED, "Invalid arguments"); return;
        }

        try {
            if(!CommandManager.worldExist(args[1])) {
                Utils.commandStatus(sender, Utils.Status.FAILED, "Invalid world, must be an active and valid world"); return;
            }
            if(Main.getMainWorld().equals(Bukkit.getWorld(args[1]))) {
                Utils.commandStatus(sender, Utils.Status.FAILED, "Cannot delete the main server world"); return;
            }

            Utils.commandStatus(sender, Utils.Status.INFO, "&7&oDeleting that world... please wait");

            for(Player player : Bukkit.getOnlinePlayers()) {
                if(player.getWorld().equals(Bukkit.getWorld(args[1]))) {
                    player.teleport(SetSpawn.getWorldSpawn(Main.getMainWorld()));
                }
            }

            CommandManager.deleteWorld(Bukkit.getWorld(args[1]).getWorldFolder());

            Bukkit.unloadWorld(Bukkit.getWorld(args[1]), false);

            if(Bukkit.getWorld(args[1]) == null) {
                Utils.commandStatus(sender, Utils.Status.SUCCESS, "&fSuccessfully deleted the world named &b" + args[1]);

                DataUtils.setCustomYml("worlds", "worlds." + args[1], null);
            }else if(Bukkit.getWorld(args[1]) != null) {
                Utils.commandStatus(sender, Utils.Status.FAILED, "Failed to delete the world");
            }
        } catch (Exception exception) {
            Utils.error(sender, "during the world deletion process for {name}", exception);
        }
    }

    @Override
    public List<String> tab(CommandSender sender, String[] args) {
        if(args.length == 2) {
            return net.cybercake.ghost.ffa.commands.admincommands.worldscommand.CommandManager.createReturnList(net.cybercake.ghost.ffa.commands.admincommands.worldscommand.CommandManager.getWorldNames(args[1]), args[1]);

        }
        return CommandManager.emptyList;
    }
}
