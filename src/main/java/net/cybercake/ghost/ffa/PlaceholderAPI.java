package net.cybercake.ghost.ffa;

import com.google.common.base.Charsets;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.cybercake.ghost.ffa.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;

public class PlaceholderAPI extends PlaceholderExpansion {

    private Main plugin;
    private static File configFile = new File(JavaPlugin.getPlugin(Main.class).getDataFolder() + "/placeholderapi.yml");
    private static FileConfiguration newConfig = YamlConfiguration.loadConfiguration(configFile);

    public PlaceholderAPI(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getAuthor() { return "CyberedCake"; }

    @Override
    public String getIdentifier() {return "ghostffa"; }

    @Override
    public String getVersion() { return "1.0.0"; }

    @Override
    public boolean persist() { return true; }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        for(String str : getPAPI().getConfigurationSection("ghostffa").getKeys(false)) {
            if(params.equalsIgnoreCase(str)) {
                return getPAPIPath(str);
            }
        }

        if(params.equalsIgnoreCase("tagName") || params.equalsIgnoreCase("tabName")){ return player.getName(); }
        else if(params.equalsIgnoreCase("tabSuffix") || params.equalsIgnoreCase("tagSuffix")) { return ""; }
        else if(params.equalsIgnoreCase("aboveName")) { return Utils.getColoredPing(player.getPlayer()); }
        else if(params.equalsIgnoreCase("belowName")) { return Math.round(player.getPlayer().getHealth()+player.getPlayer().getAbsorptionAmount()) + " &c❤"; }


        return null; // Placeholder is unknown by the Expansion
    }

    public static String getPAPIPath(String path) {
        return getPAPI().getString("ghostffa." + path);
    }

    @NotNull
    public static FileConfiguration getPAPI() {
        if (newConfig == null) {
            reloadPAPI();
        }
        return newConfig;
    }

    public static void reloadPAPI() {
        newConfig = YamlConfiguration.loadConfiguration(configFile);

        final InputStream defConfigStream = JavaPlugin.getPlugin(Main.class).getResource("placeholderapi.yml");

        newConfig.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream, Charsets.UTF_8)));
    }

    public static void savePAPI() {
        try {
            getPAPI().save(configFile);
        } catch (IOException ex) {
            Main.logError("Failed to save PlaceholderAPI to placeholderapi.yml");
            Main.logError(" ")
            Utils.printBetterStackTrace(ex);
        }
    }

    public static void saveDefaultPAPI() {
        if (!configFile.exists()) {
            JavaPlugin.getPlugin(Main.class).saveResource("placeholderapi.yml", false);
        }
    }

}
