package net.cybercake.ghost.ffa.commands.maincommand;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandSendEvent;

public class CommandListeners implements Listener {

    @EventHandler
    public void onCommandSendEvent(PlayerCommandSendEvent e) {
        Player p = e.getPlayer();

        CommandManager manager = new CommandManager();

        if(manager.getSubCommandsOnlyWithPerms(p).size() <= 1) {
            e.getCommands().remove("ghostffa");
            e.getCommands().remove("gffa");
        }
    }

}
