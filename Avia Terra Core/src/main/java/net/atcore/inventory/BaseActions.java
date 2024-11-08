package net.atcore.inventory;

import lombok.Getter;
import lombok.Setter;
import net.atcore.AviaTerraPlayer;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

@Getter
@Setter
public abstract class BaseActions {

    private InventorySection section;

    public abstract void clickInventory(InventoryClickEvent event, AviaTerraPlayer player);

    public abstract void closeInventory(InventoryCloseEvent event, AviaTerraPlayer player);

    public abstract void dragInventory(InventoryDragEvent event, AviaTerraPlayer player);

}
