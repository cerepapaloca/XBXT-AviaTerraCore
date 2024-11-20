package net.atcore.inventory.Action;

import net.atcore.aviaterraplayer.AviaTerraPlayer;
import net.atcore.inventory.BaseActions;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

public class ManipulatedAction extends BaseActions {

    @Override
    public boolean clickInventory(InventoryClickEvent event, AviaTerraPlayer player) {
        return false;
    }

    @Override
    public void closeInventory(AviaTerraPlayer player) {

    }

    @Override
    public void dragInventory(InventoryDragEvent event, AviaTerraPlayer player) {

    }
}
