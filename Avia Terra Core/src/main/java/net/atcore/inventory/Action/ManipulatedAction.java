package net.atcore.inventory.Action;

import net.atcore.AviaTerraCore;
import net.atcore.AviaTerraPlayer;
import net.atcore.inventory.BaseActions;
import net.atcore.inventory.InventorySection;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;

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
        new BukkitRunnable() {
            public void run() {
                for (Player p : player.getManipulatorInventoryPlayer()){
                    Bukkit.getLogger().warning(p.getPlayer().getName() +  " <- " + player.getPlayer().getName());
                    Inventory inv = InventorySection.MANIPULATOR.getBaseInventory().createInventory(AviaTerraPlayer.getPlayer(p));
                    p.getOpenInventory().getTopInventory().setContents(inv.getContents());
                }
            }
        }.runTaskLater(AviaTerraCore.getInstance(), 1);
    }
}
