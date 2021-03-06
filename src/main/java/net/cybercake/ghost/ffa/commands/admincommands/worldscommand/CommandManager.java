package net.cybercake.ghost.ffa.commands.admincommands.worldscommand;

import net.cybercake.ghost.ffa.commands.admincommands.worldscommand.subcommands.*;
import net.cybercake.ghost.ffa.utils.Utils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class CommandManager implements CommandExecutor, TabCompleter {

    // I am going to be using the CommandManager from https://gitlab.com/kodysimpson/command-manager-spigot, though slightly modified

    private final ArrayList<SubCommand> subcommands = new ArrayList<>();
    public static ArrayList<String> emptyList = new ArrayList<>();

    private final static String pluginPermission = "ghostffa.worlds";
    private final static String pluginTitle = "WORLDS";
    private final static String noPermissionMsg = "&cYou don't have permission to use this";

    public CommandManager() {
        subcommands.add(new Help());
        subcommands.add(new net.cybercake.ghost.ffa.commands.admincommands.worldscommand.subcommands.List());
        subcommands.add(new Teleport());
        subcommands.add(new Load());
        subcommands.add(new Delete());
        subcommands.add(new Unload());
        subcommands.add(new Gamerule());
        subcommands.add(new SetSpawn());
    }

    // Note for later: please clan this up and remove the arrow code :D

    @Override
    public boolean onCommand(@NotNull CommandSender sender, Command command, String label, String[] args) {
        if(getSubCommandsOnlyWithPerms(sender).size() <= 1) {
            sender.sendMessage(Utils.chat(noPermissionMsg));
        }else if(args.length == 0) {
            getSubCommand("list").perform(sender, new String[]{""}, command);
        }else if(args.length > 0) {
            boolean ran = false;
            if(args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("?") || args[0].equalsIgnoreCase("info")) {
                printHelpMsg(sender);
            }else{
                for (SubCommand cmd : getSubcommands()) {
                    boolean use = false;
                    if(args[0].equalsIgnoreCase(cmd.getName())) {
                        use = true;
                    }
                    if(!use) {
                        for(String alias : cmd.getAliases()) {
                            if(args[0].equalsIgnoreCase(alias)) {
                                use = true;
                            }
                        }
                    }
                    if (use) {
                        if(sender.hasPermission(pluginPermission + ".*")) {
                            cmd.perform(sender, args, command);
                        }else if (cmd.getPermission().equalsIgnoreCase("")) {
                            cmd.perform(sender, args, command);
                        } else if (!cmd.getPermission().equalsIgnoreCase("") && sender.hasPermission(cmd.getPermission())) {
                            cmd.perform(sender, args, command);
                        } else {
                            Utils.commandStatus(sender, Utils.Status.FAILED, noPermissionMsg);
                        }
                        ran = true;
                    }
                }
                if(!ran) {
                    Utils.commandStatus(sender, Utils.Status.FAILED, "Unknown sub-command");
                }
            }
        }



        return true;
    }

    public SubCommand getSubCommand(String subCommandName) {
        for(SubCommand command : getSubcommands()) {
            if(command.getName().equalsIgnoreCase(subCommandName)) {
                return command;
            }
        }
        return null;
    }

    public void printHelpMsg(CommandSender sender) {
        if(sender instanceof Player) {
            sender.sendMessage(Utils.getSeperator(ChatColor.BLUE));
        }
        Utils.sendCenteredMessage(sender, "&d&l" + pluginTitle + " COMMANDS:");
        for(String cmdStr : getSubCommandsOnlyWithPerms(sender)) {
            if(sender.hasPermission(pluginPermission + ".*")) {
                printHelpMsgSpecific(sender, getSubCommand(cmdStr).getDescription(), getSubCommand(cmdStr).getUsage(), getSubCommand(cmdStr).getPermission(), Arrays.toString(getSubCommand(cmdStr).getAliases()));
            }else if (getSubCommand(cmdStr).getPermission().equalsIgnoreCase("")) {
                printHelpMsgSpecific(sender, getSubCommand(cmdStr).getDescription(), getSubCommand(cmdStr).getUsage(), getSubCommand(cmdStr).getPermission(), Arrays.toString(getSubCommand(cmdStr).getAliases()));
            } else if (!getSubCommand(cmdStr).getPermission().equalsIgnoreCase("") && sender.hasPermission(getSubCommand(cmdStr).getPermission())) {
                printHelpMsgSpecific(sender, getSubCommand(cmdStr).getDescription(), getSubCommand(cmdStr).getUsage(), getSubCommand(cmdStr).getPermission(), Arrays.toString(getSubCommand(cmdStr).getAliases()));
            }
        }
        if(sender instanceof Player) {
            sender.sendMessage(Utils.getSeperator(ChatColor.BLUE));
        }
    }

    public static void printHelpMsgSpecific(CommandSender sender, String description, String usage, String permission, String aliases) {
        if(permission.equalsIgnoreCase("")) {
            permission = "Everyone";
        }

        String ifAliases = "";
        if(!aliases.equalsIgnoreCase("[]")) {
            ifAliases = "\n&6Aliases: &f" + aliases;
        }
        BaseComponent component = new TextComponent(Utils.chat("&b" + usage));
        component.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, usage));
        component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(Utils.chat("&6Command: &f" + usage + "\n&6Description: &f" + description + "\n&6Permission: &f" + permission + ifAliases)).create()));
        sender.spigot().sendMessage(component);
    }

    public ArrayList<SubCommand> getSubcommands(){
        return subcommands;
    }

    public ArrayList<String> getSubCommandsOnlyWithPerms(CommandSender sender) {
        ArrayList<String> cmdNames = new ArrayList<>();
        for(SubCommand cmd : getSubcommands()) {
            if(sender.hasPermission(pluginPermission + ".*")) {
                cmdNames.add(cmd.getName());
            }else if(cmd.getPermission().equalsIgnoreCase("")) {
                cmdNames.add(cmd.getName());
            }else if(!cmd.getPermission().equalsIgnoreCase("") && sender.hasPermission(cmd.getPermission())) {
                cmdNames.add(cmd.getName());
            }
        }
        return cmdNames;
    }

    public static ArrayList<String> getPlayerNames() {
        ArrayList<String> players = new ArrayList<>();
        for(Player player : Bukkit.getOnlinePlayers()) {
            players.add(player.getName());
        }
        return players;
    }

    public static ArrayList<String> getWorldNames(String argument) {
        ArrayList<String> allWorlds = new ArrayList<>();
        for(World world : Bukkit.getWorlds()) {
            allWorlds.add(world.getName());
        }
        if(allWorlds.contains(argument)) {
            return net.cybercake.ghost.ffa.commands.maincommand.CommandManager.emptyList;
        }
        return allWorlds;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if(getSubCommandsOnlyWithPerms(sender).size() <= 1) {
            return emptyList;
        }else if(args.length <= 1) {
            return createReturnList(getSubCommandsOnlyWithPerms(sender), args[0]);
        }else{
            try {
                for(SubCommand cmd : getSubcommands()) {
                    for(int i = 1; i < 100; i++) {
                        boolean use = false;
                        if(args[0].equalsIgnoreCase(cmd.getName())) {
                            use = true;
                        }
                        if(!use) {

                            for(String cmdAlias : cmd.getAliases()) {
                                if(args[0].equalsIgnoreCase(cmdAlias)) {
                                    use = true;
                                }
                            }
                        }
                        if(use) {
                            if(args.length - 1 == i) {
                                if(sender.hasPermission(pluginPermission + ".*")) {
                                    return createReturnList(cmd.tab(sender, args), args[i]);
                                }else if(cmd.getPermission().equalsIgnoreCase("")) {
                                    return createReturnList(cmd.tab(sender, args), args[i]);
                                }else if(!cmd.getPermission().equalsIgnoreCase("") && sender.hasPermission(cmd.getPermission())) {
                                    return createReturnList(cmd.tab(sender, args), args[i]);
                                }else{
                                    return emptyList;
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                return emptyList;
            }
        }
        return emptyList;
    }

    public static boolean worldExist(String name) {
        if(Bukkit.getWorld(name) != null) {
            return true;
        }
        return false;
    }

    public static boolean deleteWorld(File path) {
        if(path.exists()) {
            File files[] = path.listFiles();
            for(int i=0; i<files.length; i++) {
                if(files[i].isDirectory()) {
                    deleteWorld(files[i]);
                } else {
                    files[i].delete();
                }
            }
        }
        return(path.delete());
    }

    public static List<String> createReturnList(List<String> completions, String currentArg) {
        if (currentArg.length() <= 0) { return completions; }

        currentArg = currentArg.toLowerCase(Locale.ROOT);
        List<String> returnedCompletions = new ArrayList<>();

        for (String str : completions) {
            if (str.toLowerCase(Locale.ROOT).contains(currentArg)) {
                returnedCompletions.add(str);
            }
        }

        return returnedCompletions;
    }
}
