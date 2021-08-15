package net.cybercake.ghost.ffa.repeatingtasks;

import net.cybercake.ghost.ffa.Main;
import net.cybercake.ghost.ffa.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Item;

public class ClearLagTask implements Runnable {

    public static int clearLagInterval = 0;
    public static int currentInterval = 0;

    @Override
    public void run() {
        clearLagInterval = Main.getMainConfig().getInt("builtInClearLag.interval");

        try {
            if(currentInterval >= clearLagInterval) {
                if(Main.getMainConfig().getBoolean("builtInClearLag.showConsoleMessages")) { Main.logInfo("(ClearLag) Cleared " + clearGroundItems() + " ground items!"); }
                currentInterval = 0;
            }
        } catch (Exception e) {
            Main.logError("An error occurred whilst clearing ground entities!");
            Main.logError(" ");
            Main.logError("Stack trace below:");
            Utils.printBetterStackTrace(e);
            Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(Utils.chat("&c&lClearLag Error! &7An error occurred with ClearLag, check console!")));
            currentInterval = clearLagInterval;
        }
        currentInterval++;
    }

    public static int clearGroundItems() {
        int items = 0;
        for(String worldString : Main.getMainConfig().getStringList("builtInClearLag.worldsCleared")) {
            World world = Bukkit.getWorld(worldString);
            for(Item item : world.getEntitiesByClass(Item.class)) {
                items++;
                item.remove();
            }
        }
        return items;
    }
}
