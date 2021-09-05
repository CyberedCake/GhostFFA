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

import java.util.List;

public class Fly implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length == 0) {
            if(!(sender instanceof Player)) {
                Utils.commandStatus(sender, Utils.Status.FAILED, "Invalid arguments"); return true;
            }
            Player player = (Player) sender;
            toggleFlight(player, player);
        }else if(args.length >= 1) {
            Player target = Bukkit.getPlayerExact(args[0]);
            if(target == null) {
                Utils.commandStatus(sender, Utils.Status.FAILED, "Invalid online player");
                return true;
            }
            toggleFlight(sender, target);
        }

        return true;
    }

    public static void toggleFlight(CommandSender msgTo, Player toggleFor) {
        if(!toggleFor.getAllowFlight()) {
            toggleFor.setAllowFlight(true);
            Utils.commandStatus(msgTo, Utils.Status.INFO, "You set &b" + (msgTo == toggleFor ? "your" : toggleFor.getName() + "'s") + " &fflight mode to true");
        }else if(toggleFor.getAllowFlight()) {
            toggleFor.setAllowFlight(false);
            Utils.commandStatus(msgTo, Utils.Status.INFO, "You set &b" + (msgTo == toggleFor ? "your" : toggleFor.getName() + "'s") + " &fflight mode to false");
        }
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if(args.length == 0) {
            return CommandManager.createReturnList(CommandManager.getPlayerNames(), args[0]);
        }
        return CommandManager.emptyList;
    }
}
