package net.cybercake.ghost.ffa;

import com.google.common.base.Charsets;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;

public class DuelsLang {

    private static File configFile = new File(JavaPlugin.getPlugin(Main.class).getDataFolder() + "/duelsLang.yml");
    private static FileConfiguration newConfig = YamlConfiguration.loadConfiguration(configFile);

    public static String getDuelsMode(String path) {
        return getDuelsLang().getString("duels.modes." + path);
    }

    public static String getDuelsArena(String path) { return getDuelsLang().getString("duels.arena." + path); }

    public static String getDuelsKit(String path) { return getDuelsLang().getString("duels.kits." + path); }

    //public static String getMessagesCommands(String path, Object... replacements) {
    //    String newMsg = getMessages().getString("messages.commands." + path);
    //    if(newMsg == null) {
    //        return "messages.commands." + path;
    //    }
    //    int replacenum = 0;
    //    for(Object obj : replacements) {
    //        newMsg = newMsg.replace("{" + replacenum + "}", obj.toString());
    //        replacenum++;
    //    }
    //    return newMsg;
    //}

    @NotNull
    public static FileConfiguration getDuelsLang() {
        if (newConfig == null) {
            reloadDuelsLang();
        }
        return newConfig;
    }

    public static void reloadDuelsLang() {
        newConfig = YamlConfiguration.loadConfiguration(configFile);

        final InputStream defConfigStream = JavaPlugin.getPlugin(Main.class).getResource("duelsLang.yml");

        newConfig.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream, Charsets.UTF_8)));
    }

    public static void saveDuelsLang() {
        try {
            getDuelsLang().save(configFile);
        } catch (IOException ex) {
            Bukkit.getLogger().log(Level.SEVERE, "Could not save messages to " + configFile, ex);
        }
    }

    public static void saveDefaultDuelsLang() {
        if (!configFile.exists()) {
            JavaPlugin.getPlugin(Main.class).saveResource("duelsLang.yml", false);
        }
    }

}
