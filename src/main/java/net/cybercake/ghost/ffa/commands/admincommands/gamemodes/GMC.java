package net.cybercake.ghost.ffa.commands.admincommands.gamemodes;

import net.cybercake.ghost.ffa.Main;
import net.cybercake.ghost.ffa.commands.admincommands.Gamemode;
import net.cybercake.ghost.ffa.commands.worldscommand.CommandManager;
import net.cybercake.ghost.ffa.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GMC implements CommandExecutor, TabCompleter {

    private static final GameMode gamemode = GameMode.CREATIVE;
    private static final String gamemodeString = "Creative";

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length == 0) {
            if(!(sender instanceof Player)) {
                Utils.commandStatus(sender, Utils.Status.FAILED, "Invalid arguments"); return true;
            }
            Player player = (Player) sender;
            Gamemode.switchGamemode(player, player, gamemode, gamemodeString);
        }else if(args.length == 1) {
            Player target = Bukkit.getPlayerExact(args[0]);
            if(target == null) {
                Utils.commandStatus(sender, Utils.Status.FAILED, "Invalid online player"); return true;
            }
            Gamemode.switchGamemode(sender, target, gamemode, gamemodeString);
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if(args.length == 1) {
            return CommandManager.createReturnList(CommandManager.getPlayerNames(), args[0]);
        }
        return CommandManager.emptyList;
    }
}
