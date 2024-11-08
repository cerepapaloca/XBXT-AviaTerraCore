package net.atcore.inventory;

import lombok.Getter;
import lombok.Setter;
import net.atcore.AviaTerraPlayer;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

@Getter
@Setter
public abstract class BaseActions {

    private InventorySection section;

    public abstract boolean clickInventory(InventoryClickEvent event, AviaTerraPlayer player);

    public abstract void closeInventory(AviaTerraPlayer player);

    public abstract void dragInventory(InventoryDragEvent event, AviaTerraPlayer player);

    public abstract void pickupInventory(EntityPickupItemEvent event, AviaTerraPlayer player);

}
