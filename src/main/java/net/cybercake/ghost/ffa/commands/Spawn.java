package net.cybercake.ghost.ffa.commands;

import net.cybercake.ghost.ffa.Main;
import net.cybercake.ghost.ffa.commands.maincommand.CommandManager;
import net.cybercake.ghost.ffa.utils.DataUtils;
import net.cybercake.ghost.ffa.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Spawn implements CommandExecutor, TabCompleter {

    private final static String spawnUsage = "/spawn setLocation [<x> <y> <z> <yaw> <pitch> <world>]";

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player)) {
            Main.logError("Only players can execute this command!"); return true;
        }

        Player player = (Player) sender;
        try {
            if(args.length >= 1 && args[0].equalsIgnoreCase("setLocation")) {
                if(!player.hasPermission("ghostffa.admin.setspawnlocation")) { Utils.commandStatus(player, Utils.Status.FAILED, "You don't have permission to set a spawn location!"); return true; }

                if(Utils.isBetweenEquals(args.length, 2, 6)) {
                    Utils.commandStatus(player, Utils.Status.FAILED, "Failed to parse location, are your arguments correct?"); return true;
                } else if(args.length >= 7) {
                    if(!(Utils.isDouble(args[1])) || !(Utils.isDouble(args[2])) || !(Utils.isDouble(args[3])) || !(Utils.isDouble(args[4]) || !(Utils.isDouble(args[5])))) { Utils.commandStatus(player, Utils.Status.FAILED, "Invalid decimal"); }

                    World world = Bukkit.getWorld(args[6]);
                    if(world == null) { Utils.commandStatus(player, Utils.Status.FAILED, "Invalid world, must be an active and valid world"); return true; }

                    Location location = new Location(world, Double.parseDouble(args[1]), Double.parseDouble(args[2]), Double.parseDouble(args[3]), Float.parseFloat(args[4]), Float.parseFloat(args[5]));

                    DataUtils.setCustomYml("data", "generic.spawnLocation", location);
                    Utils.commandStatus(player, Utils.Status.SUCCESS, "&fSet spawn to location: &3x=" + args[1] + " &ey=" + args[2] + " &az=" + args[3] + " &dyaw=" + args[4] + " &6pitch=" + args[5] + " &3world=" + args[6]);

                    return true;
                }
                DataUtils.setCustomYml("data", "generic.spawnLocation", player.getLocation());
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_IRON_XYLOPHONE, 1F, 1F);
                Utils.commandStatus(player, Utils.Status.INFO, "Set spawn to your location");
            }
            player.teleport(DataUtils.getCustomYmlLocation("data", "generic.spawnLocation"));
            player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_GENERIC, 1F, 1F);
        } catch (Exception exception) {
            Utils.error(player, "whilst trying to warp {name} to spawn", exception);
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if(sender instanceof Player && sender.hasPermission("ghostffa.admin.setspawnlocation")) {
            Player player = (Player) sender;
            if(args.length == 7) {
                ArrayList<String> allWorlds = new ArrayList<>();
                for(World world : Bukkit.getWorlds()) {
                    allWorlds.add(world.getName());
                }
                return CommandManager.createReturnList(allWorlds, args[6]);
            }else if(args.length == 6) {
                return CommandManager.createReturnList(Collections.singletonList("" + Math.round(player.getLocation().getPitch())), args[5]);
            }else if(args.length == 5) {
                return CommandManager.createReturnList(Collections.singletonList(Math.round(player.getLocation().getYaw()) + " " + Math.round(player.getLocation().getPitch())), args[4]);
            }else if(args.length == 4) {
                return CommandManager.createReturnList(Collections.singletonList("" + Math.round(player.getLocation().getZ())), args[3]);
            }else if(args.length == 3) {
                return CommandManager.createReturnList(Collections.singletonList(Math.round(player.getLocation().getY()) + " " + Math.round(player.getLocation().getZ())), args[2]);
            }else if(args.length == 2) {
                return CommandManager.createReturnList(Collections.singletonList(Math.round(player.getLocation().getX()) + " " + Math.round(player.getLocation().getY()) + " " + Math.round(player.getLocation().getZ())), args[1]);
            }else if(args.length == 1) {
                return CommandManager.createReturnList(Collections.singletonList("setLocation"), args[0]);
            }
        }
        return CommandManager.emptyList;
    }
}
