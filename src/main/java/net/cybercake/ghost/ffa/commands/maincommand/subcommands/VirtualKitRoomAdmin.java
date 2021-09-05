package net.cybercake.ghost.ffa.commands.maincommand.subcommands;

import net.cybercake.ghost.ffa.Main;
import net.cybercake.ghost.ffa.commands.maincommand.CommandManager;
import net.cybercake.ghost.ffa.commands.maincommand.SubCommand;
import net.cybercake.ghost.ffa.menus.kits.VirtualKitRoom;
import net.cybercake.ghost.ffa.utils.DataUtils;
import net.cybercake.ghost.ffa.utils.ItemUtils;
import net.cybercake.ghost.ffa.utils.Utils;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.ChatPaginator;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class VirtualKitRoomAdmin extends SubCommand implements Listener {

    public static String playerEditing;
    public static HashMap<String, Integer> typeInChat = new HashMap<>();

    public VirtualKitRoomAdmin() {
        super("kitroomadmin", "ghostffa.subcommand.kitroomadmin", "Allows you to change the Virtual Kit Room.", "/ghostffa kitroomadmin", new String[]{"kra", "kitroom"});
    }

    @Override
    public void perform(CommandSender sender, String[] args, Command command) {
        if(!(sender instanceof Player)) {
            Main.logError("Only players can execute this command"); return; }
        Player player = (Player) sender;
        if(Main.virtualKitRoomLoaded.equalsIgnoreCase("unloaded")) {
            Utils.commandStatus(player, Utils.Status.TECHNICAL_FAULT, "The Virtual Kit Room isn't loaded yet!");
            player.closeInventory(); return;
        }else if(Main.virtualKitRoomLoaded.equalsIgnoreCase("failed")) {
            Utils.commandStatus(player, Utils.Status.TECHNICAL_FAULT, "The Virtual Kit Room FAILED to load!");
            player.closeInventory(); return;
        }else if(playerEditing != null) {
            player.sendMessage(Utils.chat("&c" + playerEditing + " is already editing the Administrator Virtual Kit Room, please wait for them to finish editing it!")); return; }

        openMenu(player, 1);
    }

    public static void openMenu(Player player, int category) {
        playerEditing = player.getName();
        Inventory inv = Bukkit.createInventory(player, 9*6, Component.text("Admin: Virtual Kit Room (" + category + "/6)"));
        playerEditing = player.getName();

        for(int slot=45; slot<54; slot++) {
            if(!Utils.isBetweenEquals(slot, 48, 53)) {
                inv.setItem(slot, ItemUtils.createBasicItemStack(Material.CYAN_STAINED_GLASS_PANE, 1, "&r", CommandManager.emptyList));
            }
        }

        inv.setItem(45, ItemUtils.createBasicItemStack(Material.EMERALD_BLOCK, 1, "&bSave Current Kit Room Configuration", CommandManager.emptyList));

        inv.setItem(46, ItemUtils.createBasicItemStack(Material.REDSTONE_TORCH, 1, "&bInformation:", Arrays.asList(ChatPaginator.wordWrap(Utils.chat("&7Categories work like this: You can left click to switch what current category you're on. You can right click to change the name of a category, must be under 30 characters. You can middle click the icon of a category with a block to change the icon."), 40))));

        inv.setItem(48, ItemUtils.createBasicItemStack(Material.BEDROCK, 1, "&8Loading...", CommandManager.emptyList));
        inv.setItem(49, ItemUtils.createBasicItemStack(Material.BEDROCK, 1, "&8Loading...", CommandManager.emptyList));
        inv.setItem(50, ItemUtils.createBasicItemStack(Material.BEDROCK, 1, "&8Loading...", CommandManager.emptyList));
        inv.setItem(51, ItemUtils.createBasicItemStack(Material.BEDROCK, 1, "&8Loading...", CommandManager.emptyList));
        inv.setItem(52, ItemUtils.createBasicItemStack(Material.BEDROCK, 1, "&8Loading...", CommandManager.emptyList));
        inv.setItem(53, ItemUtils.createBasicItemStack(Material.BEDROCK, 1, "&8Loading...", CommandManager.emptyList));

        player.openInventory(inv);
        playerEditing = player.getName();
        ItemUtils.currentMenu.put(player.getName(), ItemUtils.Menu.ADMIN_VIRTUAL_KIT_ROOM);
        VirtualKitRoom.currentCategory.put(player.getName(), category);
        Bukkit.getScheduler().runTaskLaterAsynchronously(Main.getPlugin(), () -> {

            for (String slotNum : DataUtils.getCustomYmlFileConfig("data").getConfigurationSection("kits.virtualKitRoom.categories.cat" + category).getKeys(false)) {
                if(!slotNum.contains("slot")) continue;

                int slot = Integer.parseInt(slotNum.replace("slot", ""));
                player.getOpenInventory().setItem(slot, DataUtils.getCustomYmlItemStack("data", "kits.virtualKitRoom.categories.cat" + category + ".slot" + slot));
            }

            player.getOpenInventory().setItem(48, DataUtils.getCustomYmlItemStack("data", "kits.virtualKitRoom.categories.cat1.item"));
            player.getOpenInventory().setItem(49, DataUtils.getCustomYmlItemStack("data", "kits.virtualKitRoom.categories.cat2.item"));
            player.getOpenInventory().setItem(50, DataUtils.getCustomYmlItemStack("data", "kits.virtualKitRoom.categories.cat3.item"));
            player.getOpenInventory().setItem(51, DataUtils.getCustomYmlItemStack("data", "kits.virtualKitRoom.categories.cat4.item"));
            player.getOpenInventory().setItem(52, DataUtils.getCustomYmlItemStack("data", "kits.virtualKitRoom.categories.cat5.item"));
            player.getOpenInventory().setItem(53, DataUtils.getCustomYmlItemStack("data", "kits.virtualKitRoom.categories.cat6.item"));
        }, 10L);
    }

    @EventHandler
    public void onInvClick(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        int slot = e.getRawSlot();
        ItemStack item = e.getCurrentItem();
        ClickType clickType = e.getClick();

        if(ItemUtils.currentMenu.get(player.getName()) == null || (!ItemUtils.currentMenu.get(player.getName()).equals(ItemUtils.Menu.ADMIN_VIRTUAL_KIT_ROOM))) return;
        if(!Utils.isBetweenEquals(slot, 45, 53)) return;
        e.setCancelled(true);
        if(ItemUtils.invClickCooldown.get(player.getName()) != null) return;
        ItemUtils.invClickCooldown.put(e.getWhoClicked().getName(), Main.getMainConfig().getInt("invClickCooldown"));

        if(item == null) { if(item.getItemMeta().getDisplayName().contains("Loading...")) return; }

        if(slot == 45) {
            saveKitRoomConfiguration(player, VirtualKitRoom.currentCategory.get(player.getName()), true);
        }else if(Utils.isBetweenEquals(slot, 48, 53)) {
            int getCategoryFromSlot = getCategoryFromSlot(slot);

            if(clickType.equals(ClickType.LEFT) || clickType.equals(ClickType.SHIFT_LEFT)) {
                if(VirtualKitRoom.currentCategory.get(player.getName()) == getCategoryFromSlot(slot)) return;

                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_IRON_XYLOPHONE, 1F, 2F);

                player.getOpenInventory().setItem(48, ItemUtils.createBasicItemStack(Material.BEDROCK, 1, "&8Loading...", CommandManager.emptyList));
                player.getOpenInventory().setItem(49, ItemUtils.createBasicItemStack(Material.BEDROCK, 1, "&8Loading...", CommandManager.emptyList));
                player.getOpenInventory().setItem(50, ItemUtils.createBasicItemStack(Material.BEDROCK, 1, "&8Loading...", CommandManager.emptyList));
                player.getOpenInventory().setItem(51, ItemUtils.createBasicItemStack(Material.BEDROCK, 1, "&8Loading...", CommandManager.emptyList));
                player.getOpenInventory().setItem(52, ItemUtils.createBasicItemStack(Material.BEDROCK, 1, "&8Loading...", CommandManager.emptyList));
                player.getOpenInventory().setItem(53, ItemUtils.createBasicItemStack(Material.BEDROCK, 1, "&8Loading...", CommandManager.emptyList));

                Bukkit.getScheduler().runTaskLater(Main.getPlugin(), () -> {
                    openMenu(player, getCategoryFromSlot);
                }, 1L);
            } else if (clickType.equals(ClickType.MIDDLE)) {
                if (Main.virtualKitRoomLoaded.equalsIgnoreCase("unloaded")) {
                    player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1F, 2F);
                    player.sendMessage(Utils.chat("&4Please wait for the Virtual Kit Room to load before typing a name!\n&7&OUsually this only takes a few seconds... then re-enter your message.\n&fAlso, this usually happens if the plugin just loaded or someone else just saved something else (name/item) of the Virtual Kit Room."));
                    return;
                } else if (player.getItemOnCursor().equals(new ItemStack(Material.AIR))) return;

                ItemStack cursor = player.getItemOnCursor();
                ItemStack newCursor = new ItemStack(cursor.getType(), getCategoryFromSlot);
                ItemMeta newCursorMeta = newCursor.getItemMeta();

                if(newCursorMeta == null) return;

                newCursorMeta.displayName(Component.text(Utils.chat("&b" + ChatColor.stripColor(DataUtils.getCustomYmlString("data", "kits.virtualKitRoom.categories.cat" + getCategoryFromSlot + ".categoryName")))));
                newCursorMeta.setLore(CommandManager.emptyList);
                newCursorMeta.addItemFlags(ItemFlag.HIDE_DYE, ItemFlag.HIDE_DESTROYS, ItemFlag.HIDE_PLACED_ON, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES);
                newCursor.setItemMeta(newCursorMeta);

                DataUtils.setCustomYml("data", "kits.virtualKitRoom.categories.cat" + getCategoryFromSlot + ".item", newCursor);
                player.getOpenInventory().setItem(slot, newCursor);

                player.setItemOnCursor(new ItemStack(Material.AIR, 1));

                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1F, 2F);

                Main.virtualKitRoomLoaded = "unloaded";
                Bukkit.getScheduler().runTaskAsynchronously(Main.getPlugin(), () -> {
                    VirtualKitRoom.loadVirtualKitRoom(false);
                });
            } else if (clickType.equals(ClickType.RIGHT) || clickType.equals(ClickType.SHIFT_RIGHT)) {
                player.closeInventory();

                player.playSound(player.getLocation(), Sound.BLOCK_BAMBOO_BREAK, 100F, 1F);
                player.sendTitle(Utils.chat("&bType name for Category #" + getCategoryFromSlot), Utils.chat("&fin your chat box"), 20, 99999, 20);

                typeInChat.put(player.getName(), getCategoryFromSlot);
            }
        }
    }

    @Override
    public List<String> tab(CommandSender sender, String[] args) {
        return CommandManager.emptyList;
    }

    // Maybe find a way to simplify these two methods?
    // They use very bad-looking code
    public static int getCategoryFromSlot(int slot) {
        if(slot == 48) { return 1; }
        if(slot == 49) { return 2; }
        if(slot == 50) { return 3; }
        if(slot == 51) { return 4; }
        if(slot == 52) { return 5; }
        if(slot == 53) { return 6; }
        return -1;
    }
    public static int getSlotFromCategory(int category) {
        if(category == 1) { return 48; }
        if(category == 2) { return 49; }
        if(category == 3) { return 50; }
        if(category == 4) { return 51; }
        if(category == 5) { return 52; }
        if(category == 6) { return 53; }
        return -1;
    }

    public static void saveKitRoomConfiguration(Player player, int categoryNumber, boolean showText) {
        Inventory inventory = Bukkit.createInventory(player, 9*6, Component.text("Virtual Kit Room (" + categoryNumber + "/6)"));

        for(int i=0; i<45; i++) {
            DataUtils.setCustomYml("data", "kits.virtualKitRoom.categories.cat" + categoryNumber + ".slot" + i, player.getOpenInventory().getItem(i));
            inventory.setItem(i, player.getOpenInventory().getItem(i));
        }

        for(int i=45; i<54; i++) {
            inventory.setItem(i, ItemUtils.createBasicItemStack(Material.CYAN_STAINED_GLASS_PANE, 1, "&r", CommandManager.emptyList));
        }

        inventory.setItem(45, ItemUtils.createBasicItemStack(Material.ARROW, 1, "&bGo Back", CommandManager.emptyList));
        inventory.setItem(46, ItemUtils.createBasicItemStack(Material.CLOCK, 1, "&bRefill Menu", CommandManager.emptyList));

        inventory.setItem(48, DataUtils.getCustomYmlItemStack("data", "kits.virtualKitRoom.categories.cat1.item"));
        inventory.setItem(49, DataUtils.getCustomYmlItemStack("data", "kits.virtualKitRoom.categories.cat2.item"));
        inventory.setItem(50, DataUtils.getCustomYmlItemStack("data", "kits.virtualKitRoom.categories.cat3.item"));
        inventory.setItem(51, DataUtils.getCustomYmlItemStack("data", "kits.virtualKitRoom.categories.cat4.item"));
        inventory.setItem(52, DataUtils.getCustomYmlItemStack("data", "kits.virtualKitRoom.categories.cat5.item"));
        inventory.setItem(53, DataUtils.getCustomYmlItemStack("data", "kits.virtualKitRoom.categories.cat6.item"));

        ItemStack currentCategory = DataUtils.getCustomYmlItemStack("data", "kits.virtualKitRoom.categories.cat" + categoryNumber + ".item");
        ItemMeta currentCategoryMeta = currentCategory.getItemMeta();
        currentCategoryMeta.displayName(Component.text(ChatColor.GREEN + ChatColor.stripColor(currentCategoryMeta.getDisplayName())));
        currentCategoryMeta.lore(Collections.singletonList(Component.text(Utils.chat("&7&oCurrently selected!"))));
        currentCategoryMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1, true);
        currentCategoryMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_PLACED_ON, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_POTION_EFFECTS, ItemFlag.HIDE_DESTROYS, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DYE);
        currentCategory.setItemMeta(currentCategoryMeta);
        inventory.setItem(getSlotFromCategory(categoryNumber), currentCategory);

        VirtualKitRoom.cachedKitRoomItems.put(categoryNumber, inventory);

        DataUtils.setCustomYml("data", "kits.virtualKitRoom.lastSaved", Utils.getUnix());

        if(showText) {
            player.sendTitle(Utils.chat("&fSaved &bVirtual Kit Room"), Utils.chat("&7&oApplied to all virtual kit rooms!"), 0, 80, 20);
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1F, 2F);
            player.closeInventory();
        }
    }

    public static void virtualKitRoomLoader() {
        setIfNull("kits.virtualKitRoom.lastCached", Utils.getUnix());
        setIfNull("kits.virtualKitRoom.lastSaved", Utils.getUnix());
        setIfNull("kits.virtualKitRoom.categories.cat1.item", ItemUtils.createBasicItemStack(Material.GRASS_BLOCK, 1, "&bCategory #1", CommandManager.emptyList));
        setIfNull("kits.virtualKitRoom.categories.cat1.categoryName", "Category #1");
        setIfNull("kits.virtualKitRoom.categories.cat2.item", ItemUtils.createBasicItemStack(Material.STONE, 2, "&bCategory #2", CommandManager.emptyList));
        setIfNull("kits.virtualKitRoom.categories.cat2.categoryName", "Category #2");
        setIfNull("kits.virtualKitRoom.categories.cat3.item", ItemUtils.createBasicItemStack(Material.OAK_SAPLING, 3, "&bCategory #3", CommandManager.emptyList));
        setIfNull("kits.virtualKitRoom.categories.cat3.categoryName", "Category #3");
        setIfNull("kits.virtualKitRoom.categories.cat4.item", ItemUtils.createBasicItemStack(Material.OAK_LOG, 4, "&bCategory #4", CommandManager.emptyList));
        setIfNull("kits.virtualKitRoom.categories.cat4.categoryName", "Category #4");
        setIfNull("kits.virtualKitRoom.categories.cat5.item", ItemUtils.createBasicItemStack(Material.OAK_LEAVES, 5, "&bCategory #5", CommandManager.emptyList));
        setIfNull("kits.virtualKitRoom.categories.cat5.categoryName", "Category #5");
        setIfNull("kits.virtualKitRoom.categories.cat6.item", ItemUtils.createBasicItemStack(Material.IRON_INGOT, 6, "&bCategory #6", CommandManager.emptyList));
        setIfNull("kits.virtualKitRoom.categories.cat6.categoryName", "Category #6");
    }

    private static void setIfNull(String path, Object toWhat) { if(DataUtils.getCustomYmlObject("data", path) != null) return; DataUtils.setCustomYml("data", path, toWhat); }
}
