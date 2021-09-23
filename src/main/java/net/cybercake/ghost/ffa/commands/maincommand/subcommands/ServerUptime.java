package net.cybercake.ghost.ffa.commands.maincommand.subcommands;

import net.cybercake.ghost.ffa.Main;
import net.cybercake.ghost.ffa.commands.maincommand.CommandManager;
import net.cybercake.ghost.ffa.commands.maincommand.SubCommand;
import net.cybercake.ghost.ffa.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class ServerUptime extends SubCommand {

    public ServerUptime() {
        super("uptime", "ghostffa.subcommand.uptime", "View the server's uptime", "/ghostffa uptime [seconds]", new String[]{"serveruptime", "online"});
    }

    @Override
    public void perform(CommandSender sender, String[] args, Command command) {
        String uptime = "The server has been online for &b" + Utils.getBetterTimeFromLongs(Utils.getUnix(), Main.unixStarted, false);
        if(args.length > 1) {
            if(args[1].equals("seconds")) {
                Utils.commandStatus(sender, Utils.Status.INFO, "The server has been online for &b" + Utils.formatLong(Utils.getUnix()-Main.unixStarted) + " &bseconds");
            }else if(args[1].equals("minutes")) {
                Utils.commandStatus(sender, Utils.Status.INFO, "The server has been online for &b" + Utils.formatLong((Utils.getUnix()-Main.unixStarted)/60) + " &bminutes");
            }else if(args[1].equals("hours")) {
                Utils.commandStatus(sender, Utils.Status.INFO, "The server has been online for &b" + Utils.formatLong(((Utils.getUnix()-Main.unixStarted)/60)/60) + " &bhours");
            }else if(args[1].equals("days")) {
                Utils.commandStatus(sender, Utils.Status.INFO, "The server has been online for &b" + Utils.formatLong((((Utils.getUnix() - Main.unixStarted) / 60) / 60) / 24) + " &bdays");
            }else{
                Utils.commandStatus(sender, Utils.Status.INFO, uptime);
            }
            return;
        }

        Utils.commandStatus(sender, Utils.Status.INFO, uptime);
    }

    @Override
    public List<String> tab(CommandSender sender, String[] args) {
        if(args.length == 2) {
            return CommandManager.createReturnList(Arrays.asList("seconds", "minutes", "hours", "days"), args[1]);
        }
        return CommandManager.emptyList;
    }
}
