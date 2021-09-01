package net.cybercake.ghost.ffa.menus.kits;

import net.cybercake.ghost.ffa.Main;
import net.cybercake.ghost.ffa.commands.maincommand.CommandManager;
import net.cybercake.ghost.ffa.commands.maincommand.subcommands.VirtualKitRoomAdmin;
import net.cybercake.ghost.ffa.utils.DataUtils;
import net.cybercake.ghost.ffa.utils.ItemUtils;
import net.cybercake.ghost.ffa.utils.Utils;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.HashMap;

public class VirtualKitRoom implements Listener {

    public static HashMap<String, Integer> oldCurrentKit = new HashMap<>();
    public static HashMap<String, Integer> currentCategory = new HashMap<>();

    public static HashMap<Integer, Inventory> cachedKitRoomItems = new HashMap<>();
    public static int lastSave;

    // MAIN OPEN INVENTORY
    public static void openMenu(Player player, int category, int oldCurrentKitInt) {
        if(Main.virtualKitRoomLoaded.equalsIgnoreCase("unloaded")) {
            Utils.commandStatus(player, Utils.Status.TECHNICAL_FAULT, "The Virtual Kit Room isn't loaded yet!");
            player.closeInventory();
            return;
        }else if(Main.virtualKitRoomLoaded.equalsIgnoreCase("failed")) {
            Utils.commandStatus(player, Utils.Status.TECHNICAL_FAULT, "The Virtual Kit Room FAILED to load!");
            player.closeInventory();
            return;
        }else if(cachedKitRoomItems.get(category) == null) {
            player.closeInventory();
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1F, 1F);
            player.sendMessage(Utils.chat("&cAn error occurred whilst attempting to load that menu! Please try again later: &8category=" + category));
            return;
        }
        ItemStack[] inventoryContent = cachedKitRoomItems.get(category).getContents();
        Inventory inventory = Bukkit.createInventory(player, 9*6, Component.text("Virtual Kit Room (" + category + "/5)"));
        for(int i=0; i<inventoryContent.length; i++) {
            inventory.setItem(i, inventoryContent[i]);
        }
        player.openInventory(inventory);
        oldCurrentKit.put(player.getName(), oldCurrentKitInt);
        ItemUtils.currentMenu.put(player.getName(), ItemUtils.Menu.VIRTUAL_KIT_ROOM);
        VirtualKitRoom.currentCategory.put(player.getName(), category);
    }

    // ON INVENTORY CLICK
    @EventHandler
    public void onInvClick(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        int slot = e.getRawSlot();
        ItemStack item = e.getCurrentItem();
        //ClickType clickType = e.getClick();

        if(ItemUtils.currentMenu.get(player.getName()) == null || (!ItemUtils.currentMenu.get(player.getName()).equals(ItemUtils.Menu.VIRTUAL_KIT_ROOM))) return;
        if(!Utils.isBetweenEquals(slot, 45, 53)) return;
        e.setCancelled(true);
        if(ItemUtils.invClickCooldown.get(player.getName()) != null) return;
        ItemUtils.invClickCooldown.put(e.getWhoClicked().getName(), Main.getMainConfig().getInt("invClickCooldown"));

        if(slot == 45) {
            KitViewer.openMenu(player, oldCurrentKit.get(player.getName()));
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1F, 2F);
        }else if(Utils.isBetweenEquals(slot, 47, 51)) {
            if(VirtualKitRoom.currentCategory.get(player.getName()) == VirtualKitRoomAdmin.getCategoryFromSlot(slot)) return;
            openMenu(player, VirtualKitRoomAdmin.getCategoryFromSlot(slot), oldCurrentKit.get(player.getName()));
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_IRON_XYLOPHONE, 1F, 2F);
        }else if(slot == 53) {
            int category = currentCategory.get(player.getName()); int currentKit = oldCurrentKit.get(player.getName());
            openMenu(player, category, currentKit);
            player.playSound(player.getLocation(), Sound.ITEM_BOTTLE_FILL, 1F, 1F);
        }
    }

    // LOAD FROM MAIN CLASS
    // Synchronous loading so may be slow --- only access when starting up!
    public static void loadVirtualKitRoom(boolean broadcast) {
        Main.logInfo("Loading the Virtual Kit Room...");
        long mss = System.currentTimeMillis();
        try {
            for(int categoryNumber=1; categoryNumber<6; categoryNumber++) {
                Inventory inventory = Bukkit.createInventory(Bukkit.getPlayerExact("CyberedCake"), 9*6, Component.text("Virtual Kit Room (" + categoryNumber + "/5)"));

                for(int i=0; i<45; i++) {
                    inventory.setItem(i, DataUtils.getCustomYmlItemStack("data", "kits.virtualKitRoom.categories.cat" + categoryNumber + ".slot" + i));
                }

                for(int i=45; i<54; i++) {
                    inventory.setItem(i, ItemUtils.createBasicItemStack(Material.CYAN_STAINED_GLASS_PANE, 1, "&r", CommandManager.emptyList));
                }

                inventory.setItem(45, ItemUtils.createBasicItemStack(Material.ARROW, 1, "&bGo Back", CommandManager.emptyList));

                inventory.setItem(47, DataUtils.getCustomYmlItemStack("data", "kits.virtualKitRoom.categories.cat1.item"));
                inventory.setItem(48, DataUtils.getCustomYmlItemStack("data", "kits.virtualKitRoom.categories.cat2.item"));
                inventory.setItem(49, DataUtils.getCustomYmlItemStack("data", "kits.virtualKitRoom.categories.cat3.item"));
                inventory.setItem(50, DataUtils.getCustomYmlItemStack("data", "kits.virtualKitRoom.categories.cat4.item"));
                inventory.setItem(51, DataUtils.getCustomYmlItemStack("data", "kits.virtualKitRoom.categories.cat5.item"));

                ItemStack currentCategory = DataUtils.getCustomYmlItemStack("data", "kits.virtualKitRoom.categories.cat" + categoryNumber + ".item");
                ItemMeta currentCategoryMeta = currentCategory.getItemMeta();
                currentCategoryMeta.displayName(Component.text(ChatColor.GREEN + ChatColor.stripColor(currentCategoryMeta.getDisplayName())));
                currentCategoryMeta.lore(Collections.singletonList(Component.text(Utils.chat("&7&oCurrently selected!"))));
                currentCategoryMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1, true);
                currentCategoryMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_PLACED_ON, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_POTION_EFFECTS, ItemFlag.HIDE_DESTROYS, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DYE);
                currentCategory.setItemMeta(currentCategoryMeta);
                inventory.setItem(VirtualKitRoomAdmin.getSlotFromCategory(categoryNumber), currentCategory);

                inventory.setItem(53, ItemUtils.createBasicItemStack(Material.CLOCK, 1, "&bRefill Menu", CommandManager.emptyList));

                VirtualKitRoom.cachedKitRoomItems.put(categoryNumber, inventory);

                DataUtils.setCustomYml("data", "kits.virtualKitRoom.lastSaved", Utils.getUnix());
            }
            Main.logInfo("Loaded Virtual Kit Room successfully in " + (System.currentTimeMillis()-mss) + "ms!");
            if(broadcast) {
                Main.broadcastFormatted("Successfully loaded the Virtual Kit Room in &a" + (System.currentTimeMillis()-mss) + "&ams&f!", true);
            }
            Main.virtualKitRoomLoaded = "loaded";
        } catch (Exception exception) {
            Main.logError(Main.getPluginPrefix() + " An error occurred whilst loading the Virtual Kit Room: " + exception);
            Bukkit.getOnlinePlayers().forEach(player -> {
                if (player.hasPermission("ghostffa.*")) {
                    player.sendMessage(Utils.chat("&4[Server Error] &cFailed to load the Virtual Kit Room: " + exception));
                }});
            Main.logError(" ");
            Main.logError("Stack trace below:");
            Utils.printBetterStackTrace(exception);
            Main.virtualKitRoomLoaded = "failed";
        }
    }

}
