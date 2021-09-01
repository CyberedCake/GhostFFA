package net.cybercake.ghost.ffa.repeatingtasks.menus;

import net.cybercake.ghost.ffa.utils.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ResetInvClickCooldown implements Runnable {

    @Override
    public void run() {
        for(Player player : Bukkit.getOnlinePlayers()) {
            ItemUtils.invClickCooldown.remove(player.getName());
        }
    }
}
