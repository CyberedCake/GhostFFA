package net.cybercake.ghost.ffa.menus.kits;

import net.cybercake.ghost.ffa.Main;
import net.cybercake.ghost.ffa.commands.maincommand.CommandManager;
import net.cybercake.ghost.ffa.utils.ItemUtils;
import net.cybercake.ghost.ffa.utils.PlayerDataUtils;
import net.cybercake.ghost.ffa.utils.Utils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.TimeZone;

public class KitPreviewer implements Listener {

    // MAIN OPEN INVENTORY
    public static void openMenu(Player player, int kitNumber) {
        Inventory inv = Bukkit.createInventory(player, 9*6, Component.text("Kit #" + kitNumber + ": Previewing"));

        for(int i=45; i<54; i++) {
            inv.setItem(i, ItemUtils.createBasicItemStack(Material.CYAN_STAINED_GLASS_PANE, 1, "&r", CommandManager.emptyList));
        }

        // Load kit from player's uuid data file
        try {
            for (String slotNum : PlayerDataUtils.getPlayerDataConfigFile(player).getConfigurationSection("kits." + kitNumber).getKeys(false)) {
                if(!slotNum.contains("slot")) continue;

                int slot = Integer.parseInt(slotNum.replace("slot", ""));
                inv.setItem(slot, (ItemStack) PlayerDataUtils.getPlayerData(player, "kits." + kitNumber + "." + slotNum));
            }
        } catch (Exception exception) {
            // Catch any potential unwanted exceptions that might cause issues when opening the kit
            player.closeInventory();
            Utils.error(player, "whilst trying to open the kit editor for kit #" + kitNumber + " for {name}", exception);
            return;
        }

        // Load glass information panels (near armor)
        for(int i=41; i<45; i++) { ItemUtils.setInvSlot(inv, i, Material.RED_STAINED_GLASS_PANE, 1, "&7← Armor & Offhand Items", CommandManager.emptyList); }

        if(Long.parseLong(PlayerDataUtils.getPlayerData(player, "kits." + kitNumber + ".lastSetTime") + "") != 0) {
            Date date = new Date((int)PlayerDataUtils.getPlayerData(player, "kits." + kitNumber + ".lastSetTime")*1000L);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM-dd-yyyy HH:mm:ss");
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone(Main.getMainConfig().getString("timeZone")));
            inv.setItem(48, ItemUtils.createBasicItemStack(Material.REDSTONE_TORCH, 1, "&bInformation about Kit #" + kitNumber + ":", Arrays.asList(" ", "&7Last Modified:", "   &e" + Utils.getBetterTimeFromLongs(Utils.getUnix(), (int)PlayerDataUtils.getPlayerData(player, "kits." + kitNumber + ".lastSetTime"), false) + " &eago", "   &d(" + simpleDateFormat.format(date) + "&d)")));
        }
        inv.setItem(49, ItemUtils.createBasicItemStack(Material.ARROW, 1, "&bGo Back", CommandManager.emptyList));
        inv.setItem(50, ItemUtils.createBasicItemStack(Material.CRAFTING_TABLE, 1, "&bEnter Editor", CommandManager.emptyList));

        player.openInventory(inv);
        ItemUtils.currentMenu.put(player.getName(), ItemUtils.Menu.KIT_PREVIEWER);
    }

    // INVENTORY CLICK
    @EventHandler
    public void onInvClick(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        int slot = e.getRawSlot();
        ItemStack item = e.getCurrentItem();
        //ClickType clickType = e.getClick();

        if(ItemUtils.currentMenu.get(player.getName()) == null || (!ItemUtils.currentMenu.get(player.getName()).equals(ItemUtils.Menu.KIT_PREVIEWER))) return;
        e.setCancelled(true);
        if(ItemUtils.invClickCooldown.get(player.getName()) != null) return;
        ItemUtils.invClickCooldown.put(e.getWhoClicked().getName(), Main.getMainConfig().getInt("invClickCooldown"));

        if(slot == 49) {
            KitsMain.openMenu(player);
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1F, 2F);
        }else if(slot == 50) {
            KitViewer.openMenu(player, Integer.parseInt(e.getView().getTitle().replace("Kit #", "").replace(": Previewing", "")));
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1F, 2F);
        }
    }

}
