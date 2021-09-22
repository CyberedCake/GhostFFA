package net.cybercake.ghost.ffa.repeatingtasks;

import net.cybercake.ghost.ffa.Main;
import net.cybercake.ghost.ffa.listeners.DamagePlayer;
import net.cybercake.ghost.ffa.utils.Utils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class CombatTimer implements Runnable {

    // first(String) = player name
    // second(Integer) = time in combat
    public static HashMap<String, Double> inCombat = new HashMap<>();

    // first(String) = player name
    // second(String) = player name in combat with
    public static HashMap<String, String> inCombatWith = new HashMap<>();

    // first(String) = player name
    // second(Booleane) = end bossbar instance
    public static HashMap<String, Boolean> removeBossbar = new HashMap<>();

    @Override
    public void run() {
        for(Player player : Bukkit.getOnlinePlayers()) {
            if(inCombat.get(player.getName()) == null || inCombat.get(player.getName()) <= 0) {

                endCombat(player, false);
                continue;
            }

            if(Bukkit.getPlayerExact(CombatTimer.inCombatWith.get(player.getName())) != null && DamagePlayer.bossBarHashMap.get(player.getName()) != null) {
                BossBar bossBar = DamagePlayer.bossBarHashMap.get(player.getName());
                bossBar.setProgress(inCombat.get(player.getName())/300);
                bossBar.setTitle(Utils.chat("&fIn combat with &b" + Utils.getFormattedName(CombatTimer.inCombatWith.get(player.getName())) + " &ffor &e" + Math.round(CombatTimer.inCombat.get(player.getName())/20) + "&es&f!"));
            }
            inCombat.put(player.getName(), inCombat.get(player.getName())-1);
        }
    }

    public static void endCombat(Player player, boolean removeBossbar) {
        inCombat.remove(player.getName());
        if(removeBossbar && DamagePlayer.bossBarHashMap.get(player.getName()) != null) {
            DamagePlayer.bossBarHashMap.get(player.getName()).setVisible(false);
            DamagePlayer.bossBarHashMap.get(player.getName()).removeAll();
        }
    }
}
