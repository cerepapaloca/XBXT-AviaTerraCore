package net.atcore.inventory.Action;

import lombok.Getter;
import net.atcore.AviaTerraCore;
import net.atcore.AviaTerraPlayer;
import net.atcore.inventory.BaseActions;
import net.atcore.inventory.InventorySection;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

@Getter
public class ManipulatorAction extends BaseActions {

    @Override
    public void clickInventory(InventoryClickEvent event, AviaTerraPlayer player) {
        ItemStack item = event.getCurrentItem();
        if (item != null) {
            if (item.getType() == Material.GRAY_STAINED_GLASS_PANE) {
                event.setCancelled(true);
            }
        }
        if (event.getSlot() >= 36 && event.getSlot() <= 45) {
            event.setCancelled(true);
        }
        updateInventory(player);
    }

    @Override
    public void closeInventory(InventoryCloseEvent event, AviaTerraPlayer player) {
        player.setInventorySection(null);
        Player victim = player.getManipulatedInventoryPlayer();
        player.setManipulatedInventoryPlayer(null);
        AviaTerraPlayer atp = AviaTerraPlayer.getPlayer(victim);
        atp.setInventorySection(null);
        atp.getManipulatorInventoryPlayer().remove(player.getPlayer());
    }

    @Override
    public void dragInventory(InventoryDragEvent event, AviaTerraPlayer player) {
        updateInventory(player);
    }

    public static void updateInventory(@NotNull AviaTerraPlayer player) {
        //player.getPlayer().openInventory(InventorySection.MANIPULATOR.getBaseInventors().createInventory(player));
        Player victim = player.getManipulatedInventoryPlayer();
        new BukkitRunnable(){
            public void run() {
                Inventory inv = Bukkit.createInventory(null, 54, player.getPlayer().getName());
                for (int i = 0; i < 54; i++){
                    ItemStack item = player.getPlayer().getOpenInventory().getTopInventory().getItem(i);
                    if (item != null) {
                        if (i == 47 || i == 48) {
                            inv.setItem(i - 2 - 9, item);
                        } else if (i == 50 || i == 51) {
                            inv.setItem(i - 3 - 9, item);
                        } else if (i == 49) {
                            inv.setItem(i - 9, item);
                        } else if (i >= 27 && i <= 35) {
                            inv.setItem(i - 27, item);
                        } else if (i <= 26) {
                            inv.setItem(i + 9, item);
                        }
                    }
                }
                victim.getInventory().setContents(Arrays.stream(inv.getContents()).toList().subList(0, victim.getInventory().getSize()).toArray(ItemStack[]::new));
            }
        }.runTaskLater(AviaTerraCore.getInstance(), 1);
    }
}
