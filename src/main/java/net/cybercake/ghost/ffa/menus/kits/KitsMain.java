package net.cybercake.ghost.ffa.menus.kits;

import net.cybercake.ghost.ffa.commands.maincommand.CommandManager;
import net.cybercake.ghost.ffa.Main;
import net.cybercake.ghost.ffa.utils.ItemUtils;
import net.cybercake.ghost.ffa.utils.PlayerDataUtils;
import net.cybercake.ghost.ffa.utils.Utils;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class KitsMain implements Listener {

    public static HashMap<Integer, String> kitStatus = new HashMap<>();

    public static void openMenu(Player player) {
        Inventory inv = Bukkit.createInventory(player, 9*4, Component.text("Kits"));

        ItemUtils.fillGUI(inv, 4, Material.CYAN_STAINED_GLASS_PANE, 1, "&r", CommandManager.emptyList);

        inv.setItem(10, setKitSlot(player,10, 1, KitType.FREE));
        inv.setItem(11, setKitSlot(player,11, 2, KitType.FREE));
        inv.setItem(12, setKitSlot(player,12, 3, KitType.FREE));
        inv.setItem(13, setKitSlot(player,13, 4, KitType.REQUIRES_VIP));
        inv.setItem(14, setKitSlot(player,14, 5, KitType.REQUIRES_VIP));
        inv.setItem(15, setKitSlot(player,15, 6, KitType.REQUIRES_PATRON));
        inv.setItem(16, setKitSlot(player,16, 7, KitType.REQUIRES_PATRON));

        inv.setItem(31, ItemUtils.createBasicItemStack(Material.STRUCTURE_VOID, 1, "&bExit Menu", CommandManager.emptyList));

        player.openInventory(inv);
        ItemUtils.currentMenu.put(player.getName(), ItemUtils.Menu.KITS_MAIN);
    }

    @EventHandler
    public void onInvClick(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        int slot = e.getRawSlot();
        ItemStack item = e.getCurrentItem();
        ClickType clickType = e.getClick();

        if(ItemUtils.currentMenu.get(player.getName()) == null || (!ItemUtils.currentMenu.get(player.getName()).equals(ItemUtils.Menu.KITS_MAIN))) return;
        if(item == null) return;
        e.setCancelled(true);
        if(ItemUtils.invClickCooldown.get(player.getName()) != null) return;
        ItemUtils.invClickCooldown.put(e.getWhoClicked().getName(), Main.getMainConfig().getInt("invClickCooldown"));

        if(slot == 31) {
            player.closeInventory();
            player.playSound(player.getLocation(), Sound.BLOCK_BAMBOO_BREAK, 1F, 1F);
        }

        if(!Utils.isBetweenEquals(slot, 10, 16)) return;
        if(kitStatus.get(slot).contains("errored>")) {
            player.sendMessage(Utils.chat("&cAn error occurred whilst attempting to load this kit: &8" + kitStatus.get(slot).replace("errored>", "")));
            player.closeInventory();
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1F, 0.1F);
        }else if(kitStatus.get(slot).equals("requires_patron")) {
            player.sendMessage(Utils.chat("&cYou must have &a&lPATRON &crank to use this kit slot!"));
            player.sendMessage(store());
            player.closeInventory();
            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1F, 0.1F);
        }else if(kitStatus.get(slot).equals("requires_vip")) {
            player.sendMessage(Utils.chat("&cYou must have &6&lVIP &crank to use this kit slot!"));
            player.sendMessage(store());
            player.closeInventory();
            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1F, 0.1F);
        }else if(kitStatus.get(slot).equals("accessible")) {
            if(clickType.equals(ClickType.RIGHT) || clickType.equals(ClickType.SHIFT_RIGHT)) {
                KitViewer.openMenu(player, item.getAmount());
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1F, 2F);
            }else if(clickType.equals(ClickType.LEFT) || clickType.equals(ClickType.SHIFT_LEFT)) {
                KitPreviewer.openMenu(player, item.getAmount());
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1F, 2F);
            }
        }

    }

    public static void applyKit(Player player, int kitNumber) {
        try {
            player.getInventory().clear();
            for (String slotNum : PlayerDataUtils.getPlayerDataConfigFile(player).getConfigurationSection("kits." + kitNumber).getKeys(false)) {
                if(!slotNum.contains("slot")) continue;

                int slot = Integer.parseInt(slotNum.replace("slot", ""));
                if(slot == 36) {
                    player.getInventory().setHelmet((ItemStack) PlayerDataUtils.getPlayerData(player, "kits." + kitNumber + "." + slotNum));
                }else if(slot == 37) {
                    player.getInventory().setChestplate((ItemStack) PlayerDataUtils.getPlayerData(player, "kits." + kitNumber + "." + slotNum));
                }else if(slot == 38) {
                    player.getInventory().setLeggings((ItemStack) PlayerDataUtils.getPlayerData(player, "kits." + kitNumber + "." + slotNum));
                }else if(slot == 39) {
                    player.getInventory().setBoots((ItemStack) PlayerDataUtils.getPlayerData(player, "kits." + kitNumber + "." + slotNum));
                }else{
                    player.getInventory().setItem(slot, (ItemStack) PlayerDataUtils.getPlayerData(player, "kits." + kitNumber + "." + slotNum));
                }
            }
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 1F, 1F);
        } catch (Exception exception) {
            // Catch any potential unwanted exceptions that might cause issues when opening the kit
            player.closeInventory();
            Utils.error(player, "whilst trying to apply that kit for " + kitNumber + " for {name}", exception);
            return;
        }
    }

    public TextComponent store() {
        TextComponent clickHereStore = new TextComponent(Utils.chat("&b&lCLICK HERE"));
        clickHereStore.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://ghostffa.tebex.io/"));
        clickHereStore.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(Utils.chat("&fClick here to visit the &bstore&f!")).create()));
        TextComponent main = new TextComponent(Utils.chat(" &cto visit the GhostFFA store!"));
        main.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, ""));
        main.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("").create()));
        clickHereStore.addExtra(main);
        return clickHereStore;
    }

    public static ItemStack setKitSlot(Player player, int slot, int kit, KitType kitType) {
        ItemStack returnedItem;
        try {
            switch (kitType) {
                case REQUIRES_PATRON:
                    if(!player.hasPermission("ghostffa.kits.patron")) {
                        returnedItem = ItemUtils.createBasicItemStack(Material.GRAY_DYE, kit, "&3&lKit #" + kit, Arrays.asList(" ", "&fRequires &a&lPATRON &fto use!"));
                        kitStatus.put(slot, "requires_patron");
                        return returnedItem;
                    }
                case REQUIRES_VIP:
                    if(!player.hasPermission("ghostffa.kits.vip")) {
                        returnedItem = ItemUtils.createBasicItemStack(Material.GRAY_DYE, kit, "&3&lKit #" + kit, Arrays.asList(" ", "&fRequires &6&lVIP &fto use!"));
                        kitStatus.put(slot, "requires_vip");
                        return returnedItem;
                    }
                default:
                    List<String> mainLore = new LinkedList<>(Arrays.asList(" ", "&7To Edit:", "   &bRight Click", " ", "&7To Apply:", "   &b/kit " + kit + " &8or &b/k " + kit, " ", "&7To Preview:", "   &bLeft Click"));
                    if(Long.parseLong(PlayerDataUtils.getPlayerData(player, "kits." + kit + ".lastSetTime") + "") != 0) {
                        mainLore.add(" ");
                        mainLore.add("&7Last Modified:");
                        mainLore.add("   &b" + Utils.getBetterTimeFromLongs(Utils.getUnix(), (int)PlayerDataUtils.getPlayerData(player, "kits." + kit + ".lastSetTime"), false) + " &bago");
                    }
                    returnedItem = ItemUtils.createBasicItemStack(Material.CHEST, kit, "&3&lKit #" + kit, mainLore);
                    kitStatus.put(slot, "accessible");
                    return returnedItem;
            }
        } catch (Exception e) {
            returnedItem = ItemUtils.createBasicItemStack(Material.BEDROCK, kit, "&c&lKit #" + kit, Arrays.asList(" ", "paginate:An error occurred whilst attempting to load this kit! Report this to an administrator.:&7", " ", "paginate:&cError: &8" + e.toString() + ":&8"));
            kitStatus.put(slot, "errored>" + e);
        }
        return returnedItem;
    }

    public enum KitType {
        FREE, REQUIRES_VIP, REQUIRES_PATRON
    }

}
