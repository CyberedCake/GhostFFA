package net.cybercake.ghost.ffa.repeatingtasks.menus;

import net.cybercake.ghost.ffa.Main;
import net.cybercake.ghost.ffa.commands.maincommand.CommandManager;
import net.cybercake.ghost.ffa.commands.maincommand.subcommands.VirtualKitRoomAdmin;
import net.cybercake.ghost.ffa.menus.kits.KitViewer;
import net.cybercake.ghost.ffa.menus.kits.KitsMain;
import net.cybercake.ghost.ffa.menus.kits.VirtualKitRoom;
import net.cybercake.ghost.ffa.utils.DataUtils;
import net.cybercake.ghost.ffa.utils.ItemUtils;
import net.cybercake.ghost.ffa.utils.PlayerDataUtils;
import net.cybercake.ghost.ffa.utils.Utils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.SimpleDateFormat;
import java.util.*;

import static net.cybercake.ghost.ffa.menus.kits.KitsMain.setKitSlot;

public class RefreshMenu implements Runnable{

    @Override
    public void run() {
        for(Player player : Bukkit.getOnlinePlayers()) {
            if(ItemUtils.currentMenu.get(player.getName()) == null) continue;

            if(ItemUtils.currentMenu.get(player.getName()).equals(ItemUtils.Menu.KITS_MAIN)) {
                player.getOpenInventory().setItem(10, setKitSlot(player,10, 1, KitsMain.KitType.FREE));
                player.getOpenInventory().setItem(11, setKitSlot(player,11, 2, KitsMain.KitType.FREE));
                player.getOpenInventory().setItem(12, setKitSlot(player,12, 3, KitsMain.KitType.FREE));
                player.getOpenInventory().setItem(13, setKitSlot(player,13, 4, KitsMain.KitType.REQUIRES_VIP));
                player.getOpenInventory().setItem(14, setKitSlot(player,14, 5, KitsMain.KitType.REQUIRES_VIP));
                player.getOpenInventory().setItem(15, setKitSlot(player,15, 6, KitsMain.KitType.REQUIRES_PATRON));
                player.getOpenInventory().setItem(16, setKitSlot(player,16, 7, KitsMain.KitType.REQUIRES_PATRON));
            }else if(ItemUtils.currentMenu.get(player.getName()).equals(ItemUtils.Menu.KIT_VIEWER)) {
                if (KitViewer.itemsInMenu(player)) {
                    player.getOpenInventory().setItem(49, ItemUtils.createBasicItemStack(Material.EMERALD_BLOCK, 1, "&bSave Current Kit Configuration", CommandManager.emptyList));
                } else {
                    player.getOpenInventory().setItem(49, ItemUtils.createBasicItemStack(Material.COAL_BLOCK, 1, "&cSave Current Kit Configuration", Collections.singletonList("paginate:Cannot save an empty kit!:&8")));
                }

                if(Main.virtualKitRoomLoaded.equalsIgnoreCase("unloaded")) {
                    player.getOpenInventory().setItem(50, ItemUtils.createBasicItemStack(Material.BEDROCK, 1, "&eLoading, please give us a moment!", CommandManager.emptyList));
                }else if(Main.virtualKitRoomLoaded.equalsIgnoreCase("failed")) {
                    player.getOpenInventory().setItem(50, ItemUtils.createBasicItemStack(Material.BARRIER, 1, "&cFailed to load!", Arrays.asList("&7&oA technical fault has occurred!")));
                }else{
                    player.getOpenInventory().setItem(50, ItemUtils.createBasicItemStack(Material.RESPAWN_ANCHOR, 1, "&bVirtual Kit Room", CommandManager.emptyList));
                }
            }else if(ItemUtils.currentMenu.get(player.getName()).equals(ItemUtils.Menu.ADMIN_VIRTUAL_KIT_ROOM)) {
                try {
                    for(int i=1; i<7; i++) {
                        ItemStack item = DataUtils.getCustomYmlItemStack("data", "kits.virtualKitRoom.categories.cat" + i + ".item");
                        assert item != null;
                        ItemMeta meta = item.getItemMeta();
                        ArrayList<Component> lore = new ArrayList<>();
                        meta.setDisplayName(Utils.chat("&b" + ChatColor.stripColor(item.getItemMeta().getDisplayName())));
                        if(VirtualKitRoom.currentCategory.get(player.getName()).equals(i)) {
                            lore.add(Component.text(Utils.chat("&7&oCurrently selected!")));
                            meta.setDisplayName(Utils.chat("&a" + ChatColor.stripColor(item.getItemMeta().getDisplayName())));
                            meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1, false);
                        }
                        meta.lore(lore);
                        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_DYE, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DESTROYS, ItemFlag.HIDE_PLACED_ON, ItemFlag.HIDE_POTION_EFFECTS, ItemFlag.HIDE_UNBREAKABLE);
                        item.setItemMeta(meta);

                        player.getOpenInventory().setItem(VirtualKitRoomAdmin.getSlotFromCategory(i), item);
                    }
                } catch (Exception exception) {
                    player.closeInventory();
                    Utils.error(player, "whilst loading and keeping open the virtual kit room editor/viewer for {name}", exception);
                    ItemUtils.currentMenu.remove(player.getName());
                    VirtualKitRoomAdmin.playerEditing = null;
                }
            }else if(ItemUtils.currentMenu.get(player.getName()).equals(ItemUtils.Menu.KIT_PREVIEWER)) {
                int kitNumber = Integer.parseInt(player.getOpenInventory().getTitle().replace("Kit #", "").replace(": Previewing", ""));
                if(Long.parseLong(PlayerDataUtils.getPlayerData(player, "kits." + kitNumber + ".lastSetTime") + "") != 0) {
                    Date date = new Date((int)PlayerDataUtils.getPlayerData(player, "kits." + kitNumber + ".lastSetTime")*1000L);
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM-dd-yyyy HH:mm:ss");
                    simpleDateFormat.setTimeZone(TimeZone.getTimeZone(Main.getMainConfig().getString("timeZone")));
                    player.getOpenInventory().setItem(48, ItemUtils.createBasicItemStack(Material.REDSTONE_TORCH, 1, "&bInformation about Kit #" + kitNumber + ":", Arrays.asList(" ", "&7Last Modified:", "   &e" + Utils.getBetterTimeFromLongs(Utils.getUnix(), (int)PlayerDataUtils.getPlayerData(player, "kits." + kitNumber + ".lastSetTime"), false) + " &eago", "   &d(" + simpleDateFormat.format(date) + "&d)")));
                }
            }
        }
    }
}
