package net.atcore.inventory;

import net.atcore.AviaTerraPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

import java.util.HashMap;

public class ActionsInventoryManager {

    public static final HashMap<InventorySection, BaseActions> inventories = new HashMap<>();

    public static boolean clickEvent(InventoryClickEvent event){
        AviaTerraPlayer atp = AviaTerraPlayer.getPlayer((Player) event.getWhoClicked());
        if (atp.getInventorySection() == null) return false;
        InventorySection section = atp.getInventorySection();
        section.getBaseActions().clickInventory(event, atp);
        return section.isProtectedInventory();
    }

    public static void closeEvent(InventoryCloseEvent event){
        AviaTerraPlayer atp = AviaTerraPlayer.getPlayer((Player) event.getPlayer());
        if (atp.getInventorySection() == null) return;
        atp.getInventorySection().getBaseActions().closeInventory(event, atp);
    }

    public static boolean dragEvent(InventoryDragEvent event){
        AviaTerraPlayer atp = AviaTerraPlayer.getPlayer((Player) event.getWhoClicked());
        if (atp.getInventorySection() == null) return false;
        InventorySection section = atp.getInventorySection();
        section.getBaseActions().dragInventory(event, atp);
        return section.isProtectedInventory();
    }
}
