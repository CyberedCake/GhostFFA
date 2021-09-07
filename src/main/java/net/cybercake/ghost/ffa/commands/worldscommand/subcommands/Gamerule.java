package net.cybercake.ghost.ffa.commands.worldscommand.subcommands;

import net.cybercake.ghost.ffa.commands.maincommand.CommandManager;
import net.cybercake.ghost.ffa.commands.worldscommand.SubCommand;
import net.cybercake.ghost.ffa.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Gamerule extends SubCommand {

    public Gamerule() {
        super("gamerule", "ghostffa.worlds.gamerule", "Change a gamerule of a world.", "/worlds gamerule <gamerule> [value] [world]", "gamerules", "rule");
    }

    @Override
    public void perform(CommandSender sender, String[] args, Command command) {
        if(args.length == 1) {
            Utils.commandStatus(sender, Utils.Status.FAILED, "Invalid arguments"); return;
        }else if(args.length == 2) {
            if(!(sender instanceof Player)) {
                Utils.commandStatus(sender, Utils.Status.FAILED, "Invalid arguments"); return;
            }
            if(GameRule.getByName(args[1]) == null) {
                Utils.commandStatus(sender, Utils.Status.FAILED, "Invalid gamerule"); return;
            }
            Player player = (Player) sender;
            Utils.commandStatus(sender, Utils.Status.INFO, "Gamerule &b" + args[1] + " &fis currently &e" + player.getWorld().getGameRuleValue(GameRule.getByName(args[1])) + " &fin world &a" + player.getWorld().getName());

        }else if(args.length == 3) {
            if(!(sender instanceof Player)) {
                Utils.commandStatus(sender, Utils.Status.FAILED, "Please specify a world"); return;
            }
            if(GameRule.getByName(args[1]) == null) {
                Utils.commandStatus(sender, Utils.Status.FAILED, "Invalid gamerule"); return;
            }

            Player player = (Player) sender;

            if(stringToGamerule(args[1]).getType() == Boolean.class && !(args[2].equals("true") || args[2].equals("false"))) {
                Utils.commandStatus(sender, Utils.Status.FAILED, "Expected boolean (true/false), but found &b" + args[2]); return;
            }
            if(stringToGamerule(args[1]).getType() == Integer.class && !(Utils.isInteger(args[2]))) {
                Utils.commandStatus(sender, Utils.Status.FAILED, "Expected integer, but found &b" + args[2]); return;
            }

            player.getWorld().setGameRuleValue(args[1], args[2]);
            Utils.commandStatus(sender, Utils.Status.SUCCESS, "&fSet gamerule &b" + args[1] + " &fto &e" + args[2] + " &fin world &a" + player.getWorld().getName());
        }else if(args.length >= 4) {
            if(GameRule.getByName(args[1]) == null) {
                Utils.commandStatus(sender, Utils.Status.FAILED, "Invalid gamerule"); return;
            }
            if(stringToGamerule(args[1]).getType() == Boolean.class && !(args[2].equals("true") || args[2].equals("false"))) {
                Utils.commandStatus(sender, Utils.Status.FAILED, "Expected boolean (true/false), but found &b" + args[2]); return;
            }
            if(stringToGamerule(args[1]).getType() == Integer.class && !(Utils.isInteger(args[2]))) {
                Utils.commandStatus(sender, Utils.Status.FAILED, "Expected integer, but found &b" + args[2]); return;
            }
            if(!Bukkit.getWorlds().contains(Bukkit.getWorld(args[3]))) {
                Utils.commandStatus(sender, Utils.Status.FAILED, "Invalid world, must be an active and valid world"); return;
            }

            Bukkit.getWorld(args[3]).setGameRuleValue(args[1], args[2]);
            Utils.commandStatus(sender, Utils.Status.SUCCESS, "&fSet gamerule &b" + args[1] + " &fto &e" + args[2] + " &fin world &a" + args[3]);
        }
    }

    @Override
    public List<String> tab(CommandSender sender, String[] args) {
        if(args.length == 2) {
            return CommandManager.createReturnList(getGamerules(), args[1]);
        }else if(args.length == 3) {
            if(!getGamerules().contains(args[1])) {
                return CommandManager.emptyList;
            }

            if(stringToGamerule(args[1]).getType() == Boolean.class) {
                return CommandManager.createReturnList(Arrays.asList("true", "false"), args[2]);
            }
        }else if(args.length == 4) {
            if(stringToGamerule(args[1]).getType() == Boolean.class && !(args[2].equals("true") || args[2].equals("false"))) {
                return CommandManager.emptyList;
            }
            if(stringToGamerule(args[1]).getType() == Integer.class && !(Utils.isInteger(args[2]))) {
                return CommandManager.emptyList;
            }
            return net.cybercake.ghost.ffa.commands.worldscommand.CommandManager.createReturnList(net.cybercake.ghost.ffa.commands.worldscommand.CommandManager.getWorldNames(args[3]), args[3]);
        }
        return CommandManager.emptyList;
    }

    public static GameRule<?> stringToGamerule(String original) {
        for(GameRule<?> gamerule : GameRule.values()) {
            if(gamerule.getName().equalsIgnoreCase(original)) {
                return gamerule;
            }
        }
        return null;
    }

    public static ArrayList<String> getGamerules() {
        ArrayList<String> gameRules = new ArrayList<>();
        for(GameRule<?> gamerule : GameRule.values()) {
            gameRules.add(gamerule.getName());
        }
        return gameRules;
    }
}
