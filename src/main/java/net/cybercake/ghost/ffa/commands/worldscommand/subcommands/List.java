package net.cybercake.ghost.ffa.commands.worldscommand.subcommands;

import net.cybercake.ghost.ffa.commands.worldscommand.CommandManager;
import net.cybercake.ghost.ffa.commands.worldscommand.SubCommand;
import net.cybercake.ghost.ffa.utils.Utils;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.StringJoiner;

public class List extends SubCommand {

    public List() {
        super("list", "ghostffa.worlds.list", "Provides a list of all active worlds.", "/worlds list", new String[]{"worldlist"});
    }

    @Override
    public void perform(CommandSender sender, String[] args, Command command) {
        sender.sendMessage(" ");
        sender.sendMessage(Utils.chat("&a&lALL ACTIVE WORLDS:"));
        if(sender instanceof Player) {
            sender.sendMessage(Utils.chat("&7&oClick a world to teleport to it!"));
        }
        sender.sendMessage(" ");
        for(World world : Bukkit.getWorlds()) {
            TextComponent component = new TextComponent(Utils.chat("&f❖ &b" + world.getName()));
            StringJoiner joiner = new StringJoiner(", ");
            ArrayList<String> getPlayer = new ArrayList<>();
            for(Player player : world.getPlayers()) {
                getPlayer.add(player.getName());
            }
            getPlayer.forEach(item -> joiner.add(item));
            component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(Utils.chat("&fPlayers &e(" + getPlayer.size() + "&e)&f: &f" + joiner + "\n\n&6Click here to warp to &b" + world.getName() + "&6!"))));
            component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/world teleport " + world.getName()));
            sender.sendMessage(component);
        }
        sender.sendMessage(" ");
    }

    @Override
    public java.util.List<String> tab(CommandSender sender, String[] args) {
        return CommandManager.emptyList;
    }
}
