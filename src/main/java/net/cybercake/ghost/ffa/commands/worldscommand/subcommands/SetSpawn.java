package net.cybercake.ghost.ffa.commands.worldscommand.subcommands;

import net.cybercake.ghost.ffa.Main;
import net.cybercake.ghost.ffa.commands.worldscommand.CommandManager;
import net.cybercake.ghost.ffa.commands.worldscommand.SubCommand;
import net.cybercake.ghost.ffa.utils.DataUtils;
import net.cybercake.ghost.ffa.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SetSpawn extends SubCommand {

    public SetSpawn() {
        super("setspawn", "ghostffa.worlds.setspawn", "Sets a world's spawn to your location.", "/worlds setspawn [<x> <y> <z> <yaw> <pitch> [world]]", "setlocation");
    }

    @Override
    public void perform(CommandSender sender, String[] args, Command command) {
        if(args.length == 1) {
            if(!(sender instanceof Player)) {
                Utils.commandStatus(sender, Utils.Status.FAILED, "Invalid arguments"); return;
            }
            Player player = (Player) sender;
            DataUtils.setCustomYml("worlds", "worlds." + player.getWorld().getName() + ".spawnLocation", player.getLocation());
            Utils.commandStatus(sender, Utils.Status.INFO, "Set this world's spawn to your location");
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1F, 2F);
        }else if(args.length > 1){
            if(Utils.isBetweenEquals(args.length, 2, (sender instanceof Player ? 5 : 6))) {
                Utils.commandStatus(sender, Utils.Status.FAILED, "Failed to parse location, are your arguments correct?"); return;
            } else if(args.length >= (sender instanceof Player ? 6 : 7)) {
                if(!(Utils.isDouble(args[1])) || !(Utils.isDouble(args[2])) || !(Utils.isDouble(args[3])) || !(Utils.isDouble(args[4]) || !(Utils.isDouble(args[5])))) { Utils.commandStatus(sender, Utils.Status.FAILED, "Invalid decimal"); }

                World world;
                if(sender instanceof Player) {
                    Player player = (Player) sender;
                    if(args.length >= 7) {
                        world = Bukkit.getWorld(args[6]);
                        if(world == null) { Utils.commandStatus(sender, Utils.Status.FAILED, "Invalid world, must be an active and valid world"); return; }
                    }else{
                        world = player.getWorld();
                    }
                }else{
                    world = Bukkit.getWorld(args[6]);
                    if(world == null) { Utils.commandStatus(sender, Utils.Status.FAILED, "Invalid world, must be an active and valid world"); return; }
                }

                Location location = new Location(world, Double.parseDouble(args[1]), Double.parseDouble(args[2]), Double.parseDouble(args[3]), Float.parseFloat(args[4]), Float.parseFloat(args[5]));

                DataUtils.setCustomYml("worlds", "worlds." + location.getWorld().getName() + ".spawnLocation", location);
                Utils.commandStatus(sender, Utils.Status.INFO, "&fSet this world's spawn location to: &3x=" + args[1] + " &ey=" + args[2] + " &az=" + args[3] + " &dyaw=" + args[4] + " &6pitch=" + args[5] + " &3world=" + world.getName());

                if(sender instanceof Player) {
                    Player player = (Player) sender;
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1F, 2F);
                }
            }
        }
    }

    @Override
    public List<String> tab(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        if(args.length == 7) {
            return net.cybercake.ghost.ffa.commands.worldscommand.CommandManager.createReturnList(net.cybercake.ghost.ffa.commands.worldscommand.CommandManager.getWorldNames(args[6]), args[6]);
        }else if(args.length == 6) {
            return net.cybercake.ghost.ffa.commands.maincommand.CommandManager.createReturnList(Collections.singletonList("" + Math.round(player.getLocation().getPitch())), args[5]);
        }else if(args.length == 5) {
            return net.cybercake.ghost.ffa.commands.maincommand.CommandManager.createReturnList(Collections.singletonList(Math.round(player.getLocation().getYaw()) + " " + Math.round(player.getLocation().getPitch())), args[4]);
        }else if(args.length == 4) {
            return net.cybercake.ghost.ffa.commands.maincommand.CommandManager.createReturnList(Collections.singletonList("" + Math.round(player.getLocation().getZ())), args[3]);
        }else if(args.length == 3) {
            return net.cybercake.ghost.ffa.commands.maincommand.CommandManager.createReturnList(Collections.singletonList(Math.round(player.getLocation().getY()) + " " + Math.round(player.getLocation().getZ())), args[2]);
        }else if(args.length == 2) {
            return net.cybercake.ghost.ffa.commands.maincommand.CommandManager.createReturnList(Collections.singletonList(Math.round(player.getLocation().getX()) + " " + Math.round(player.getLocation().getY()) + " " + Math.round(player.getLocation().getZ())), args[1]);
        }else if(args.length == 1) {
            return net.cybercake.ghost.ffa.commands.maincommand.CommandManager.createReturnList(Collections.singletonList("setLocation"), args[0]);
        }
        return CommandManager.emptyList;
    }

    public static Location getWorldSpawn(World world) {
        if(DataUtils.getCustomYmlLocation("worlds", "worlds." + world.getName() + ".spawnLocation") == null) {
            DataUtils.setCustomYml("worlds", "worlds." + world.getName() + ".spawnLocation", world.getSpawnLocation());
            return world.getSpawnLocation();
        }
        return DataUtils.getCustomYmlLocation("worlds", "worlds." + world.getName() + ".spawnLocation");
    }

    public static Location getWorldSpawn(String worldName) {
        return getWorldSpawn(Bukkit.getWorld(worldName));
    }
}
