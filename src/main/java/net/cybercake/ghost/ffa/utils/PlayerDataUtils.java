package net.cybercake.ghost.ffa.utils;

import net.cybercake.ghost.ffa.Main;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;

public class PlayerDataUtils {

    public static boolean checkLoaded() {
        return true;
    }

    public static void setPlayerData(Player player, String path, Object toWhat) {
        File customYml = new File(Main.getPlugin().getDataFolder() + "/playerdata/" + player.getUniqueId() + ".yml");
        FileConfiguration customConfig = YamlConfiguration.loadConfiguration(customYml);

        customConfig.set(path, toWhat);

        try {
            customConfig.save(customYml);
        } catch (Exception e) {
            Bukkit.getLogger().severe(" ");
            Bukkit.getLogger().severe("Failed to save the configuration file (" + player.getUniqueId() + ".yml). Error: " + e);
            Bukkit.getLogger().severe(" ");
        }
    }

    public static Object getPlayerData(Player player, String path) {
        try {
            File customYml = new File(Main.getPlugin().getDataFolder() + "/playerdata/" + player.getUniqueId() + ".yml");
            FileConfiguration customConfig = YamlConfiguration.loadConfiguration(customYml);

            return customConfig.get(path);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static FileConfiguration getPlayerDataConfigFile(Player player) {
        try {
            File customYml = new File(Main.getPlugin().getDataFolder() + "/playerdata/" + player.getUniqueId() + ".yml");
            FileConfiguration customConfig = YamlConfiguration.loadConfiguration(customYml);

            return customConfig;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
