package net.cybercake.ghost.ffa.repeatingtasks.menus;

import net.cybercake.ghost.ffa.commands.maincommand.CommandManager;
import net.cybercake.ghost.ffa.menus.kits.KitViewer;
import net.cybercake.ghost.ffa.menus.kits.KitsMain;
import net.cybercake.ghost.ffa.utils.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Collections;

import static net.cybercake.ghost.ffa.menus.kits.KitsMain.setKitSlot;

public class RefreshMenu implements Runnable{

    @Override
    public void run() {
        for(Player player : Bukkit.getOnlinePlayers()) {
            if(ItemUtils.currentMenu.get(player.getName()) == null) return;

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
                    if(player.getOpenInventory().getItem(49).getType().equals(Material.EMERALD_BLOCK)) {
                        return;
                    }
                    player.getOpenInventory().setItem(49, ItemUtils.createBasicItemStack(Material.EMERALD_BLOCK, 1, "&bSave Current Kit Configuration", CommandManager.emptyList));
                } else {
                    if(player.getOpenInventory().getItem(49).getType().equals(Material.COAL_BLOCK)) {
                        return;
                    }
                    player.getOpenInventory().setItem(49, ItemUtils.createBasicItemStack(Material.COAL_BLOCK, 1, "&cSave Current Kit Configuration", Collections.singletonList("paginate:Cannot save an empty kit!:&8")));
                }
            }
        }
    }
}
