package net.atcore.inventory.Action;

import net.atcore.AviaTerraCore;
import net.atcore.AviaTerraPlayer;
import net.atcore.inventory.BaseActions;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class ManipulatedAction implements BaseActions {

    @Override
    public void clickInventory(InventoryClickEvent event, AviaTerraPlayer player) {
        updateInventory(player);
    }

    @Override
    public void closeInventory(InventoryCloseEvent event, AviaTerraPlayer player) {

    }

    @Override
    public void dragInventory(InventoryDragEvent event, AviaTerraPlayer player) {
        updateInventory(player);
    }

    private void updateInventory(AviaTerraPlayer player) {
        new BukkitRunnable() {
            public void run() {
                for (Player p : player.getManipulatorInventoryPlayer()){
                    p.getOpenInventory().getTopInventory().setContents(player.getPlayer().getInventory().getContents());
                }
            }
        }.runTaskLater(AviaTerraCore.getInstance(), 1);
    }
}
