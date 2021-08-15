package net.cybercake.ghost.ffa.utils;

import net.cybercake.ghost.ffa.commands.maincommand.CommandManager;
import net.cybercake.ghost.ffa.Main;
import net.cybercake.ghost.ffa.menus.kits.KitViewer;
import net.cybercake.ghost.ffa.menus.kits.KitsMain;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.util.ChatPaginator;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ItemUtils implements CommandExecutor, Listener, TabCompleter {

    public static HashMap<String, Menu> currentMenu = new HashMap<>();
    public static HashMap<String, Integer> invClickCooldown = new HashMap<>();

    public static void setInvSlot(Inventory inventory, int slot, Material material, int amount, String name, List<String> lore) {
        try {
            ItemStack item = new ItemStack(material, amount);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
            List<String> Lore = new ArrayList<String>();
            for(String loreActual : lore) {
                if(!loreActual.startsWith("paginate:")) {
                    Lore.add(ChatColor.translateAlternateColorCodes('&', loreActual));
                }else{
                    String color = loreActual.substring(loreActual.length()-2, loreActual.length());
                    loreActual = loreActual.substring(9, loreActual.length()-3);
                    for(String str : ChatPaginator.wordWrap(Utils.chat(color + loreActual), 40)) {
                        Lore.add(Utils.chat(str));
                    }
                }
            }
            meta.setLore(Lore);
            item.setItemMeta(meta);
            inventory.setItem(slot, item);
        } catch (Exception e) {
            Main.logError("An error occurred whilst setting a Menu item!");
            Main.logError(" ");
            Main.logError("Stack trace below:");
            Utils.printBetterStackTrace(e);
        }
    }

    public static ItemStack createBasicItemStack(Material material, int amount, String name, List<String> lore) {
        try {
            ItemStack item = new ItemStack(material, amount);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
            List<String> Lore = new ArrayList<String>();
            for(String loreActual : lore) {
                if(!loreActual.startsWith("paginate:")) {
                    Lore.add(ChatColor.translateAlternateColorCodes('&', loreActual));
                }else{
                    String color = loreActual.substring(loreActual.length()-2, loreActual.length());
                    loreActual = loreActual.substring(9, loreActual.length()-3);
                    for(String str : ChatPaginator.wordWrap(Utils.chat(color + loreActual), 40)) {
                        Lore.add(Utils.chat(str));
                    }
                }
            }
            meta.setLore(Lore);
            item.setItemMeta(meta);
            return item;
        } catch (Exception e) {
            Main.logError("An error occurred whilst creating an ItemStack!");
            Main.logError(" ");
            Main.logError("Stack trace below:");
            Utils.printBetterStackTrace(e);
        }
        return null;
    }

    public static ItemStack createBasicShinyItemStack(Material material, int amount, String name, List<String> lore) {
        try {
            ItemStack item = new ItemStack(material, amount);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
            List<String> Lore = new ArrayList<String>();
            for(String loreActual : lore) {
                if(!loreActual.startsWith("paginate:")) {
                    Lore.add(ChatColor.translateAlternateColorCodes('&', loreActual));
                }else{
                    String color = loreActual.substring(loreActual.length()-2, loreActual.length());
                    loreActual = loreActual.substring(9, loreActual.length()-3);
                    for(String str : ChatPaginator.wordWrap(Utils.chat(color + loreActual), 40)) {
                        Lore.add(Utils.chat(str));
                    }
                }
            }
            meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1, false);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            meta.setLore(Lore);
            item.setItemMeta(meta);
            return item;
        } catch (Exception e) {
            Main.logError("An error occurred whilst creating an ItemStack!");
            Main.logError(" ");
            Main.logError("Stack trace below:");
            Utils.printBetterStackTrace(e);
        }
        return null;
    }

    public static void setShinyInvSlot(Inventory inventory, int slot, Material material, int amount, String name, List<String> lore) {
        try {
            ItemStack item = new ItemStack(material, amount);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
            meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1, false);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            List<String> Lore = new ArrayList<String>();
            for(String loreActual : lore) {
                Lore.add(ChatColor.translateAlternateColorCodes('&', loreActual));
            }
            meta.setLore(Lore);
            item.setItemMeta(meta);
            inventory.setItem(slot, item);
        } catch (Exception e) {
            Main.logError("An error occurred whilst setting a Menu item (shiny)!");
            Main.logError(" ");
            Main.logError("Stack trace below:");
            Utils.printBetterStackTrace(e);
        }
    }

    public static void setInvSlotHead(Inventory inventory, int slot, String owner, int amount, String name, List<String> lore) {
        try {
            ItemStack item = new ItemStack(Material.PLAYER_HEAD, amount);
            SkullMeta meta = (SkullMeta) item.getItemMeta();
            Player ownerFinish = Bukkit.getPlayerExact(owner);
            OfflinePlayer ownerOffline = Bukkit.getOfflinePlayer(ownerFinish.getUniqueId());
            meta.setOwningPlayer(ownerOffline);
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
            List<String> Lore = new ArrayList<String>();
            for (String loreActual : lore) {
                Lore.add(ChatColor.translateAlternateColorCodes('&', loreActual));
            }
            meta.setLore(Lore);
            item.setItemMeta(meta);
            inventory.setItem(slot, item);
        } catch (Exception e) {
            Main.logError("An error occurred whilst setting a Menu item (skull)!");
            Main.logError(" ");
            Main.logError("Stack trace below:");
            Utils.printBetterStackTrace(e);
        }
    }

    public static void fillGUI(Inventory inventory, int rows, Material material, int amount, String name, List<String> lore) {
        ItemStack item = new ItemStack(material, amount);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text(Utils.chat(name)));
        meta.setLore(lore);
        item.setItemMeta(meta);
        for(int i=0; i<rows*9; i++) {
            inventory.setItem(i, item);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(sender instanceof Player) {
            Player p = (Player) sender;
            if(args.length == 0) {
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cInvalid usage! &7/guislots <number of " +
                        "rows>"));
            }else if(args.length > 1) {
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cInvalid usage! &7/guislots <number of " +
                        "rows>"));
            }else if(args.length == 1) {
                try {
                    if(Integer.parseInt(args[0]) > 6) {
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou can only set the gui rows to &b1" +
                                " - 6"));
                    }else if(Integer.parseInt(args[0]) < 1) {
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou can only set the gui rows to &b1" +
                                " - 6"));
                    }else {
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aOpening a GUI with " + args[0] +
                                " &arows!"));
                        Inventory guislots = Bukkit.createInventory(p, 9 * Integer.parseInt(args[0]), "GUI Slots (" + Integer.parseInt(args[0]) + " Rows)");

                        for(int i = 0; i < Integer.parseInt(args[0])*9; ++i) {
                            ItemStack item = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
                            ItemMeta meta = item.getItemMeta();
                            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&8Slot >> " + i));
                            item.setItemMeta(meta);
                            guislots.setItem(i, item);
                        }

                        p.openInventory(guislots);
                    };
                } catch (Exception e) {
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cAn error occurred whilst trying to load the GUI slots menu."));
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cThe error could be caused by something you typed into the arguments!"));
                }
            }
        }else{
            sender.sendMessage("Only players can execute this command!");
        }



        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if(args.length <= 1) {
            return CommandManager.createReturnList(Arrays.asList("1", "2", "3", "4", "5", "6"), args[0]);
        }

        return CommandManager.emptyList;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if(e.getView().getTitle().contains("GUI Slots")) { e.setCancelled(true); }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        Player player = (Player) e.getPlayer();

        if(currentMenu.get(player.getName()) != null) {
            boolean openKitsAfter = false;
            if(currentMenu.get(player.getName()).equals(Menu.KIT_VIEWER)) {
                KitViewer.saveKitConfiguration(player, KitViewer.currentKit.get(player.getName()), false);
                openKitsAfter = true;
            }
            currentMenu.remove(player.getName());
            if(openKitsAfter) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(), new Runnable() {
                    @Override
                    public void run() {
                        KitsMain.openMenu(player);
                    }
                }, 1L);
            }
        }
        if(invClickCooldown.get(player.getName()) != null) {
            invClickCooldown.remove(player.getName());
        }
        if(KitViewer.currentKit.get(player.getName()) != null) {
            KitViewer.currentKit.remove(player.getName());
        }
    }

    public enum Menu {
        KITS_MAIN, KIT_VIEWER
    }

    public static boolean hasEnoughRoom(Player player, ItemStack item) {
        int amountToHold = 0;
        int currentSize = item.getAmount();
        for(int counter=0; counter<36; counter++) {
            if(player.getInventory().getItem(counter) == null) return true;

            if(player.getInventory().getItem(counter).equals(item)) {
                amountToHold = item.getMaxStackSize()-player.getInventory().getItem(counter).getAmount();
                if(amountToHold > 0) return true;

            }
        }

        return false;
    }

    public static void giveItem(Player player, ItemStack item) {
        if(hasEnoughRoom(player, item)) {
            player.getInventory().addItem(item);
        }else if(!hasEnoughRoom(player, item)) {
            Location playerLocation = player.getLocation();
            Vector playerVector = playerLocation.getDirection().multiply(0.5);
            playerLocation.setDirection(playerVector);
            Item newItem = player.getWorld().dropItem(playerLocation, item);
            newItem.setVelocity(playerVector);
        }
    }




}