package net.cybercake.ghost.ffa;

import me.lucko.commodore.CommodoreProvider;
import net.cybercake.ghost.ffa.commands.admincommands.*;
import net.cybercake.ghost.ffa.commands.admincommands.gamemodes.GMA;
import net.cybercake.ghost.ffa.commands.admincommands.gamemodes.GMC;
import net.cybercake.ghost.ffa.commands.admincommands.gamemodes.GMS;
import net.cybercake.ghost.ffa.commands.admincommands.gamemodes.GMSP;
import net.cybercake.ghost.ffa.commands.defaultcommands.*;
import net.cybercake.ghost.ffa.commands.maincommand.CommandListeners;
import net.cybercake.ghost.ffa.commands.maincommand.CommandManager;
import net.cybercake.ghost.ffa.commands.maincommand.subcommands.VirtualKitRoomAdmin;
import net.cybercake.ghost.ffa.commands.worldscommand.subcommands.Load;
import net.cybercake.ghost.ffa.listeners.*;
import net.cybercake.ghost.ffa.menus.kits.KitPreviewer;
import net.cybercake.ghost.ffa.menus.kits.KitViewer;
import net.cybercake.ghost.ffa.menus.kits.KitsMain;
import net.cybercake.ghost.ffa.menus.kits.VirtualKitRoom;
import net.cybercake.ghost.ffa.repeatingtasks.ClearLagTask;
import net.cybercake.ghost.ffa.repeatingtasks.CombatTimer;
import net.cybercake.ghost.ffa.repeatingtasks.menus.RefreshMenu;
import net.cybercake.ghost.ffa.repeatingtasks.menus.ResetInvClickCooldown;
import net.cybercake.ghost.ffa.utils.DataUtils;
import net.cybercake.ghost.ffa.utils.ItemUtils;
import net.cybercake.ghost.ffa.utils.PlayerDataUtils;
import net.cybercake.ghost.ffa.utils.Utils;
import net.kyori.adventure.text.Component;
import org.apache.commons.lang.Validate;
import org.bukkit.*;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Properties;
import java.util.logging.Level;

public final class Main extends JavaPlugin {

    private static Main plugin;
    public static String virtualKitRoomLoaded = "unloaded";
    public static long unixStarted = 0;

    @Override
    public void onEnable() {
        long mss = System.currentTimeMillis();
        plugin = this;

        // ------------------ SAVE RESOURCES & RELOAD ------------------
        new PlaceholderAPI(this).register();

        saveResource("config.yml", true);
        reloadConfig();

        saveResource("placeholderapi.yml", true);
        PlaceholderAPI.reloadPAPI();

        // ------------------ MESSAGE WHEN RELOADING ------------------
        if(Bukkit.getOnlinePlayers().size() > 0) {
            for(Player player : Bukkit.getOnlinePlayers()) {
                player.closeInventory();
                player.sendTitle(" ", Utils.chat("&eServer reloading... please wait!"), 10, 400, 10);
                player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 1F, 1F);
                player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 400, 1, false, false));
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 400, 255, false, false));
            }
        }else if(Bukkit.getOnlinePlayers().size() == 0 && unixStarted == 0) {
            unixStarted = Utils.getUnix();
        }

        // ------------------ LOAD COMMANDS, TAB COMPLETERS, LISTENERS, AND RUNNABLES ------------------
        boolean productionReady = true;
                      // General commands
        registerCommandAndTab("ghostffa", new CommandManager(), productionReady);
        registerCommandAndTab("worlds", new net.cybercake.ghost.ffa.commands.worldscommand.CommandManager(), productionReady);
        registerCommandAndTab("guislots", new ItemUtils(), productionReady);
        registerCommandAndTab("ping", new Ping(), productionReady);
        registerCommandAndTab("discord", new Discord(), productionReady);
        registerCommandAndTab("kits", new Kits(), productionReady);
        registerCommandAndTab("clearlag", new ClearLagCMD(), productionReady);
        registerCommandAndTab("spawn", new Spawn(), productionReady);
        registerCommandAndTab("rename", new Rename(), productionReady);
        registerCommandAndTab("seen", new Seen(), productionReady);
                      // Gamemode commands
        registerCommandAndTab("gmc", new GMC(), productionReady);
        registerCommandAndTab("gms", new GMS(), productionReady);
        registerCommandAndTab("gmsp", new GMSP(), productionReady);
        registerCommandAndTab("gma", new GMA(), productionReady);
                      // Admin commands
        registerCommandAndTab("gamemode", new Gamemode(), productionReady);
        registerCommandAndTab("clear", new Clear(), productionReady);
        registerCommandAndTab("give", new Give(), productionReady);
        registerCommandAndTab("fly", new Fly(), productionReady);
        registerCommandAndTab("broadcast", new Broadcast(), productionReady);
        registerCommandAndTab("invsee", new InvSee(), productionReady);
        registerCommandAndTab("steleport", new STeleport(), productionReady);
        registerCommandAndTab("help", new Help(), productionReady);
        registerCommandAndTab("combat", new Combat(), productionReady);
        registerCommandAndTab("loop", new Loop(), productionReady);
        registerCommandAndTab("heal", new Heal(), productionReady);
        registerCommandAndTab("feed", new Feed(), productionReady);


                      // General
        registerListener(new ChatEvent());
        registerListener(new JoinLeaveEvent());
        registerListener(new KickEvent());
                      // Commands
        registerListener(new CommandListeners());
        registerListener(new CommandSendEvent());
        registerListener(new CommandPreProcessEvent());
                      // GUIs & inventory click events
        registerListener(new ItemUtils());
        registerListener(new KitsMain());
        registerListener(new KitViewer());
        registerListener(new VirtualKitRoom());
        registerListener(new VirtualKitRoomAdmin());
        registerListener(new KitPreviewer());
        registerListener(new DamagePlayer());


                      // Runnables
        registerRunnable(new ClearLagTask(), 20L);
        registerRunnable(new RefreshMenu(), 20L);
        registerRunnable(new ResetInvClickCooldown(), 5L);
        registerRunnable(new CombatTimer(), 1L);

        // ------------------ LOAD COMMODORE & WORLDS & OTHERS ------------------
        if(CommodoreProvider.isSupported()) {
            try {
                if(!(Bukkit.getPluginManager().getPlugin("PlugMan") == null) && Bukkit.getPluginManager().getPlugin("PlugMan").isEnabled()) {
                    RegisterBrigadier.registerCommodoreCommand(Bukkit.getPluginManager().getPlugin("PlugMan").getServer().getPluginCommand("plugman"), "plugman");
                }

            } catch (IOException e) {
                logError(getPluginPrefix() + " An error occurred whilst loading brigadier/commodore command: /plugman");
            }
        }

        for(String world : Load.allWorldsPlusUnloaded(true)) {
            logInfo("Attempting to load the world " + world + " ... please wait!");
            Load.loadWorld(world);
        }
        for(World world : Bukkit.getWorlds()) {
            if(!DataUtils.getCustomYmlBoolean("worlds", "worlds." + world.getName() + ".loaded")) {
                Bukkit.unloadWorld(world, true);
            }
        }

        try {
            PlayerDataUtils.checkLoaded();
        } catch (Exception exception) {
            logError("An error occurred whilst loading the class PlayerDataUtils.java");
            logError("Try restarting the server! That should fix it.");
            logError(" ");
            Utils.printBetterStackTrace(exception);
        }

        // ------------------ MESSAGE WHEN COMPLETE ------------------
        for(Player player : Bukkit.getOnlinePlayers()) {
            player.closeInventory();
            player.sendTitle(" ", Utils.chat("&aReload complete in &b" + Utils.formatLong(System.currentTimeMillis()-mss) + "&bms&a!"), 10, 80, 10);
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 1F, 1F);
            player.removePotionEffect(PotionEffectType.BLINDNESS);
            player.removePotionEffect(PotionEffectType.SLOW);
            if(VirtualKitRoomAdmin.typeInChat.get(player.getName()) != null) {
                VirtualKitRoomAdmin.typeInChat.remove(player.getName());
            }
        }

        logInfo("Enabled GhostFFA [v" + getPlugin(Main.class).getDescription().getVersion() + "] in " + (System.currentTimeMillis()-mss) + "ms");


        // ------------------ LOAD POST PLUGIN STARTUP ------------------
        //              (usually Async or unimportant tasks)
        try {
            Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
                broadcastFormatted("Loading Virtual Kit Room... this may take a few seconds!", true);
                VirtualKitRoomAdmin.virtualKitRoomLoader();
                VirtualKitRoom.loadVirtualKitRoom(true);
            });
        } catch (Exception exception) {
            Utils.error(Bukkit.getPlayerExact("CyberedCake"), "whilst trying to load the Virtual Kit Room", exception);
            broadcastFormatted("&4[Server Error] &cFailed to load the Virtual Kit Room: " + exception, true);
        }

        if(!productionReady) {
            Bukkit.getScheduler().runTaskLater(Main.getPlugin(), () -> {
                broadcastFormatted("Please note that this is a developmental version of the plugin. Use extreme caution and make backups!", true);
            }, 80L);
        }
    }



    public void broadcastLOUD() {
        Bukkit.broadcastMessage("SUCH A LOUD BROADCAST WITH NO MESSAGE ATTACHED!");
    }

    public void broadcastLOADArguments(String argument) {
        Bukkit.broadcastMessage("SUCH A LOUD BROADCAST: " + argument);
    }

    @Override
    public void onDisable() {
        long mss = System.currentTimeMillis();

        logInfo("Disabled GhostFFA [v" + getPlugin(Main.class).getDescription().getVersion() + "] in " + (System.currentTimeMillis()-mss) + "ms");
    }




    @NotNull
    public static YamlConfiguration loadConfiguration(@NotNull File file) throws Exception {
        Validate.notNull(file, "File cannot be null");

        YamlConfiguration config = new YamlConfiguration();

        try {
            config.load(file);
        } catch (FileNotFoundException ex) {
        } catch (IOException ex) {
            Bukkit.getLogger().log(Level.SEVERE, "Cannot load " + file, ex);
            throw ex;
        } catch (InvalidConfigurationException ex) {
            Bukkit.getLogger().log(Level.SEVERE, "Cannot load " + file, ex);
            throw ex;
        }

        return config;
    }

    public static void broadcastFormatted(String string, boolean admin) {
        if(Bukkit.getOnlinePlayers().size() == 0) return;

        for(Player player : Bukkit.getOnlinePlayers()) {
            if(admin) {
                if(player.hasPermission("ghostffa.admin.receiveadminbc")) {
                    player.sendMessage(Component.text(Utils.chat("&8[&bGFFA &c&lA&8] &f" + string)));
                }
            } else {
                player.sendMessage(Component.text(Utils.chat("&8[&bGFFA&8] &f" + string)));
            }
        }
    }

    public static Main getPlugin() { return plugin; }
    public static @NotNull FileConfiguration getMainConfig() { return plugin.getConfig(); }
    public static @NotNull String getPluginPrefix() { return "[" + getPlugin(Main.class).getDescription().getPrefix() + "]"; }
    public static void logInfo(String msg) { Bukkit.getLogger().info(getPluginPrefix() + " " + msg); }
    public static void logWarn(String msg) { Bukkit.getLogger().warning(getPluginPrefix() + " " + msg); }
    public static void logError(String msg) { Bukkit.getLogger().severe(msg); }
    public static @Nullable World getMainWorld() {
        Properties pr = new Properties();
        try {
            FileInputStream in = new FileInputStream("server.properties");
            pr.load(in);
            String string = pr.getProperty("level-name");
            return Bukkit.getWorld(string);
        }
        catch (IOException e) { }
        return null;
    }
    public static @NotNull String getVersionString() throws Exception {
          int yourProtocol = -1;
          String yourVersion = Main.getPlugin(Main.class).getDescription().getVersion();

          try {
               BufferedReader reader = new BufferedReader(new InputStreamReader(Main.getPlugin().getResource("plugin.yml")));
               String line;
               while((line = reader.readLine()) != null) {
                    if(line.contains("version-protocol")) {
                         yourProtocol = Integer.parseInt(line.replace("version-protocol: ", ""));
                    }
               }
          } catch (Exception exception) {
               throw new Exception(exception);
          }
          return "GhostFFA version " + yourVersion + ", protocol " + yourProtocol;
    }

    private static void registerCommandAndTab(String name, Object commandExecutor, boolean withCommodore) {
        try {
            plugin.getCommand(name).setExecutor((CommandExecutor)commandExecutor);
            plugin.getCommand(name).setTabCompleter((TabCompleter) commandExecutor);
            if(withCommodore) {
                if(CommodoreProvider.isSupported()) {
                    RegisterBrigadier.registerCommodoreCommand(Bukkit.getPluginCommand(name), name);
                }
            }
        } catch (Exception exception) {
            logError("An error occurred whilst loading the command: /" + name);
        }
    }
    private static void registerCommand(String name, CommandExecutor commandExecutor) { plugin.getCommand(name).setExecutor(commandExecutor); }
    private static void registerTabCompleter(String name, TabCompleter tabCompleter) { plugin.getCommand(name).setTabCompleter(tabCompleter); }
    private static void registerListener(Listener listener) { plugin.getServer().getPluginManager().registerEvents(listener, plugin); }
    private static void registerRunnable(Runnable runnable, long period) { Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, runnable, 10L, period); }

}
