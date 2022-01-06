package net.cybercake.ghost.ffa.listeners;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.cybercake.ghost.ffa.Main;
import net.cybercake.ghost.ffa.commands.maincommand.subcommands.VirtualKitRoomAdmin;
import net.cybercake.ghost.ffa.menus.kits.VirtualKitRoom;
import net.cybercake.ghost.ffa.utils.DataUtils;
import net.cybercake.ghost.ffa.utils.Utils;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Content;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;

public class ChatEvent implements Listener {

    public static HashMap<String, Long> lastChat = new HashMap<>();
    public static HashMap<String, String> lastChatContents = new HashMap<>();

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        e.setCancelled(true);

        Player player = e.getPlayer();
        String msg = e.getMessage();

        // Cancel events to prevent typing in actual chat when you are changing names of stuff (i.e. category names)
        if(VirtualKitRoomAdmin.typeInChat.get(player.getName()) != null) {
            try {
                if(Main.virtualKitRoomLoaded.equalsIgnoreCase("unloaded")) {
                    player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1F, 2F);
                    player.sendMessage(Utils.chat("&4Please wait for the Virtual Kit Room to load before typing a name!\n&7&OUsually this only takes a few seconds... then re-enter your message.\n&fAlso, this usually happens if the plugin just loaded or someone else just saved something else (name/item) of the Virtual Kit Room."));
                    return;
                }else if(msg.equalsIgnoreCase("CANCEL")) {
                    player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1F, 2F);
                    player.sendTitle(" ", " ", 20, 50, 20);
                    VirtualKitRoomAdmin.openMenu(player, VirtualKitRoomAdmin.typeInChat.get(player.getName()));
                    return;
                }else if(msg.length() > 20) {
                    player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1F, 2F);
                    player.sendTitle(Utils.chat("&b&lType a different name"), Utils.chat("&cSorry, a maximum of 20 characters is allowed"), 20, 99999, 20);
                    return;
                }

                player.sendTitle(" ", " ", 20, 50, 20);

                DataUtils.setCustomYml("data", "kits.virtualKitRoom.categories.cat" + VirtualKitRoomAdmin.typeInChat.get(player.getName()) + ".categoryName", msg);
                ItemStack physicalItem = DataUtils.getCustomYmlItemStack("data", "kits.virtualKitRoom.categories.cat" + VirtualKitRoomAdmin.typeInChat.get(player.getName()) + ".item");
                ItemMeta physicalItemMeta = physicalItem.getItemMeta();
                physicalItemMeta.setDisplayName(Utils.chat("&b" + msg));
                physicalItem.setItemMeta(physicalItemMeta);
                DataUtils.setCustomYml("data", "kits.virtualKitRoom.categories.cat" + VirtualKitRoomAdmin.typeInChat.get(player.getName()) + ".item", physicalItem);

                VirtualKitRoomAdmin.openMenu(player, VirtualKitRoomAdmin.typeInChat.get(player.getName()));

                VirtualKitRoomAdmin.typeInChat.remove(player.getName());

                Main.virtualKitRoomLoaded = "unloaded";
                Bukkit.getScheduler().runTaskAsynchronously(Main.getPlugin(), () -> {
                    VirtualKitRoom.loadVirtualKitRoom(false);
                });

                return;
            } catch (Exception exception) {
                player.sendTitle(" ", " ", 20, 50, 20);
                Utils.error(player, "whilst trying to process chat request by {name}", exception);
                player.sendMessage(Utils.component("&7&oExiting chat edit mode..."));

                if(VirtualKitRoomAdmin.typeInChat.get(player.getName()) != null) {
                    player.sendActionBar(Component.text(Utils.chat("&4FAILED! &fAttempted category: &e" + VirtualKitRoomAdmin.typeInChat.get(player.getName()))));
                    VirtualKitRoomAdmin.typeInChat.remove(player.getName());
                }
            }
        }

        // Just to prevent errors :)
        if(lastChat.get(player.getName()) == null) {
            lastChat.put(player.getName(), System.currentTimeMillis()-100000); }
        if(lastChatContents.get(player.getName()) == null) {
            lastChatContents.put(player.getName(), " "); }

        // Send message to Minecraft
        if(player.hasPermission("ghostffa.admin.coloredchat")) {
            msg = Utils.chat(e.getMessage()); }

        if(!(player.hasPermission("ghostffa.bypass.chatcooldown")) && (System.currentTimeMillis()-lastChat.get(player.getName())) <= Main.getMainConfig().getLong("chatCooldown")) {
            //long cooldown = (lastChat.get(player.getName())-System.currentTimeMillis()+Main.getMainConfig().getLong("chatCooldown")+1000)/1000;
            TextComponent msgToPlayer = new TextComponent(Utils.chat(Utils.getFormattedName(player) + "&f: &c") + ChatColor.stripColor(msg));
            msgToPlayer.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(Utils.chat("&6This message &ehas been &cremoved &efor potential spam!\n&8(Too many messages within short period of time)"))));
            player.sendMessage(msgToPlayer);
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1F, 1F);
            return; }
        if(!(player.hasPermission("ghostffa.bypass.chatsimilar")) && (lastChatContents.get(player.getName()).equals(e.getMessage()))) {
            TextComponent msgToPlayer = new TextComponent(Utils.chat(Utils.getFormattedName(player) + "&f: &c") + ChatColor.stripColor(msg));
            msgToPlayer.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(Utils.chat("&6This message &ehas been &cremoved &efor potential spam!\n&8(Message too similar to last)"))));
            player.sendMessage(msgToPlayer);
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1F, 1F);
            return; }

        lastChat.put(player.getName(), System.currentTimeMillis());
        lastChatContents.put(player.getName(), e.getMessage());
        String format = Utils.chat(Utils.getFormattedName(player) + "&f: ") + msg;
        for(Player player1 : Bukkit.getOnlinePlayers()) {
            player1.sendMessage(format);
        }
        Bukkit.getLogger().info("[GhostFFA Chat] " + format);

        // Send message to Discord
    }



}
