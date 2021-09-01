package net.cybercake.ghost.ffa;

import me.lucko.commodore.CommodoreProvider;
import net.cybercake.ghost.ffa.commands.*;
import net.cybercake.ghost.ffa.commands.admincommands.Clear;
import net.cybercake.ghost.ffa.commands.admincommands.Gamemode;
import net.cybercake.ghost.ffa.commands.maincommand.CommandListeners;
import net.cybercake.ghost.ffa.commands.maincommand.CommandManager;
import net.cybercake.ghost.ffa.commands.maincommand.subcommands.VirtualKitRoomAdmin;
import net.cybercake.ghost.ffa.listeners.*;
import net.cybercake.ghost.ffa.menus.kits.KitPreviewer;
import net.cybercake.ghost.ffa.menus.kits.KitViewer;
import net.cybercake.ghost.ffa.menus.kits.KitsMain;
import net.cybercake.ghost.ffa.menus.kits.VirtualKitRoom;
import net.cybercake.ghost.ffa.repeatingtasks.ClearLagTask;
import net.cybercake.ghost.ffa.repeatingtasks.menus.RefreshMenu;
import net.cybercake.ghost.ffa.repeatingtasks.menus.ResetInvClickCooldown;
import net.cybercake.ghost.ffa.utils.ItemUtils;
import net.cybercake.ghost.ffa.utils.Utils;
import net.kyori.adventure.text.Component;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;

public final class Main extends JavaPlugin {

    private static Main plugin;
    public static String virtualKitRoomLoaded = "unloaded";

    @Override
    public void onEnable() {
        long mss = System.currentTimeMillis();
        plugin = this;

        new PlaceholderAPI(this).register();

        saveResource("config.yml", true);
        reloadConfig();

        saveResource("placeholderapi.yml", true);
        PlaceholderAPI.reloadPAPI();

        if(Bukkit.getOnlinePlayers().size() > 0) {
            for(Player player : Bukkit.getOnlinePlayers()) {
                player.closeInventory();
                player.sendTitle(" ", Utils.chat("&eServer reloading... please wait!"), 10, 400, 10);
                player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 1F, 1F);
                player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 400, 1, false, false));
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 400, 255, false, false));
            }
        }

        registerCommandAndTab("ghostffa", new CommandManager(), true);
        registerCommandAndTab("guislots", new ItemUtils(), true);
        registerCommandAndTab("ping", new Ping(), true);
        registerCommandAndTab("discord", new Discord(), true);
        registerCommandAndTab("kits", new Kits(), true);
        registerCommandAndTab("clearlag", new ClearLagCMD(), true);
        registerCommandAndTab("spawn", new Spawn(), true);
        // Admin commands
        registerCommandAndTab("gamemode", new Gamemode(), true);
        registerCommandAndTab("clear", new Clear(), true);

        if(CommodoreProvider.isSupported()) {
            try {
                RegisterBrigadier.registerCommodoreCommand(Bukkit.getPluginManager().getPlugin("PlugMan").getServer().getPluginCommand("plugman"), "plugman");
            } catch (IOException e) {
                logError(getPluginPrefix() + " An error occurred whilst loading brigadier/commodore command: /guislots");
            }
        }

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

        registerRunnable(new ClearLagTask(), 20L);
        registerRunnable(new RefreshMenu(), 20L);
        registerRunnable(new ResetInvClickCooldown(), 5L);

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

        try {
            Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
                broadcastFormatted("Loading Virtual Kit Room... this may take a few seconds!", true);
                VirtualKitRoomAdmin.virtualKitRoomLoader();
                VirtualKitRoom.loadVirtualKitRoom(true);
            });
        } catch (Exception exception) {
            logError(getPluginPrefix() + " An error occurred whilst loading the Virtual Kit Room: " + exception);
            Bukkit.getOnlinePlayers().forEach(player -> {
                if (player.hasPermission("ghostffa.*")) {
                    player.sendMessage(Utils.chat("&4[Server Error] &cFailed to load the Virtual Kit Room: " + exception));
                }});
        }
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
    public static FileConfiguration getMainConfig() { return plugin.getConfig(); }
    public static String getPluginPrefix() { return "[" + getPlugin(Main.class).getDescription().getPrefix() + "]"; }
    public static void logInfo(String msg) { Bukkit.getLogger().info(getPluginPrefix() + " " + msg); }
    public static void logWarn(String msg) { Bukkit.getLogger().warning(getPluginPrefix() + " " + msg); }
    public static void logError(String msg) { Bukkit.getLogger().severe(msg); }

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
