package net.cybercake.ghost.ffa.menus.kits;

import net.cybercake.ghost.ffa.commands.maincommand.CommandManager;
import net.cybercake.ghost.ffa.Main;
import net.cybercake.ghost.ffa.utils.ItemUtils;
import net.cybercake.ghost.ffa.utils.PlayerDataUtils;
import net.cybercake.ghost.ffa.utils.Utils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class KitViewer implements Listener {

    // VARIABLES
    private static final HashMap<String, Map<Integer, ItemStack>> itemsWhenOpened = new HashMap<>();
    public static HashMap<String, Integer> currentKit = new HashMap<>();
    public static HashMap<String, Integer> secondsLeftClearKit = new HashMap<>();

    private Integer assignedTaskId;

    // MAIN OPEN INVENTORY
    public static void openMenu(Player player, int kitNumber) {
        Inventory inv = Bukkit.createInventory(player, 9*6, Component.text("Kit #" + kitNumber + ": Modifying"));

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
            Utils.error(player, "whilst trying to open the kit editor for " + kitNumber + " for {name}", exception);
            return;
        }

        // Load glass information panels (near armor)
        for(int i=41; i<45; i++) { ItemUtils.setInvSlot(inv, i, Material.RED_STAINED_GLASS_PANE, 1, "&7← Armor & Offhand Items", Arrays.asList(" ", "&fOrder goes like this:", "&e(left to right) &6Helmet, Chestplate, Leggings,", "                  &6Boots, Offhand")); }
        // Load glass at bottom
        for(int i=45; i<54; i++) { ItemUtils.setInvSlot(inv, i, Material.CYAN_STAINED_GLASS_PANE, 1, "&r", CommandManager.emptyList); }
        // Load buttons at bottom
        ItemUtils.setInvSlot(inv, 51, Material.NAME_TAG, 1, "&bMake Public", CommandManager.emptyList);
        if(!player.hasPermission("ghostffa.kits.vip")) {
            ItemUtils.setInvSlot(inv, 51, Material.NAME_TAG, 1, "&cMake Public", Arrays.asList("&7&oRequires &6&l&oVIP &7&orank!"));
        }
        if(Main.virtualKitRoomLoaded.equalsIgnoreCase("unloaded")) {
            inv.setItem(50, ItemUtils.createBasicItemStack(Material.BEDROCK, 1, "&eLoading, please give us a moment!", CommandManager.emptyList));
        }else if(Main.virtualKitRoomLoaded.equalsIgnoreCase("failed")) {
            inv.setItem(50, ItemUtils.createBasicItemStack(Material.BARRIER, 1, "&cFailed to load!", Arrays.asList("&7&oA technical fault has occurred!")));
        }else{
            inv.setItem(50, ItemUtils.createBasicItemStack(Material.RESPAWN_ANCHOR, 1, "&bVirtual Kit Room", CommandManager.emptyList));
        }
        if(!itemsInMenu(player)) {
            ItemUtils.setInvSlot(inv, 49, Material.REDSTONE_BLOCK, 1, "&8Loading...", CommandManager.emptyList);
        }else{
            ItemUtils.setInvSlot(inv, 49, Material.REDSTONE_BLOCK, 1, "&8Loading...", CommandManager.emptyList);
        }
        ItemUtils.setInvSlot(inv, 48, Material.CHEST, 1, "&bImport Inventory", CommandManager.emptyList);
        ItemUtils.setInvSlot(inv, 47, Material.WATER_BUCKET, 1, "&bClear Kit", CommandManager.emptyList);

        ItemUtils.setInvSlot(inv, 45, Material.ARROW, 1, "&bSave and Leave Editor", CommandManager.emptyList);

        player.openInventory(inv);
        ItemUtils.currentMenu.put(player.getName(), ItemUtils.Menu.KIT_VIEWER);
        currentKit.put(player.getName(), kitNumber);

        HashMap<Integer, ItemStack> items = new HashMap<>();
        for(int i=0; i<36; i++) {
            items.put(i, player.getOpenInventory().getItem(i));
        }
        itemsWhenOpened.put(player.getName(), items);
    }

    // ON INVENTORY CLICK
    @EventHandler
    public void onInvClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        int slot = e.getRawSlot();
        ItemStack item = e.getCurrentItem();
        //ClickType clickType = e.getClick();

        if(ItemUtils.currentMenu.get(p.getName()) == null || (!ItemUtils.currentMenu.get(p.getName()).equals(ItemUtils.Menu.KIT_VIEWER))) return;
        if(item == null) return;
        if(!Utils.isBetweenEquals(slot, 41, 53)) return;
        e.setCancelled(true);
        if(ItemUtils.invClickCooldown.get(p.getName()) != null) return;

        ItemUtils.invClickCooldown.put(e.getWhoClicked().getName(), Main.getMainConfig().getInt("invClickCooldown"));

        if(slot == 45) {
            p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1F, 2F);
            saveKitConfiguration(p, currentKit.get(p.getName()), false);
            KitsMain.openMenu(p);
        }else if(slot == 47) {
            if(secondsLeftClearKit.get(p.getName()) != null) {
                clearKitConfiguration(p, currentKit.get(p.getName()));
                p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_SPLASH, 1F, 1F);
                secondsLeftClearKit.remove(p.getName());
                Bukkit.getScheduler().cancelTask(this.assignedTaskId);
                p.getOpenInventory().setItem(47, ItemUtils.createBasicItemStack( Material.WATER_BUCKET, 1, "&bClear Kit", CommandManager.emptyList));
                return;
            }
            secondsLeftClearKit.put(p.getName(), 5);
            this.assignedTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getPlugin(), new Runnable() {
                @Override
                public void run() {
                    if(secondsLeftClearKit.get(p.getName()) == null || secondsLeftClearKit.get(p.getName()) < 1) {
                        if(secondsLeftClearKit.get(p.getName()) != null) {
                            secondsLeftClearKit.remove(p.getName());
                        }

                        p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_LAND, 1F, 2F);
                        p.getOpenInventory().setItem(47, ItemUtils.createBasicItemStack( Material.WATER_BUCKET, 1, "&bClear Kit", CommandManager.emptyList));
                        Bukkit.getScheduler().cancelTask(assignedTaskId);
                        return;
                    }
                    p.getOpenInventory().setItem(47, ItemUtils.createBasicShinyItemStack(Material.WATER_BUCKET, secondsLeftClearKit.get(p.getName()), "&aClear Kit?", Arrays.asList("paginate:&7&oClick here to clear this kit's contents!:&7", " ", "paginate:&fYou have &b" + secondsLeftClearKit.get(p.getName()) + " &b" + (secondsLeftClearKit.get(p.getName()) != 1 ? "seconds" : "second") + " &fto confirm!:&f")));
                    secondsLeftClearKit.put(p.getName(), secondsLeftClearKit.get(p.getName())-1);
                }
            }, 0L, 20L);
        }else if(slot == 48) {
            importPlayerInventory(p);
            p.playSound(p.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1F, 1F);
        }else if(slot == 49) {
            if(itemsInMenu(p)) {
                saveKitConfiguration(p, currentKit.get(p.getName()), true);
                return;
            }

            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1F, 0.1F);
        }else if(slot == 50) {
            int currentCurrentKit = currentKit.get(p.getName());
            VirtualKitRoom.openMenu(p, 1, currentKit.get(p.getName()));
            p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1F, 2F);
            Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(), () -> VirtualKitRoom.openMenu(p, 1, currentCurrentKit), 1L);
        }

    }

    // CLEAR CURRENT KIT & DATA
    public void clearKitConfiguration(Player player, int kitNumber) {
        for(int slot=0; slot<41; slot++) {
            player.getOpenInventory().setItem(slot, new ItemStack(Material.AIR));
        }
        saveKitConfiguration(player, kitNumber, false);
    }

    // SAVE CURRENT KIT CONFIGURATION
    public static void saveKitConfiguration(Player player, int kitNumber, boolean showText) {
        for(int i=0; i<41; i++) {
            PlayerDataUtils.setPlayerData(player, "kits." + kitNumber + ".slot" + i, player.getOpenInventory().getItem(i));
        }

        PlayerDataUtils.setPlayerData(player, "kits." + kitNumber + ".lastSetTime", Utils.getUnix());
        if(showText) {
            player.sendTitle(Utils.chat("&fSaved &bKit #" + kitNumber), Utils.chat("&7&oApply it by using &f&o/kit " + kitNumber + "&7&o!"), 0, 80, 20);
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1F, 2F);
            player.closeInventory();
            Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(), new Runnable() {
                @Override
                public void run() {
                    player.closeInventory();
                }
            }, 1L);
        }
    }

    // IMPORT INVENTORY
    public void importPlayerInventory(Player player) {
        for(int i=0; i<36; i++) {
            if(player.getInventory().getItem(i) != null) {
                ItemStack itemInInventory = player.getOpenInventory().getItem(i);
                ItemStack itemInGUI = player.getInventory().getItem(i);
                player.getOpenInventory().setItem(i, itemInGUI);
                player.getInventory().setItem(i, itemInInventory);
            }
        }
        ItemStack itemInInventory;
        ItemStack itemInGUI;

        itemInInventory = player.getOpenInventory().getItem(36);
        itemInGUI = player.getInventory().getHelmet();
        player.getOpenInventory().setItem(36, itemInGUI);
        player.getInventory().setHelmet(itemInInventory);

        itemInInventory = player.getOpenInventory().getItem(37);
        itemInGUI = player.getInventory().getChestplate();
        player.getOpenInventory().setItem(37, itemInGUI);
        player.getInventory().setChestplate(itemInInventory);

        itemInInventory = player.getOpenInventory().getItem(38);
        itemInGUI = player.getInventory().getLeggings();
        player.getOpenInventory().setItem(38, itemInGUI);
        player.getInventory().setLeggings(itemInInventory);

        itemInInventory = player.getOpenInventory().getItem(39);
        itemInGUI = player.getInventory().getBoots();
        player.getOpenInventory().setItem(39, itemInGUI);
        player.getInventory().setBoots(itemInInventory);

        itemInInventory = player.getOpenInventory().getItem(40);
        itemInGUI = player.getInventory().getItemInOffHand();
        player.getOpenInventory().setItem(40, itemInGUI);
        player.getInventory().setItemInOffHand(itemInInventory);
    }

    // DETECT IF ITEMS IN MENU
    public static boolean itemsInMenu(Player player) {
        for(int i=0; i<41; i++) {
            if(player.getOpenInventory().getItem(i) != null) {
                return true;
            }
        }
        return false;
    }

    // PREVENT DROPPING WHEN IN INVENTORY
    // (this is an easy way to fix some exploits)
    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        Player player = e.getPlayer();

        if(currentKit.get(player.getName()) == null) return;

        e.setCancelled(true);
    }

}
