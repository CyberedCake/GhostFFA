package net.cybercake.ghost.ffa.listeners;

import net.cybercake.ghost.ffa.Main;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CommandPreProcessEvent implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCommand(PlayerCommandPreprocessEvent e) {
        Player p = e.getPlayer();
        if(p.hasPermission(Main.getMainConfig().getString("blockedCommands.bypassPermission"))) return;

        for(String command : Main.getMainConfig().getStringList("blockedCommands.whitelisted")) {
            if(e.getMessage().equalsIgnoreCase("/" + command)) {
                return;
            }
        }
        e.setCancelled(true);
    }

}