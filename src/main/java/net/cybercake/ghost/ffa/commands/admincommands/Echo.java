package net.cybercake.ghost.ffa.commands.admincommands;

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
import java.util.Arrays;
import java.util.List;

public class Echo implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if(args.length < 3) {
            Utils.commandStatus(sender, Utils.Status.FAILED, "Invalid arguments"); return true;
        }
        if(!Utils.checkStrings(Utils.CheckType.equals, args[0], "chat", "actionbar", "title", "subtitle")) {
            Utils.commandStatus(sender, Utils.Status.FAILED, "Invalid message type"); return true;
        }
        Player target = Bukkit.getPlayerExact(args[1]);
        if(args[1].equals("me")) {
            if(!(sender instanceof Player)) {
                Utils.commandStatus(sender, Utils.Status.FAILED, "Console cannot send a message to themselves"); return true;
            }
            target = (Player) sender;
        }
        if(target == null && !(args[1].equals("me"))) {
            Utils.commandStatus(sender, Utils.Status.FAILED, "Invalid online player"); return true;
        }

        String message = Utils.getStringFromArguments(2, args);
        switch (args[0]) {
            case "chat":
                Utils.commandStatus(sender, Utils.Status.INFO, "&fSent a chat message to &b" + target.getName());
                target.sendMessage(Utils.component(message));
                break;
            case "actionbar":
                Utils.commandStatus(sender, Utils.Status.INFO, "&fSent an actionbar message to &b" + target.getName());
                target.sendActionBar(Utils.component(message));
                break;
            case "title":
                Utils.commandStatus(sender, Utils.Status.INFO, "&fSent a title message to &b" + target.getName());
                target.sendTitle(Utils.chat(message), null, 10, 50, 10);
                break;
            case "subtitle":
                Utils.commandStatus(sender, Utils.Status.INFO, "&fSent a subtitle message to &b" + target.getName());
                target.sendTitle(null, Utils.chat(message), 10, 50, 10);
                break;
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if(args.length <= 1) {
            return CommandManager.createReturnList(Arrays.asList("chat", "actionbar", "title", "subtitle"), args[0]);
        }else if(args.length == 2) {
            ArrayList<String> players = CommandManager.getPlayerNames();
            players.add("me");
            return CommandManager.createReturnList(players, args[1]);
        }
        return CommandManager.emptyList;
    }
}
