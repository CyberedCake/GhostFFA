package net.cybercake.ghost.ffa.commands.admincommands.worldscommand.subcommands;

import net.cybercake.ghost.ffa.Main;
import net.cybercake.ghost.ffa.commands.admincommands.worldscommand.CommandManager;
import net.cybercake.ghost.ffa.commands.admincommands.worldscommand.SubCommand;
import net.cybercake.ghost.ffa.utils.DataUtils;
import net.cybercake.ghost.ffa.utils.Utils;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Load extends SubCommand {

    public Load() {
        super("load", "ghostffa.worlds.load", "Loads a specific world.", "/worlds load <worldName> <worldType>", "import", "create");
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
                if(file.exists() && !(folderHasDat(file))) {
                    Utils.commandStatus(sender, Utils.Status.FAILED, "File specified does not look like a world"); return;
                }
            }

            Utils.commandStatus(sender, Utils.Status.INFO, "&7&oCreating or loading a new world... please wait");

            WorldCreator worldCreator = new WorldCreator(args[1].toLowerCase(Locale.ROOT));
            worldCreator.type(type);
            worldCreator.createWorld();
            if(Bukkit.getWorld(args[1]) != null) {
                Utils.commandStatus(sender, Utils.Status.SUCCESS, "&fSuccessfully created or loaded new world named &b" + args[1]);

                setIfNull("worlds." + args[1].toLowerCase(Locale.ROOT) + ".name", args[1].toLowerCase(Locale.ROOT));
                setIfNull("worlds." + args[1].toLowerCase(Locale.ROOT) + ".key", Bukkit.getWorld(args[1]).getKey().toString());
                setIfNull("worlds." + args[1].toLowerCase(Locale.ROOT) + ".loaded", true);
                setIfNull("worlds." + args[1].toLowerCase(Locale.ROOT) + ".loadedBy", sender.getName());
                setIfNull("worlds." + args[1].toLowerCase(Locale.ROOT) + ".loadedOriginal", Utils.getUnix());
                setIfNull("worlds." + args[1].toLowerCase(Locale.ROOT) + ".type", type.getName().toUpperCase(Locale.ROOT));
                setIfNull("worlds." + args[1].toLowerCase(Locale.ROOT) + ".spawnLocation", Bukkit.getWorld(args[1]).getSpawnLocation());

                if(sender instanceof Player) {
                    Player player = (Player) sender;

                    player.teleport(SetSpawn.getWorldSpawn(args[1]));
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
            return CommandManager.createReturnList(allWorldsPlusUnloaded(true), args[1]);
        }else if(args.length == 3) {
            ArrayList<String> types = new ArrayList<>();
            for(WorldType type : WorldType.values()) {
                types.add(type.getName().toLowerCase(Locale.ROOT));
            }
            return CommandManager.createReturnList(types, args[2]);
        }
        return CommandManager.emptyList;
    }

    public static void setIfNull(String path, Object toWhat) {
        if(DataUtils.getCustomYmlObject("worlds", path) == null) {
            DataUtils.setCustomYml("worlds", path, toWhat);
        }
    }

    public static ArrayList<String> allWorldsPlusUnloaded(boolean onlyUnloaded) {
        ArrayList<String> worlds = new ArrayList<>();
        for(String normalFolder : new File(Paths.get("").toAbsolutePath() + "/").list()) {
            if(onlyUnloaded) {
                if(folderHasDat(new File(Paths.get("").toAbsolutePath() + "/" + normalFolder + "/")) && Bukkit.getWorld(normalFolder) == null) {
                    worlds.add(normalFolder);
                }
            }else{
                if(folderHasDat(new File(Paths.get("").toAbsolutePath() + "/" + normalFolder + "/"))) {
                    worlds.add(normalFolder);
                }
            }
        }
        return worlds;
    }

    public static boolean folderHasDat(File worldFolder) {
        File[] files = worldFolder.listFiles((file, name) -> name.toLowerCase(Locale.ROOT).endsWith(".dat"));
        return files != null && files.length > 0;
    }

    public static void loadWorld(String worldName) {
        // Make sure the world does not exist and is unloaded
        if(Bukkit.getWorld(worldName) != null) {
            return;
        }

        try {
            // Create a new world
            WorldCreator worldCreator = new WorldCreator(worldName.toLowerCase(Locale.ROOT));
            worldCreator.createWorld();

            // If successful, should pop out in console it has been
            if(Bukkit.getWorlds().contains(Bukkit.getWorld(worldName))) {
                Main.logInfo("Successfully loaded new world " + worldName);
            } else{
                Main.logError("An error occurred whilst trying to load the world " + worldName + " [...] failed to find!");
            }
        } catch (Exception exception) {
            // If unsuccessful, show internal error
            Main.logError("An error occurred whilst trying to load the world " + worldName);
            Main.logError(" ");
            Utils.printBetterStackTrace(exception);
        }
    }
}
