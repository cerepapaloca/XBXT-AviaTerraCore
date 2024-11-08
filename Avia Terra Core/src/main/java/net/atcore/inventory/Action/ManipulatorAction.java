package net.atcore.inventory.Action;

import lombok.Getter;
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
import org.jetbrains.annotations.NotNull;

import java.awt.image.LookupOp;
import java.util.Arrays;

@Getter
public class ManipulatorAction extends BaseActions {

    @Override
    public boolean clickInventory(InventoryClickEvent event, AviaTerraPlayer player) {
        sendInventoryToOtherPlayersAndUpdate(player);
        return (event.getSlot() >= 36 && event.getSlot() <= 44) || event.getSlot() == 49 || event.getSlot() == 51 || event.getSlot() == 53;
    }

    @Override
    public void closeInventory(AviaTerraPlayer player) {
        player.setInventorySection(null);
        Player victim = player.getManipulatedInventoryPlayer();
        player.setManipulatedInventoryPlayer(null);
        AviaTerraPlayer atp = AviaTerraPlayer.getPlayer(victim);
        atp.getManipulatorInventoryPlayer().remove(player.getPlayer());
        if (atp.getManipulatorInventoryPlayer().isEmpty() || !atp.getPlayer().isOnline()){
            atp.setInventorySection(null);
        }
    }

    @Override
    public void dragInventory(InventoryDragEvent event, AviaTerraPlayer player) {
        sendInventoryToOtherPlayersAndUpdate(player);

    }

    @Override
    public void pickupInventory(EntityPickupItemEvent event, AviaTerraPlayer player) {
        sendInventoryToOtherPlayersAndUpdate(player);
    }

    private void sendInventoryToOtherPlayersAndUpdate(AviaTerraPlayer p) {
        Player victim = p.getManipulatedInventoryPlayer();
        new BukkitRunnable() {
            final AviaTerraPlayer player = p;
            public void run() {
                Inventory inv = Bukkit.createInventory(null, 54, player.getPlayer().getName());
                for (int i = 0; i < 54; i++){
                    ItemStack item = player.getPlayer().getOpenInventory().getTopInventory().getItem(i);
                    if (item != null) {
                        if (i >= 45 && i <= 48){
                            inv.setItem(i - 9, item);
                        } else if (i == 50) {
                            inv.setItem(i - 10, item);
                        } else if (i >= 27 && i <= 35) {
                            inv.setItem(i - 27, item);
                        } else if (i <= 26) {
                            inv.setItem(i + 9, item);
                        }
                    }
                }
                Bukkit.getLogger().warning(player.getPlayer().getName() + " ->> " + victim.getName());
                victim.setItemOnCursor(player.getPlayer().getOpenInventory().getTopInventory().getItem(52));
                victim.getInventory().setContents(Arrays.stream(inv.getContents()).toList().subList(0, victim.getInventory().getSize()).toArray(ItemStack[]::new));

                AviaTerraPlayer.getPlayer(player.getManipulatedInventoryPlayer()).getManipulatorInventoryPlayer().forEach(p -> {
                    if (!p.getName().equals(player.getPlayer().getName())){
                        Bukkit.getLogger().warning(player.getPlayer().getName() + " -> " + p.getName());
                        p.getOpenInventory().getTopInventory().setContents(InventorySection.MANIPULATOR.getBaseInventory().
                                createInventory(AviaTerraPlayer.getPlayer(p)).getContents());
                    }
                });
            }
        }.runTaskLater(AviaTerraCore.getInstance(), 1L);
    }
}
