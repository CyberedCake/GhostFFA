package net.cybercake.ghost.ffa.listeners;

import me.clip.placeholderapi.PlaceholderAPI;
import net.cybercake.ghost.ffa.Main;
import net.cybercake.ghost.ffa.menus.kits.KitViewer;
import net.cybercake.ghost.ffa.utils.ItemUtils;
import net.cybercake.ghost.ffa.utils.PlayerDataUtils;
import net.cybercake.ghost.ffa.utils.Utils;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class JoinLeaveEvent implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();

        player.sendMessage(Utils.chat("\n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n"));
        if(PlayerDataUtils.getPlayerData(player, "joinLeave.joinDate") == null) {
            for(int i=1; i<8; i++) {
                PlayerDataUtils.setPlayerData(player, "kits." + i + ".lastSetTime", 0);
            }
        }
        PlayerDataUtils.setPlayerData(player, "joinLeave.joinDate", Utils.getUnix());

        e.joinMessage(Component.text(""));
        if(PlayerDataUtils.getPlayerData(player, "joinLeave.leaveDate") != null) {
            for (String joinMessageLine : Main.getMainConfig().getStringList("join")) {
                player.sendMessage(replacePlaceholders(joinMessageLine, player));
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();

        if(ItemUtils.currentMenu.get(player.getName()) != null && ItemUtils.currentMenu.get(player.getName()).equals(ItemUtils.Menu.KIT_VIEWER)) {
            KitViewer.saveKitConfiguration(player, KitViewer.currentKit.get(player.getName()), false);
        }

        PlayerDataUtils.setPlayerData(player, "joinLeave.leaveDate", Utils.getUnix());

        e.quitMessage(Component.text(""));
    }

    public String replacePlaceholders(String string, Player player) {
        String placeholdered = PlaceholderAPI.setPlaceholders(player, string);

        placeholdered = placeholdered.replace("{seperator}", Utils.getSeperator(ChatColor.BLUE));
        if(PlayerDataUtils.getPlayerData(player, "joinLeave.leaveDate") != null) {
            placeholdered = placeholdered.replace("{time}", Utils.getBetterTimeFromLongs(Utils.getUnix(), Long.parseLong(PlayerDataUtils.getPlayerData(player, "joinLeave.leaveDate") + ""), true));
        }

        return placeholdered;
    }

}
