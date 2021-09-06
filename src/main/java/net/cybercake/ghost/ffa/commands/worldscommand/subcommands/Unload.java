package net.cybercake.ghost.ffa.commands.worldscommand.subcommands;

import net.cybercake.ghost.ffa.Main;
import net.cybercake.ghost.ffa.commands.worldscommand.CommandManager;
import net.cybercake.ghost.ffa.commands.worldscommand.SubCommand;
import net.cybercake.ghost.ffa.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class Unload extends SubCommand {

    public Unload() {
        super("unload", "ghostffa.worlds.unload", "Unloads a specific world.", "/worlds unload <world>", "");
    }

    @Override
    public void perform(CommandSender sender, String[] args, Command command) {
        if(args.length == 1) {
            Utils.commandStatus(sender, Utils.Status.FAILED, "Invalid arguments"); return;
        }
        if(!Bukkit.getWorlds().contains(Bukkit.getWorld(args[1]))) {
            Utils.commandStatus(sender, Utils.Status.FAILED, "Invalid world, must be an active and valid world"); return;
        }
        if(Main.getMainWorld().equals(Bukkit.getWorld(args[1]))) {
            Utils.commandStatus(sender, Utils.Status.FAILED, "Cannot unload the main server world"); return;
        }

        try {
            Bukkit.unloadWorld(args[1], true);
            Utils.commandStatus(sender, Utils.Status.SUCCESS, "&fSuccessfully unloaded the world named &b" + args[1]);
        } catch (Exception exception) {
            Utils.error(sender, "whilst trying to unload world " + args[1] + " by {name}", exception);
        }
    }

    @Override
    public List<String> tab(CommandSender sender, String[] args) {
        if(args.length == 2) {
            ArrayList<String> worlds = new ArrayList<>();
            for(World world : Bukkit.getWorlds()) {
                worlds.add(world.getName());
            }
            return worlds;
        }
        return CommandManager.emptyList;
    }
}