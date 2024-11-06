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
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

@Getter
public class ManipulatorAction implements BaseActions {

    @Override
    public void clickInventory(InventoryClickEvent event, AviaTerraPlayer player) {
        ItemStack item = event.getCurrentItem();
        if (item != null) {
            if (item.getType() == Material.GRAY_STAINED_GLASS_PANE) {
                event.setCancelled(true);
            }
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

    private void updateInventory(@NotNull AviaTerraPlayer player) {
        //player.getPlayer().openInventory(InventorySection.MANIPULATOR.getBaseInventors().createInventory(player));
        Player victim = player.getManipulatedInventoryPlayer();
        new BukkitRunnable(){
            public void run() {
                victim.getInventory().setContents(Arrays.stream(player.getPlayer().getOpenInventory().getTopInventory().getContents())
                        .toList().subList(0, victim.getInventory().getContents().length).toArray(ItemStack[]::new));
            }
        }.runTaskLater(AviaTerraCore.getInstance(), 1);
    }
}
