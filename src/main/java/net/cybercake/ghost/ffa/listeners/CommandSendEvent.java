package net.cybercake.ghost.ffa.listeners;

import net.cybercake.ghost.ffa.Main;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandSendEvent;

public class CommandSendEvent implements Listener {

    @EventHandler
    public void onCommandSend(PlayerCommandSendEvent e) {
        Player p = e.getPlayer();
        if(p.hasPermission(Main.getMainConfig().getString("blockedCommands.bypassPermission"))) return;

        e.getCommands().removeAll(e.getCommands());
        e.getCommands().addAll(Main.getMainConfig().getStringList("blockedCommands.whitelisted"));
    }

}
