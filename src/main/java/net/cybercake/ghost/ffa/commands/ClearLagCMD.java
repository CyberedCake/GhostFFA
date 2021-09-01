package net.cybercake.ghost.ffa.commands;

import net.cybercake.ghost.ffa.commands.maincommand.CommandManager;
import net.cybercake.ghost.ffa.repeatingtasks.ClearLagTask;
import net.cybercake.ghost.ffa.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ClearLagCMD implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage(Utils.chat("&fClear Lag will clear dropped items in &b" + Utils.getBetterTimeFromLongs(ClearLagTask.clearLagInterval, ClearLagTask.currentInterval, false)));
        }
        Player player = (Player) sender;
        Utils.commandStatus(player, Utils.Status.INFO, "&fDropped items cleared in &b" + Utils.getBetterTimeFromLongs(ClearLagTask.clearLagInterval, ClearLagTask.currentInterval, false));

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return CommandManager.emptyList;
    }
}
