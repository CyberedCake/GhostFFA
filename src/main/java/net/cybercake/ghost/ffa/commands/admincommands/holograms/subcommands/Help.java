package net.cybercake.ghost.ffa.commands.admincommands.holograms.subcommands;

import net.cybercake.ghost.ffa.commands.admincommands.holograms.CommandManager;
import net.cybercake.ghost.ffa.commands.admincommands.holograms.SubCommand;
import net.cybercake.ghost.ffa.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

public class Help extends SubCommand {

    public Help() { super("help", "", "Prints this help message.", "/holograms help", new String[]{"?", "info"}); }

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

