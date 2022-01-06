package net.cybercake.ghost.ffa.commands.admincommands;

import net.cybercake.ghost.ffa.Main;
import net.cybercake.ghost.ffa.commands.maincommand.CommandManager;
import net.cybercake.ghost.ffa.utils.Utils;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTameEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class Wolf implements CommandExecutor, TabCompleter, Listener {

    // I GOT BORED ONE DAY SO I MADE THE THINGS BELOW THIS (September 26th, 2021 @ 9-10pm)
    // ALSO IF FUTURE ME IS READING THIS PLEASE GO DONATE TO TECHNOBLADE'S CANCER RESEARCH:
    //                         https://youtu.be/GHKsjtB9Q30

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player)) {
            Main.logError("Console cannot have any pets, sorry :("); return true;
        }

        Player player = (Player) sender;
        World world = player.getWorld();
        Location location = player.getLocation();
        org.bukkit.entity.Wolf wolf = (org.bukkit.entity.Wolf) world.spawnEntity(location, EntityType.WOLF);

        wolf.setOwner(player);
        wolf.setAngry(false);

        spawnWolf(player, wolf);

        player.playSound(player.getLocation(), Sound.ENTITY_WOLF_AMBIENT, 1F, 1F);

        return true;
    }

    public static void spawnWolf(Player owner, org.bukkit.entity.Wolf wolf) {
        wolf.setCustomNameVisible(true);

        String chooseGender = (System.currentTimeMillis() % 2 == 0 ? "male" : "female");

        try {
            URL url = new URL((chooseGender.equals("male") ? Main.getMainConfig().getString("fun.wolfSpawning.api-male") : Main.getMainConfig().getString("fun.wolfSpawning.api-female")));
            URLConnection conn = url.openConnection();
            conn.connect();
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            ArrayList<String> names = new ArrayList<>();

            while((inputLine = in.readLine()) != null) {
                for(String str : inputLine.split("[\"]")) {
                    if(str.contains(",") || str.contains("]") || str.contains("[")) continue;
                    names.add(str.split("_")[0]);
                }
            }

            in.close();

            wolf.setCustomName((chooseGender.equals("male") ? ChatColor.BLUE : ChatColor.LIGHT_PURPLE) + names.get(0));

            wolf.setCollarColor((chooseGender.equals("male") ? DyeColor.BLUE : DyeColor.PINK));
        } catch (Exception exception) {
            Utils.error(owner, "whilst trying to tame the mob for {name}", exception); wolf.setCustomName("[]");
        }
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return CommandManager.emptyList;
    }

    @EventHandler
    public void onTame(EntityTameEvent e) {
        spawnWolf((Player)e.getOwner(), (org.bukkit.entity.Wolf)e.getEntity());
    }
}
