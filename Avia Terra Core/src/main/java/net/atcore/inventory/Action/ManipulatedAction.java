package net.atcore.inventory.Action;

import net.atcore.AviaTerraCore;
import net.atcore.AviaTerraPlayer;
import net.atcore.inventory.BaseActions;
import net.atcore.inventory.InventorySection;
import net.atcore.inventory.inventors.ManipulatorInventory;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class ManipulatedAction extends BaseActions {

    @Override
    public boolean clickInventory(InventoryClickEvent event, AviaTerraPlayer player) {
        updateInventory(player);
        return false;
    }

    @Override
    public void closeInventory(AviaTerraPlayer player) {

    }

    @Override
    public void dragInventory(InventoryDragEvent event, AviaTerraPlayer player) {
        updateInventory(player);
    }

    @Override
    public void pickupInventory(EntityPickupItemEvent event, AviaTerraPlayer player) {
        updateInventory(player);
    }

    public static void updateInventory(AviaTerraPlayer player) {
        /*
        new BukkitRunnable() {
            public void run() {
                if (InventorySection.MANIPULATOR.getBaseInventory() instanceof ManipulatorInventory manipulatorInventory) {
                    manipulatorInventory.updateInventory(player.getPlayer());
                }
            }
        }.runTaskLater(AviaTerraCore.getInstance(), 1);*/
    }
}
