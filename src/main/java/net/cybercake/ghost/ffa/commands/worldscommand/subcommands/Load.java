package net.cybercake.ghost.ffa.commands.worldscommand.subcommands;

import net.cybercake.ghost.ffa.Main;
import net.cybercake.ghost.ffa.commands.worldscommand.CommandManager;
import net.cybercake.ghost.ffa.commands.worldscommand.SubCommand;
import net.cybercake.ghost.ffa.utils.DataUtils;
import net.cybercake.ghost.ffa.utils.Utils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class Load extends SubCommand {

    public Load() {
        super("load", "ghostffa.worlds.load", "Loads a specific world.", "/worlds load <worldName>", "import", "create");
    }

    @Override
    public void perform(CommandSender sender, String[] args, Command command) {
        if (args.length < 3) {
            Utils.commandStatus(sender, Utils.Status.FAILED, "Invalid arguments"); return;
        }

        try {
            WorldType type = null;
            if(args.length == 3) {
                for(WorldType types : WorldType.values()) {
                    if(args[2].toLowerCase(Locale.ROOT).equals(types.getName().toLowerCase(Locale.ROOT))) {
                        type = types;
                        break;
                    }
                }
            }

            if(type == null) {
                Utils.commandStatus(sender, Utils.Status.FAILED, "Invalid world type"); return;
            }
            if(CommandManager.worldExist(args[1])) {
                Utils.commandStatus(sender, Utils.Status.FAILED, "World already loaded with that name"); return;
            }else{
                File file = new File(Paths.get("").toAbsolutePath() + "/" + args[1]);
                if(file.exists() && !(Arrays.asList(file.listFiles()).contains("level.dat"))) {
                    Utils.commandStatus(sender, Utils.Status.FAILED, "File specified does not look like a world"); return;
                }
            }

            Utils.commandStatus(sender, Utils.Status.INFO, "&7&oCreating or loading a new world... please wait");

            WorldCreator worldCreator = new WorldCreator(args[1].toLowerCase(Locale.ROOT));
            worldCreator.type(type);
            worldCreator.createWorld();
            if(Bukkit.getWorld(args[1]) != null) {
                Utils.commandStatus(sender, Utils.Status.SUCCESS, "&fSuccessfully created or loaded new world named &b" + args[1]);

                DataUtils.setCustomYml("worlds", "worlds." + args[1].toLowerCase(Locale.ROOT) + ".name", args[1].toLowerCase(Locale.ROOT));
                DataUtils.setCustomYml("worlds", "worlds." + args[1].toLowerCase(Locale.ROOT) + ".key", Bukkit.getWorld(args[1]).getKey().toString());
                DataUtils.setCustomYml("worlds", "worlds." + args[1].toLowerCase(Locale.ROOT) + ".loadedBy", sender.getName());
                DataUtils.setCustomYml("worlds", "worlds." + args[1].toLowerCase(Locale.ROOT) + ".loadedOriginal", Utils.getUnix());
                DataUtils.setCustomYml("worlds", "worlds." + args[1].toLowerCase(Locale.ROOT) + ".type", type.getName().toUpperCase(Locale.ROOT));

                if(sender instanceof Player) {
                    Player player = (Player) sender;

                    player.teleport(Bukkit.getWorld(args[1]).getSpawnLocation());
                }
            }else if(Bukkit.getWorld(args[1]) == null) {
                Utils.commandStatus(sender, Utils.Status.FAILED, "Failed to create the world");
            }
        } catch (Exception exception) {
            Utils.error(sender, "during the world creation process for {name}", exception);
        }
    }

    @Override
    public List<String> tab(CommandSender sender, String[] args) {
        if(args.length == 2) {
            ArrayList<String> files = new ArrayList<>();
            File file = new File(Paths.get("").toAbsolutePath() + "");
            for(File file1 : file.listFiles()) {
                if(!Arrays.asList(file1.listFiles()).contains("level.dat")) {
                    files.add(file1.getName());
                }
            }
            return files;
        }else if(args.length == 3) {
            ArrayList<String> types = new ArrayList<>();
            for(WorldType type : WorldType.values()) {
                types.add(type.getName().toLowerCase(Locale.ROOT));
            }
            return CommandManager.createReturnList(types, args[2]);
        }
        return CommandManager.emptyList;
    }

    public static void loadWorld(String worldName) {
        if(Bukkit.getWorld(worldName) != null) {
            return;
        }

        try {
            WorldCreator worldCreator = new WorldCreator(worldName.toLowerCase(Locale.ROOT));
            worldCreator.createWorld();
            if(Bukkit.getWorlds().contains(Bukkit.getWorld(worldName))) {
                Main.logInfo("Successfully loaded new world " + worldName);
            } else{
                Main.logError("An error occurred whilst trying to load the world " + worldName + " [...] failed to find!");
            }
        } catch (Exception exception) {
            Main.logError("An error occurred whilst trying to load the world " + worldName);
            Main.logError(" ");
            Utils.printBetterStackTrace(exception);
        }
    }
}
