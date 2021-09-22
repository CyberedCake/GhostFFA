package net.cybercake.ghost.ffa.listeners;

import net.cybercake.ghost.ffa.Main;
import net.cybercake.ghost.ffa.repeatingtasks.CombatTimer;
import net.cybercake.ghost.ffa.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.HashMap;

public class DamagePlayer implements Listener {

    public static HashMap<String, BossBar> bossBarHashMap = new HashMap<>();

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
        if(!(e.getDamager() instanceof Player) || !(e.getEntity() instanceof Player)) { return; }

        Player attacker = (Player) e.getDamager();
        Player victim = (Player) e.getEntity();

        if(bossBarHashMap.get(attacker.getName()) != null) {
            bossBarHashMap.get(attacker.getName()).setVisible(false);
            bossBarHashMap.get(attacker.getName()).removeAll();
        }
        if(bossBarHashMap.get(victim.getName()) != null) {
            bossBarHashMap.get(victim.getName()).setVisible(false);
            bossBarHashMap.get(victim.getName()).removeAll();
        }

        CombatTimer.inCombat.put(victim.getName(), 300.0);
        CombatTimer.inCombat.put(attacker.getName(), 300.0);

        CombatTimer.inCombatWith.put(victim.getName(), attacker.getName());
        CombatTimer.inCombatWith.put(attacker.getName(), victim.getName());

        BossBar victimBossbar = Bukkit.createBossBar(Utils.chat("&fIn combat with &b" + Utils.getFormattedName(CombatTimer.inCombatWith.get(victim.getName())) + " &ffor &e" + Math.round(CombatTimer.inCombat.get(victim.getName())/20) + "&es&f!"), BarColor.RED, BarStyle.SOLID);
        victimBossbar.setVisible(true);
        victimBossbar.setProgress(1.0);
        victimBossbar.addPlayer(victim);
        bossBarHashMap.put(victim.getName(), victimBossbar);

        BossBar attackerBossbar = Bukkit.createBossBar(Utils.chat("&fIn combat with &b" + Utils.getFormattedName(CombatTimer.inCombatWith.get(attacker.getName())) + " &ffor &e" + Math.round(CombatTimer.inCombat.get(victim.getName())/20) + "&es&f!"), BarColor.RED, BarStyle.SOLID);
        attackerBossbar.setVisible(true);
        attackerBossbar.setProgress(1.0);
        attackerBossbar.addPlayer(attacker);
        bossBarHashMap.put(attacker.getName(), attackerBossbar);

        if(Main.getMainConfig().getBoolean("customSoundOnDamage")) {
            attacker.playSound(attacker.getLocation(), Sound.ENTITY_BLAZE_HURT, 1F, 1F);
            victim.playSound(attacker.getLocation(), Sound.ENTITY_BLAZE_HURT, 1F, 1F);
        }
    }

}
