package net.atcore.inventory;

import lombok.Getter;
import lombok.Setter;
import net.atcore.aviaterraplayer.AviaTerraPlayer;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

@Getter
@Setter
public abstract class BaseActions {

    private InventorySection section;

    public abstract boolean clickInventory(InventoryClickEvent event, AviaTerraPlayer player);

    public abstract void closeInventory(AviaTerraPlayer player);

    public abstract void dragInventory(InventoryDragEvent event, AviaTerraPlayer player);

}
