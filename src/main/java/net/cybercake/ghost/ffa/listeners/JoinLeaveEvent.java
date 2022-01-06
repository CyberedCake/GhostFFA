package net.cybercake.ghost.ffa.listeners;

import de.tr7zw.changeme.nbtapi.data.PlayerData;
import me.clip.placeholderapi.PlaceholderAPI;
import net.cybercake.ghost.ffa.Main;
import net.cybercake.ghost.ffa.menus.kits.KitViewer;
import net.cybercake.ghost.ffa.repeatingtasks.CombatTimer;
import net.cybercake.ghost.ffa.utils.DataUtils;
import net.cybercake.ghost.ffa.utils.ItemUtils;
import net.cybercake.ghost.ffa.utils.PlayerDataUtils;
import net.cybercake.ghost.ffa.utils.Utils;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JoinLeaveEvent implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();

        e.joinMessage(Component.text(""));

        // Set basic data
        if(PlayerDataUtils.getPlayerData(player, "joinLeave.joinDate") == null) {
            for(int i=1; i<8; i++) {
                PlayerDataUtils.setPlayerData(player, "kits." + i + ".lastSetTime", 0);
            }
            ArrayList<String> names = new ArrayList<>();
            names.addAll(DataUtils.getCustomYmlStringList("playerdata/-allnames", "names"));
            names.add(player.getName());
            DataUtils.setCustomYml("playerdata/-allnames", "names", names);
        }
        PlayerDataUtils.setPlayerData(player, "joinLeave.joinDate", Utils.getUnix());
        PlayerDataUtils.setPlayerData(player, "generic.username", player.getName());
        ChatEvent.lastChat.put(player.getName(), System.currentTimeMillis());
        CombatTimer.removeBossbar.put(player.getName(), true);
        if(PlayerDataUtils.getPlayerData(player, "staff.enabled") == null) {
            PlayerDataUtils.setPlayerData(player, "staff.enabled", (player.hasPermission("ghostffa.command.staffchat")));
        }

        // Message when player joins
        player.sendMessage(Utils.chat("\n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n"));
        if(PlayerDataUtils.getPlayerData(player, "joinLeave.leaveDate") != null) {
            for (String joinMessageLine : Main.getMainConfig().getStringList("join")) {
                player.sendMessage(replacePlaceholders(joinMessageLine, player));
            }
        }
        player.sendTitle(" ", " ", 20, 50, 20);
        player.sendActionBar(Component.text(" "));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();

        if(ItemUtils.currentMenu.get(player.getName()) != null && ItemUtils.currentMenu.get(player.getName()).equals(ItemUtils.Menu.KIT_VIEWER)) {
            KitViewer.saveKitConfiguration(player, KitViewer.currentKit.get(player.getName()), false);
        }

        PlayerDataUtils.setPlayerData(player, "joinLeave.leaveDate", Utils.getUnix());

        for(String inCombatWith : CombatTimer.inCombatWith.values()) {
            CombatTimer.removeBossbar.put(inCombatWith, false);
            BossBar bossBar = DamagePlayer.bossBarHashMap.get(player.getName());
            bossBar.setTitle(Utils.chat("&fYour opponent, " + Utils.getFormattedName(player) + "&f, logged out in combat!"));
            bossBar.setProgress(0.0);
            bossBar.setColor(BarColor.BLUE);
            Bukkit.getScheduler().runTaskLater(Main.getPlugin(), ()->{
                if(Bukkit.getPlayerExact(inCombatWith) != null) {
                    bossBar.setVisible(false);
                    bossBar.removeAll();
                    CombatTimer.removeBossbar.put(inCombatWith, true);
                }
            }, 80L);
        }

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
