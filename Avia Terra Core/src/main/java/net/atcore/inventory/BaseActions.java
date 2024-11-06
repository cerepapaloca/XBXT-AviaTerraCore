package net.atcore.inventory;

import net.atcore.AviaTerraPlayer;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

public interface BaseActions {

    void clickInventory(InventoryClickEvent event, AviaTerraPlayer player);

    void closeInventory(InventoryCloseEvent event, AviaTerraPlayer player);

    void dragInventory(InventoryDragEvent event, AviaTerraPlayer player);

}
