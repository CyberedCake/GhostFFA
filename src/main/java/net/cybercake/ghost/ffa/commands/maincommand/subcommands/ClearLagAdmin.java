package net.cybercake.ghost.ffa.commands.maincommand.subcommands;

import net.cybercake.ghost.ffa.commands.maincommand.CommandManager;
import net.cybercake.ghost.ffa.commands.maincommand.SubCommand;
import net.cybercake.ghost.ffa.Main;
import net.cybercake.ghost.ffa.repeatingtasks.ClearLagTask;
import net.cybercake.ghost.ffa.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;

public class ClearLagAdmin extends SubCommand {

    public ClearLagAdmin() {
        super("clearlagadmin", "ghostffa.subcommand.clearlagadmin", "Set the clearlag time or set to instant clearlag.", "/ghostffa clearlagadmin now", new String[]{"clearlag", "cl"});
    }

    @Override
    public void perform(CommandSender sender, String[] args, Command command) {
        if(args.length < 2) {
            sender.sendMessage(Utils.chat("&cInvalid usage! &7/ghostffa clearlagadmin now"));
        }else if(args.length == 2) {
            if(!args[1].equals("now")) { sender.sendMessage(Utils.chat("&cUnknown argument: &8" + args[1])); }


            try {
                int itemsCleared = ClearLagTask.clearGroundItems();
                sender.sendMessage(Utils.chat("&fYou have cleared &b" + itemsCleared + " &fground items manually!"));
                Main.logInfo("(ClearLag) Cleared " + itemsCleared + " ground items, manually triggered by " + sender.getName());
            } catch (Exception e) {
                Main.logError("An error occurred whilst clearing items manually, by " + sender.getName());
                Main.logError(" ");
                Main.logError("Stack trace below:");
                Utils.printBetterStackTrace(e);
            }
        }
    }

    @Override
    public List<String> tab(CommandSender sender, String[] args) {
        if(args.length < 3) {
            return CommandManager.createReturnList(Arrays.asList("now"), args[1]);
        }
        return CommandManager.emptyList;
    }
}
