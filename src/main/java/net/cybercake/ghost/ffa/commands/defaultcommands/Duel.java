package net.cybercake.ghost.ffa.commands.defaultcommands;

import net.cybercake.ghost.ffa.Main;
import net.cybercake.ghost.ffa.commands.maincommand.CommandManager;
import net.cybercake.ghost.ffa.menus.duels.DuelMain;
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

public class Duel implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player)) {
            Main.logError("Only players can execute this command!"); return true;
        }

        Player player = (Player) sender;

        if(args.length == 0) {
            Utils.commandStatus(sender, Utils.Status.FAILED, "Invalid arguments"); return true;
        }
        Player target = Bukkit.getPlayerExact(args[0]);
        if(target == null) {
            Utils.commandStatus(sender, Utils.Status.FAILED, "Invalid online player"); return true;
        }
        if(target == player) {
            Utils.commandStatus(sender, Utils.Status.FAILED, "You cannot duel yourself"); return true;
        }
        DuelMain.openMenu(player, target);

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if(args.length <= 1) {
            return CommandManager.createReturnList(CommandManager.getPlayerNames(), args[0]);
        }
        return CommandManager.emptyList;
    }
}
