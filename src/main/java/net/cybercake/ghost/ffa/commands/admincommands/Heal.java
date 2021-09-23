package net.cybercake.ghost.ffa.commands.admincommands;

import net.cybercake.ghost.ffa.commands.maincommand.CommandManager;
import net.cybercake.ghost.ffa.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Heal implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if(args.length == 0) {
            if(!(sender instanceof Player)) {
                Utils.commandStatus(sender, Utils.Status.FAILED, "Invalid arguments"); return true;
            }

            healSelf(sender, false);
        }else if(args.length >= 1) {
            if(args[0].equals("-s")) {
                if(!(sender instanceof Player)) {
                    Utils.commandStatus(sender, Utils.Status.FAILED, "Invalid arguments"); return true;
                }
                healSelf(sender, true);
                return true;
            }

            if(Bukkit.getPlayerExact(args[0]) == null) {
                Utils.commandStatus(sender, Utils.Status.FAILED, "Invalid online player"); return true;
            }

            healPlayer(sender, Bukkit.getPlayerExact(args[0]), false);
        }


        return true;
    }

    public static void healPlayer(CommandSender msgTo, Player healWhom, boolean silent) {
        healWhom.setHealth(20.0);
        healWhom.setSaturation(20);
        healWhom.setFoodLevel(20);
        for(PotionEffect effect : healWhom.getActivePotionEffects()) {
            healWhom.removePotionEffect(effect.getType());
        }

        if(!silent) {
            for(Player nearbyPlayer : Bukkit.getOnlinePlayers()) {
                nearbyPlayer.playSound(healWhom.getLocation(), Sound.ENTITY_BLAZE_SHOOT, 0.4F, 1F);
                nearbyPlayer.spawnParticle(Particle.HEART, healWhom.getLocation(), 30, 1.5, 1.5, 1.5, 10);
            }
        }

        Utils.commandStatus(msgTo, Utils.Status.INFO, "You healed " + (msgTo == healWhom ? "yourself" : "&b" + healWhom.getName()));
    }

    public static void healSelf(CommandSender sender, boolean silent) {
        Player player = (Player) sender;
        healPlayer(sender, player, silent);
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if(args.length <= 1) {
            ArrayList<String> returned = CommandManager.getPlayerNames();
            returned.add("-s");
            return CommandManager.createReturnList(returned, args[0]);
        }
        return CommandManager.emptyList;
    }
}
