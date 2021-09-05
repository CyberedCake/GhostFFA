package net.cybercake.ghost.ffa.commands.maincommand.subcommands;

import net.cybercake.ghost.ffa.Main;
import net.cybercake.ghost.ffa.commands.maincommand.CommandManager;
import net.cybercake.ghost.ffa.commands.maincommand.SubCommand;
import net.cybercake.ghost.ffa.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

public class ServerUptime extends SubCommand {

    public ServerUptime() {
        super("uptime", "ghostffa.subcommand.uptime", "View the server's uptime", "/ghostffa uptime [seconds]", new String[]{"serveruptime", "online"});
    }

    @Override
    public void perform(CommandSender sender, String[] args, Command command) {
        if(args.length > 1 && args[1].equals("seconds")) {
            Utils.commandStatus(sender, Utils.Status.INFO, "The server has been online for &b" + Utils.formatLong(Utils.getUnix()-Main.unixStarted) + " &bseconds");
            return;
        }
        Utils.commandStatus(sender, Utils.Status.INFO, "The server has been online for &b" + Utils.getBetterTimeFromLongs(Utils.getUnix(), Main.unixStarted, false));
    }

    @Override
    public List<String> tab(CommandSender sender, String[] args) {
        if(args.length < 1) {
            return CommandManager.createReturnList(Collections.singletonList("seconds"), args[1]);
        }
        return CommandManager.emptyList;
    }
}
