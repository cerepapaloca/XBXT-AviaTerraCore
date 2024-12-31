package net.atcore.inventory.Action;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

import net.atcore.aviaterraplayer.AviaTerraPlayer;
import net.atcore.inventory.BaseActions;

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
