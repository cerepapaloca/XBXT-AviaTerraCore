package net.atcore.inventory;

import net.atcore.AviaTerraPlayer;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public interface ClickInventory {
    void clickInventory(InventoryClickEvent event, AviaTerraPlayer player);
    void closeInventory(InventoryCloseEvent event, AviaTerraPlayer player);
}
