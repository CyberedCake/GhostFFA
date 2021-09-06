package net.cybercake.ghost.ffa.commands.worldscommand.subcommands;

import net.cybercake.ghost.ffa.commands.worldscommand.CommandManager;
import net.cybercake.ghost.ffa.commands.worldscommand.SubCommand;
import net.cybercake.ghost.ffa.utils.Utils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Locale;
import java.util.StringJoiner;

public class List extends SubCommand {

    public List() {
        super("list", "ghostffa.worlds.list", "Provides a list of all active worlds.", "/worlds list [world]", new String[]{"worldlist"});
    }

    @Override
    public void perform(CommandSender sender, String[] args, Command command) {
        if(args.length == 1) {
            sender.sendMessage(Utils.getSeperator(ChatColor.BLUE));
            Utils.sendCenteredMessage(sender, "&d&lWORLDS AND THEIR PLAYERS:");
            for(World world : Bukkit.getWorlds()) {
                StringJoiner joiner = new StringJoiner(", ");
                ArrayList<String> getPlayer = new ArrayList<>();
                for(Player player : world.getPlayers()) {
                    getPlayer.add(player.getName());
                }
                getPlayer.forEach(item -> joiner.add(item));

                TextComponent component = new TextComponent(Utils.chat("&b" + world.getName() + " &e(" + getPlayer.size() + ")&f: " + joiner));

                component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(Utils.chat("&fClick here to view players in world &b" + world.getName() + "&f!"))));
                component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/worlds list " + world.getName()));

                sender.sendMessage(component);
            }
            sender.sendMessage(Utils.getSeperator(ChatColor.BLUE));
        }else if(args.length > 1) {
            if(!Bukkit.getWorlds().contains(Bukkit.getWorld(args[1]))) {
                Utils.commandStatus(sender, Utils.Status.FAILED, "Invalid world, must be an active and valid world"); return;
            }
            sender.sendMessage(Utils.getSeperator(ChatColor.BLUE));
            Utils.sendCenteredMessage(sender, "&d&lWORLD \"" + args[1].toUpperCase(Locale.ROOT) + "\"&d&l'S PLAYERS");

            BaseComponent bigComponentBoy = new TextComponent(Utils.chat(""));
            int index = 0;
            for(Player player : Bukkit.getWorld(args[1]).getPlayers()) {
                TextComponent component = new TextComponent(Utils.chat("&b" + player.getName()));

                component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/stp " + player.getName()));
                component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(Utils.chat("&fClick here to teleport to &b" + player.getName() + "&f!"))));

                TextComponent comma = new TextComponent(Utils.chat("&f, "));

                BaseComponent combined = component;
                if(index == Bukkit.getWorld(args[1]).getPlayers().size()) {
                    combined.addExtra(comma);
                }

                bigComponentBoy.addExtra(combined);

                index++;
            }
            sender.sendMessage(bigComponentBoy);

            sender.sendMessage(Utils.getSeperator(ChatColor.BLUE));
        }
    }

    @Override
    public java.util.List<String> tab(CommandSender sender, String[] args) {
        if(args.length == 2) {
            ArrayList<String> worlds = new ArrayList<>();
            for(World world : Bukkit.getWorlds()) {
                worlds.add(world.getName());
            }
            return CommandManager.createReturnList(worlds, args[1]);
        }
        return CommandManager.emptyList;
    }
}
