package net.cybercake.ghost.ffa.commands.admincommands;

import net.cybercake.ghost.ffa.Main;
import net.cybercake.ghost.ffa.commands.maincommand.CommandManager;
import net.cybercake.ghost.ffa.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Loop implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if(args.length < 3) {
            Utils.commandStatus(sender, Utils.Status.FAILED, "Invalid arguments"); return true;
        } if(!args[0].equals("")) {
            if(!Utils.isInteger(args[0])) { Utils.commandStatus(sender, Utils.Status.FAILED, "Invalid integer"); return true; }
            if(!Utils.isBetweenEquals(Integer.parseInt(args[0]), 1, 10000)) { Utils.commandStatus(sender, Utils.Status.FAILED, "Integer too big or small"); return true;}
        } if(!args[1].equals("")) {
            if(!Utils.isInteger(args[1])) { Utils.commandStatus(sender, Utils.Status.FAILED, "Invalid integer"); return true; }
            if(!Utils.isBetweenEquals(Integer.parseInt(args[1]), 0, 1000)) { Utils.commandStatus(sender, Utils.Status.FAILED, "Integer too big or small"); return true;}
        } if(!(sender instanceof Player) && !args[2].startsWith("/")) {
            Utils.commandStatus(sender, Utils.Status.FAILED, "Console cannot repeat chat actions!"); return true;
        }

        int currentDelay = Integer.parseInt(args[1]);
        for(int i=0; i<Integer.parseInt(args[0]); i++) {
            Bukkit.getScheduler().runTaskLater(Main.getPlugin(), () -> {
                if(args[2].startsWith("/")) {
                    Utils.performCommand(sender, Utils.getStringFromArguments(2, args));
                }else{
                    Player player = (Player) sender;
                    player.chat(Utils.getStringFromArguments(2, args));
                }
            }, currentDelay);
            currentDelay = currentDelay + Integer.parseInt(args[1]);
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if(args.length <= 1) {
            return CommandManager.createReturnList(CommandManager.getIntegers(args[0], 1, 10000), args[0]);
        }else if(args.length == 2) {
            return CommandManager.createReturnList(CommandManager.getIntegers(args[1], 1, 1000), args[1]);
        }else if(args.length == 3) {
            if(!args[2].startsWith("/")) return CommandManager.emptyList;

            ArrayList<String> commands = new ArrayList<>();
            for(Command cmd : Bukkit.getCommandMap().getKnownCommands().values()) {
                if(cmd.getName().contains(":")) continue;

                commands.add("/" + cmd.getName());
            }
            return CommandManager.createReturnListSearch(commands, args[2]);
        }
        return CommandManager.emptyList;
    }
}
