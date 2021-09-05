package net.cybercake.ghost.ffa.commands.maincommand.subcommands;

import net.cybercake.ghost.ffa.Main;
import net.cybercake.ghost.ffa.commands.maincommand.SubCommand;
import net.cybercake.ghost.ffa.commands.worldscommand.CommandManager;
import net.cybercake.ghost.ffa.menus.kits.VirtualKitRoom;
import net.cybercake.ghost.ffa.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class ReloadKitRoom extends SubCommand {

     public ReloadKitRoom() {
         super("kitroomreload", "ghostffa.subcommand.kitroomreload", "Reloads the virtual kit room.", "/ghostffa kitroomreload", "reloadkitroom", "rlkitroom", "rlkr", "kitroomrl");
     }

    @Override
    public void perform(CommandSender sender, String[] args, Command command) {
        if(Main.virtualKitRoomLoaded.equalsIgnoreCase("unloaded")) {
            Utils.commandStatus(sender, Utils.Status.FAILED, "The Kit Room is already reloading/loading"); return;
        }
        long mss = System.currentTimeMillis();

        Utils.commandStatus(sender, Utils.Status.INFO, "&7&oReloading the kit room... please wait");

        Main.virtualKitRoomLoaded = "unloaded";
        try {
            Bukkit.getScheduler().runTaskAsynchronously(Main.getPlugin(), () -> {
                VirtualKitRoomAdmin.virtualKitRoomLoader();
                VirtualKitRoom.loadVirtualKitRoom(false);
                Utils.commandStatus(sender, Utils.Status.INFO, "Successfully loaded the Virtual Kit Room in &b" + (System.currentTimeMillis()-mss) + "&bms");
                if(sender instanceof Player) {
                    Player player = (Player) sender;
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 1F, 1F);
                }
            });
        } catch (Exception exception) {
            Main.logError(Main.getPluginPrefix() + ": An error occurred whilst loading the Virtual Kit Room: " + exception);
            Bukkit.getOnlinePlayers().forEach(player -> {
                if (player.hasPermission("ghostffa.*")) {
                    player.sendMessage(Utils.chat("&4[Server Error] &cFailed to load the Virtual Kit Room: " + exception));
                }});
            Main.virtualKitRoomLoaded = "failed";
            Utils.commandStatus(sender, Utils.Status.TECHNICAL_FAULT, "The Virtual Kit Room FAILED to reload");
        }
    }

    @Override
    public List<String> tab(CommandSender sender, String[] args) {
        return CommandManager.emptyList;
    }
}
