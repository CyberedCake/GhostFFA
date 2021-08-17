package net.cybercake.ghost.ffa;

import me.lucko.commodore.CommodoreProvider;
import net.cybercake.ghost.ffa.commands.*;
import net.cybercake.ghost.ffa.commands.maincommand.CommandListeners;
import net.cybercake.ghost.ffa.commands.maincommand.CommandManager;
import net.cybercake.ghost.ffa.listeners.ChatEvent;
import net.cybercake.ghost.ffa.listeners.CommandPreProcessEvent;
import net.cybercake.ghost.ffa.listeners.CommandSendEvent;
import net.cybercake.ghost.ffa.listeners.JoinLeaveEvent;
import net.cybercake.ghost.ffa.menus.kits.KitViewer;
import net.cybercake.ghost.ffa.menus.kits.KitsMain;
import net.cybercake.ghost.ffa.repeatingtasks.ClearLagTask;
import net.cybercake.ghost.ffa.repeatingtasks.menus.RefreshMenu;
import net.cybercake.ghost.ffa.repeatingtasks.menus.ResetInvClickCooldown;
import net.cybercake.ghost.ffa.utils.ItemUtils;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;

public final class Main extends JavaPlugin {

    private static Main plugin;

    @Override
    public void onEnable() {
        long mss = System.currentTimeMillis();
        plugin = this;

        new PlaceholderAPI(this).register();

        saveResource("config.yml", true);
        reloadConfig();

        saveResource("placeholderapi.yml", true);
        PlaceholderAPI.reloadPAPI();

        registerCommand("guislots", new ItemUtils());
        registerCommand("ghostffa", new CommandManager());
        registerCommand("ping", new Ping());
        registerCommand("discord", new Discord());
        registerCommand("kits", new Kits());
        registerCommand("clearlag", new ClearLagCMD());

        registerAllTabCompleters();

        if(CommodoreProvider.isSupported()) {
            try {
                RegisterBrigadier.registerCommodoreCommand(Bukkit.getPluginCommand("guislots"), "guislots");
                RegisterBrigadier.registerCommodoreCommand(Bukkit.getPluginCommand("clearlag"), "clearlagcmd");
                RegisterBrigadier.registerCommodoreCommand(Bukkit.getPluginCommand("discord"), "discord");
                RegisterBrigadier.registerCommodoreCommand(Bukkit.getPluginCommand("ping"), "ping");
                RegisterBrigadier.registerCommodoreCommand(Bukkit.getPluginCommand("kits"), "kits");
                RegisterBrigadier.registerCommodoreCommand(Bukkit.getPluginCommand("ghostffa"), "ghostffa");
                RegisterBrigadier.registerCommodoreCommand(Bukkit.getPluginManager().getPlugin("PlugMan").getServer().getPluginCommand("plugman"), "plugman");

                logInfo("Commodore successfully registered all commands!");
            } catch (Exception e) {
                logError(this.getDescription().getPrefix() + " Some or all Commodore commands failed to register, reverting those to default Bukkit format...");
                registerAllTabCompleters();
            }
        }

        // General
        registerListener(new ChatEvent());
        registerListener(new JoinLeaveEvent());
        // Commands
        registerListener(new CommandListeners());
        registerListener(new CommandSendEvent());
        registerListener(new CommandPreProcessEvent());
        // GUIs & inventory click events
        registerListener(new ItemUtils());
        registerListener(new KitsMain());
        registerListener(new KitViewer());

        registerRunnable(new ClearLagTask(), 20L);
        registerRunnable(new RefreshMenu(), 1L);
        registerRunnable(new ResetInvClickCooldown(), 1L);

        logInfo("Enabled GhostFFA [v" + getPlugin(Main.class).getDescription().getVersion() + "] in " + (System.currentTimeMillis()-mss) + "ms");
    }

    @Override
    public void onDisable() {
        long mss = System.currentTimeMillis();

        logInfo("Disabled GhostFFA [v" + getPlugin(Main.class).getDescription().getVersion() + "] in " + (System.currentTimeMillis()-mss) + "ms");
    }

    public void registerAllTabCompleters() {
        registerTabCompleter("guislots", new ItemUtils());
        registerTabCompleter("ghostffa", new CommandManager());
        registerTabCompleter("ping", new Ping());
        registerTabCompleter("discord", new Discord());
        registerTabCompleter("kits", new Kits());
        registerTabCompleter("clearlag", new ClearLagCMD());
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

    public static Main getPlugin() { return plugin; }
    public static FileConfiguration getMainConfig() { return plugin.getConfig(); }
    public static String getPluginPrefix() { return getPlugin(Main.class).getDescription().getPrefix(); }
    public static void logInfo(String msg) { Bukkit.getLogger().info("[" + getPluginPrefix() + "] " + msg); }
    public static void logWarn(String msg) { Bukkit.getLogger().warning("[" + getPluginPrefix() + "] " + msg); }
    public static void logError(String msg) { Bukkit.getLogger().severe(msg); }

    private static void registerCommand(String name, CommandExecutor commandExecutor) { plugin.getCommand(name).setExecutor(commandExecutor); }
    private static void registerTabCompleter(String name, TabCompleter tabCompleter) { plugin.getCommand(name).setTabCompleter(tabCompleter); }
    private static void registerListener(Listener listener) { plugin.getServer().getPluginManager().registerEvents(listener, plugin); }
    private static void registerRunnable(Runnable runnable, long period) { Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, runnable, 10L, period); }

}
