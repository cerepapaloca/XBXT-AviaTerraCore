package net.atcore.inventory.inventors;

import net.atcore.AviaTerraCore;
import net.atcore.AviaTerraPlayer;
import net.atcore.inventory.BaseInventory;
import net.atcore.inventory.InventorySection;
import net.atcore.inventory.InventoryUtils;
import net.atcore.utils.GlobalUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class ManipulatorInventory extends BaseInventory {

    public ManipulatorInventory() {
        super(54,"Inventario de moderación");
    }

    public static final HashMap<UUID, Inventory> inventories = new HashMap<>();
    public static final HashMap<UUID, BukkitTask> tasks = new HashMap<>();

    @Override
    public Inventory createInventory(AviaTerraPlayer player) {
        if (!tasks.containsKey(player.getManipulatedInventoryPlayer())) {
            UUID uuid = player.getManipulatedInventoryPlayer();
            BukkitTask task = new BukkitRunnable() {
                private int previousHash = getInventoryHash(player); // Obtener el estado inicial del inventario

                @Override
                public void run() {
                    if (player.getPlayer().isOnline() && player.getManipulatedInventoryPlayer() != null) {
                        int currentHash = getInventoryHash(player);
                        if (currentHash != previousHash) {
                            previousHash = currentHash;
                            if (InventorySection.MANIPULATOR.getBaseInventory() instanceof ManipulatorInventory manipulatorInventory) {
                                manipulatorInventory.updateInventory(GlobalUtils.getPlayer(player.getManipulatedInventoryPlayer()));
                            }
                        }
                    } else {
                        tasks.remove(uuid);
                        cancel();
                    }
                }
            }.runTaskTimer(AviaTerraCore.getInstance(), 0, 1);
            tasks.put(player.getManipulatedInventoryPlayer(), task);
        }

        return updateInventory(GlobalUtils.getPlayer(player.getManipulatedInventoryPlayer()));
    }

    private int getInventoryHash(AviaTerraPlayer player) {
        Inventory inventory = GlobalUtils.getPlayer(player.getManipulatedInventoryPlayer()).getInventory();
        int hash = 7;//se inicia por 7 por qué es un número primo facilitando una mayor aleatoriedad

        for (ItemStack item : inventory.getContents()) {
            hash = 31 * hash + (item != null ? item.hashCode() : 0);//el 31 es porque es otro número primo que también mejora la aleatoriedad
        }

        return hash;
    }

    public Inventory updateInventory(Player victim) {
        Inventory inv = inventories.get(victim.getUniqueId());
        if (inv == null) {
            inv = createNewInventory(AviaTerraPlayer.getPlayer(victim));
            inventories.put(victim.getUniqueId(), inv);
        }
        inv.clear();
        inv.setItem(52, victim.getItemOnCursor());
        for (int i = 0; i < 54; i++){
            ItemStack item = Objects.requireNonNull(GlobalUtils.getPlayer(victim.getName())).getInventory().getItem(i);
            if (item != null) {
                if (i >= 36 && i <= 39){
                    inv.setItem(i + 9,item);
                }else if (i == 40){
                    inv.setItem(i + 10,item);
                }else if (i > 8){
                    inv.setItem(i - 9,item);
                }else {
                    inv.setItem(i + 27,item);
                }
            }
        }
        ItemStack item = InventoryUtils.newItems(Material.GRAY_STAINED_GLASS_PANE, " ", 1, null);
        InventoryUtils.fillItems(inv, item, 36, 44);
        InventoryUtils.setItems(inv, item, 49, 51, 53);
        return inv;
    }
}
