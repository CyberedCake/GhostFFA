package net.cybercake.ghost.ffa.commands.defaultcommands;

import net.cybercake.ghost.ffa.Main;
import net.cybercake.ghost.ffa.commands.maincommand.CommandManager;
import net.cybercake.ghost.ffa.repeatingtasks.CombatTimer;
import net.cybercake.ghost.ffa.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class Combat implements CommandExecutor, TabCompleter {

   @Override
   public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
       if(!(sender instanceof Player)) {
           Main.logError("Only players can execute this command!"); return true;
       }

       Player player = (Player) sender;

       if(CombatTimer.inCombat.get(player.getName()) == null) {
           Utils.commandStatus(player, Utils.Status.FAILED, "You are not in combat"); return true;
       }
       Utils.commandStatus(player, Utils.Status.INFO, "&fIn combat with " + Utils.getFormattedName(CombatTimer.inCombatWith.get(player.getName())) + " &ffor &e" + Math.round(CombatTimer.inCombat.get(player.getName())/20) + " &eseconds&f!");

       return true;
   }

   @Override
   public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
       return CommandManager.emptyList;
   }
}
