package net.cybercake.ghost.ffa.commands.worldscommand.subcommands;

import net.cybercake.ghost.ffa.Main;
import net.cybercake.ghost.ffa.commands.worldscommand.CommandManager;
import net.cybercake.ghost.ffa.commands.worldscommand.SubCommand;
import net.cybercake.ghost.ffa.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Teleport extends SubCommand {

    public Teleport() {
        super("tp", "ghostffa.worlds.teleport", "Teleports you or another player to a world.", "/worlds tp <world> [player]", new String[]{"teleport"});
    }

    @Override
    public void perform(CommandSender sender, String[] args, Command command) {
        if(!(sender instanceof Player)) {
            Main.logError("Only players can execute this command!"); return;
        }

        Player player = (Player) sender;

        if(args.length == 1) {
            Utils.commandStatus(player, Utils.Status.FAILED, "Invalid arguments");
        }else if(args.length == 2) {
            World world = Bukkit.getWorld(args[1]);
            if(world == null) {
                Utils.commandStatus(player, Utils.Status.FAILED, "Invalid world, must be an active and valid world"); return;
            }

            teleportPlayers(player, player, world);
            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1F, 1F);
        }else if(args.length == 3) {
            World world = Bukkit.getWorld(args[1]);
            if(world == null) {
                Utils.commandStatus(player, Utils.Status.FAILED, "Invalid world, must be an active and valid world"); return;
            }
            Player target = Bukkit.getPlayerExact(args[2]);
            if(target == null) {
                Utils.commandStatus(player, Utils.Status.FAILED, "Invalid online player"); return;
            }

            teleportPlayers(player, target, world);
        }
    }

    public static void teleportPlayers(Player msgTo, Player teleportWho, World worldSpawn) {
        teleportWho.teleport(worldSpawn.getSpawnLocation());
        Utils.commandStatus(msgTo, Utils.Status.INFO, "You teleported " + (msgTo == teleportWho ? "yourself" : teleportWho.getName()) + " to &b" + worldSpawn.getName() + "&f's spawn");
    }

    @Override
    public List<String> tab(CommandSender sender, String[] args) {
        if(args.length == 2) {
            ArrayList<String> worlds = new ArrayList<>();
            for(World world : Bukkit.getWorlds()) {
                worlds.add(world.getName());
            }
            return CommandManager.createReturnList(worlds, args[1]);
        }else if(args.length == 3) {
            return CommandManager.createReturnList(CommandManager.getPlayerNames(), args[2]);
        }
        return CommandManager.emptyList;
    }
}
