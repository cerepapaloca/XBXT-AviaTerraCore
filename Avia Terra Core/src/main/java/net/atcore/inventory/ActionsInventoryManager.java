package net.atcore.inventory;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

import net.atcore.aviaterraplayer.AviaTerraPlayer;
import org.jetbrains.annotations.NotNull;

public class ActionsInventoryManager {

    public static boolean clickEvent(@NotNull InventoryClickEvent event){
        AviaTerraPlayer atp = AviaTerraPlayer.getPlayer((Player) event.getWhoClicked());
        if (atp.getInventorySection() == null) return false;
        InventorySection section = atp.getInventorySection();
        boolean b = section.getBaseActions().clickInventory(event, atp);
        return section.isProtectedInventory() || b;
    }

    public static void closeEvent(@NotNull InventoryCloseEvent event){
        AviaTerraPlayer atp = AviaTerraPlayer.getPlayer((Player) event.getPlayer());
        if (atp.getInventorySection() == null) return;
        atp.getInventorySection().getBaseActions().closeInventory(atp);
    }

    public static boolean dragEvent(@NotNull InventoryDragEvent event){
        AviaTerraPlayer atp = AviaTerraPlayer.getPlayer((Player) event.getWhoClicked());
        if (atp.getInventorySection() == null) return false;
        InventorySection section = atp.getInventorySection();
        section.getBaseActions().dragInventory(event, atp);
        return section.isProtectedInventory();
    }
}
