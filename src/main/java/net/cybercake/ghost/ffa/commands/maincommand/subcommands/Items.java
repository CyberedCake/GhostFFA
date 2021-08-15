package net.cybercake.ghost.ffa.commands.maincommand.subcommands;

import net.cybercake.ghost.ffa.commands.maincommand.CommandManager;
import net.cybercake.ghost.ffa.commands.maincommand.SubCommand;
import net.cybercake.ghost.ffa.Main;
import net.cybercake.ghost.ffa.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class Items extends SubCommand {

    public Items() {
        super("items", "ghostffa.subcommand.items", "Allows the developer to quickly and easily test items", "/ghostffa items", new String[]{""});
    }

    @Override
    public void perform(CommandSender sender, String[] args, Command command) {
        if(sender instanceof Player) {
            Player player = (Player) sender;

            player.sendMessage(Utils.chat("&cInvalid usage! &7" + this.getUsage()));
        }else{
            Main.logError("Only players can execute this sub-command!");
        }
    }

    @Override
    public List<String> tab(CommandSender sender, String[] args) {
        return CommandManager.emptyList;
    }
}
