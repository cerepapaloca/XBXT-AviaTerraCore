package net.atcore.inventory;

import net.atcore.AviaTerraPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityPickupItemEvent;
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
        boolean b = section.getBaseActions().clickInventory(event, atp);
        return section.isProtectedInventory() || b;
    }

    public static void closeEvent(InventoryCloseEvent event){
        AviaTerraPlayer atp = AviaTerraPlayer.getPlayer((Player) event.getPlayer());
        if (atp.getInventorySection() == null) return;
        atp.getInventorySection().getBaseActions().closeInventory(atp);
    }

    public static boolean dragEvent(InventoryDragEvent event){
        AviaTerraPlayer atp = AviaTerraPlayer.getPlayer((Player) event.getWhoClicked());
        if (atp.getInventorySection() == null) return false;
        InventorySection section = atp.getInventorySection();
        section.getBaseActions().dragInventory(event, atp);
        return section.isProtectedInventory();
    }

    public static boolean pickupItem(EntityPickupItemEvent event){
        if (event.getEntity() instanceof Player player){
            AviaTerraPlayer atp = AviaTerraPlayer.getPlayer(player);
            if (atp.getInventorySection() == null) return false;
            InventorySection section = atp.getInventorySection();
            section.getBaseActions().pickupInventory(event, atp);
            return section.isProtectedInventory();
        }
        return false;
    }
}
