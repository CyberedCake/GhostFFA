package net.cybercake.ghost.ffa.commands.worldscommand.subcommands;

import net.cybercake.ghost.ffa.commands.worldscommand.SubCommand;
import net.cybercake.ghost.ffa.commands.worldscommand.CommandManager;
import net.cybercake.ghost.ffa.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

public class Help extends SubCommand {

    public Help() { super("help", "", "Prints this help message.", "/worlds help", new String[]{"?", "info"}); }

    @Override
    public void perform(CommandSender sender, String[] args, Command command) {
        // Overridden and moved to main CommandManager class
        sender.sendMessage(Utils.chat("&cAn error occurred!"));
    }

    @Override
    public List<String> tab(CommandSender sender, String[] args) {
        return CommandManager.emptyList;
    }
}

