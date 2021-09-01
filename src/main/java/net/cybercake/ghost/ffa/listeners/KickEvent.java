package net.cybercake.ghost.ffa.listeners;

import net.cybercake.ghost.ffa.Main;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;

public class KickEvent implements Listener {

    @EventHandler
    public void onKick(PlayerKickEvent e) {
        for(String str : Main.getMainConfig().getStringList("disabledKickMessages")) {
            if(ChatColor.stripColor(e.getReason()).equalsIgnoreCase(ChatColor.stripColor(str))) {
                e.setCancelled(true);
                return;
            }
        }
    }

}
