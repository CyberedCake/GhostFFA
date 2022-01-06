package net.cybercake.ghost.ffa.menus.duels;

import net.cybercake.ghost.ffa.Main;
import net.cybercake.ghost.ffa.commands.maincommand.CommandManager;
import net.cybercake.ghost.ffa.utils.ItemUtils;
import net.cybercake.ghost.ffa.utils.Utils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;

public class DuelMain implements Listener {

    // first(String) = player name
    // second(String) = player name target
    public static HashMap<String, String> duelWhom = new HashMap<>();

    // first(String) = player name
    // second(String) = mode
    public static HashMap<String, String> duelMode = new HashMap<>();

    // first(String) = player name
    // second(String) = arena
    public static HashMap<String, String> duelArena= new HashMap<>();

    // first(String) = player name
    // second(String) = kit
    public static HashMap<String, String> duelKit = new HashMap<>();

    public static void openMenu(Player player, Player dueling) {
        if(duelMode.get(player.getName()) == null) { duelMode.put(player.getName(), Main.getMainConfig().getString("duels.defaultMode")); }
        if(duelArena.get(player.getName()) == null) { duelArena.put(player.getName(), Main.getMainConfig().getString("duels.defaultArena")); }
        if(duelKit.get(player.getName()) == null) { duelKit.put(player.getName(), Main.getMainConfig().getString("duels.defaultKit")); }
        duelWhom.put(player.getName(), dueling.getName());

        Inventory inv = Bukkit.createInventory(null, 9*3, Utils.component("Dueling " + dueling.getName()));

        ItemUtils.fillGUI(inv, 3, Material.CYAN_STAINED_GLASS_PANE, 1, "&r", CommandManager.emptyList);

        for(int slot=0, i=0; i<9; i++) {
            if(slot > 20) {
                slot = slot - (17+9);
            }
            inv.setItem(slot, accept(dueling.getName()));
            slot = slot+9;
        }
        for(int slot=6, i=0; i<9; i++) {
            if(slot > 26) {
                slot = slot - (17+9);
            }
            inv.setItem(slot, cancel(dueling.getName()));
            slot = slot+9;
        }

        inv.setItem(12, ItemUtils.createBasicItemStack(Material.ENDER_EYE, 1, "&bChange Arena", Arrays.asList("paginate:Change the current arena you will be playing on to one more fit for you and your opponent!:&f", " ", "&7Current: &e" + duelArena.get(player.getName()))));
        inv.setItem(13, ItemUtils.createBasicItemStack(Material.NETHER_STAR, 1, "&bChange Mode", Arrays.asList("paginate:Change the mode you're playing on to completely customize the game experience!:&f", " ", "&7Current: &e" + duelMode.get(player.getName()))));
        inv.setItem(14, ItemUtils.createBasicItemStack(Material.CHEST, 1, "&bChange Kit", Arrays.asList("paginate:Change the current kit you will play, including custom kits from your personal kit menu!:&f", " ", "&7Current: &e" + duelKit.get(player.getName()))));

        player.openInventory(inv);
        ItemUtils.currentMenu.put(player.getName(), ItemUtils.Menu.DUEL_PLAYER);
    }

    public static ItemStack accept(String who) {
        return ItemUtils.createBasicItemStack(Material.LIME_STAINED_GLASS_PANE, 1, "&aSend duel request", Arrays.asList("&7To &e" + who));
    }
    public static ItemStack cancel(String who) {
        return ItemUtils.createBasicItemStack(Material.RED_STAINED_GLASS_PANE, 1, "&cCancel duel request", Arrays.asList("&7Against &e" + who));
    }

    @EventHandler
    @SuppressWarnings("deprecation")
    public void onInvClick(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        int slot = e.getRawSlot();
        ItemStack item = e.getCurrentItem();
        ClickType clickType = e.getClick();

        if(ItemUtils.currentMenu.get(player.getName()) == null || (!ItemUtils.currentMenu.get(player.getName()).equals(ItemUtils.Menu.DUEL_PLAYER))) return;
        e.setCancelled(true);
        if(item == null) return;
        if(ItemUtils.invClickCooldown.get(player.getName()) != null) return;
        ItemUtils.invClickCooldown.put(e.getWhoClicked().getName(), Main.getMainConfig().getInt("invClickCooldown"));

        if(item.getItemMeta().getDisplayName().contains("Send duel request")) {
            player.sendMessage(Utils.chat("weee"));
        }else if(item.getItemMeta().getDisplayName().contains("Cancel duel request")) {
            player.sendMessage(Utils.chat("cancelled?!??!?!?!"));
        }
    }

}
