package net.cybercake.ghost.ffa.listeners;

import net.cybercake.ghost.ffa.utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatEvent implements Listener {

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();
        String msg = e.getMessage();
        if(player.hasPermission("ghostffa.admin.coloredchat")) {
            msg = Utils.chat(e.getMessage());
        }
        e.setFormat(Utils.chat(Utils.getFormattedName(player) + " &f> ") + msg);

        // Send message to Discord
    }

}
