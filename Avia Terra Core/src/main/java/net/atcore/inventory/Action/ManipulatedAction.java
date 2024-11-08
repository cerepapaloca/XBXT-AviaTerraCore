package net.atcore.inventory.Action;

import net.atcore.AviaTerraCore;
import net.atcore.AviaTerraPlayer;
import net.atcore.inventory.BaseActions;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class ManipulatedAction extends BaseActions {

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
                    Inventory inv = Bukkit.createInventory(null, 54, p.getName());
                    for (int i = 0; i < 54; i++){
                        ItemStack item = player.getPlayer().getInventory().getItem(i);
                        if (item != null){
                            if (i == 36 || i == 37){
                                inv.setItem(i + 2 + 9,item);
                            }else if (i == 38 || i == 39){
                                inv.setItem(i + 3 + 9,item);
                            }else if (i == 40){
                                inv.setItem(i + 9,item);
                            }else if (i > 8){
                                inv.setItem(i - 9,item);
                            }else{
                                inv.setItem(i + 27,item);
                            }
                        }
                    }
                    p.getOpenInventory().getTopInventory().setContents(inv.getContents());
                }
            }
        }.runTaskLater(AviaTerraCore.getInstance(), 1);
    }
}
