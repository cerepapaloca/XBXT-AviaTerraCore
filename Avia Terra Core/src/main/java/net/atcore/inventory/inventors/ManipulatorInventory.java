package net.atcore.inventory.inventors;

import net.atcore.AviaTerraCore;
import net.atcore.aviaterraplayer.AviaTerraPlayer;
import net.atcore.aviaterraplayer.ModerationPlayer;
import net.atcore.inventory.BaseInventory;
import net.atcore.inventory.InventorySection;
import net.atcore.inventory.InventoryUtils;
import net.atcore.messages.Message;
import net.atcore.utils.GlobalUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class ManipulatorInventory extends BaseInventory {

    public ManipulatorInventory() {
        super(54, Message.INVENTORY_MANIPULATOR_TITLE.getMessage());
    }

    public static final HashMap<UUID, Inventory> INVENTORIES = new HashMap<>();
    public static final HashMap<UUID, BukkitTask> TASKS = new HashMap<>();

    @Override
    public Inventory createInventory(AviaTerraPlayer player) {
        ModerationPlayer moderationPlayer = player.getModerationPlayer();
        if (!TASKS.containsKey(moderationPlayer.getManipulatedInventoryPlayer())) {
            UUID uuid = moderationPlayer.getManipulatedInventoryPlayer();
            BukkitTask task = new BukkitRunnable() {
                private int previousHash = getInventoryHash(player); // Obtener el estado inicial del inventario
                @Override
                public void run() {
                    if (player.getPlayer().isOnline() && moderationPlayer.getManipulatedInventoryPlayer() != null && Bukkit.getPlayer(uuid) != null) {
                        int currentHash = getInventoryHash(player); // Se obtiene el hash para saber si su inventarió á cambiado
                        if (currentHash != previousHash) { // Revisa si lo hash son diferente en caso de que si le envía él inventarió
                            previousHash = currentHash; // Actualiza el Hash
                            if (InventorySection.MANIPULATOR.getBaseInventory() instanceof ManipulatorInventory manipulatorInventory) {
                                manipulatorInventory.updateInventoryManipulator(GlobalUtils.getPlayer(moderationPlayer.getManipulatedInventoryPlayer()));
                            }
                        }
                    } else {
                        TASKS.remove(uuid);
                        cancel();
                    }
                }
            }.runTaskTimer(AviaTerraCore.getInstance(), 0, 1); // Cada Tick
            TASKS.put(moderationPlayer.getManipulatedInventoryPlayer(), task);
        }

        return updateInventoryManipulator(GlobalUtils.getPlayer(moderationPlayer.getManipulatedInventoryPlayer()));
    }

    private int getInventoryHash(AviaTerraPlayer player) {
        ModerationPlayer moderationPlayer = player.getModerationPlayer();
        Inventory inventory = GlobalUtils.getPlayer(moderationPlayer.getManipulatedInventoryPlayer()).getInventory();
        int hash = 7;//se inicia por 7 por qué es un número primo facilitando una mayor aleatoriedad

        for (ItemStack item : inventory.getContents()) {
            hash = 31 * hash + (item != null ? item.hashCode() : 0);//el 31 es porque es otro número primo que también mejora la aleatoriedad
        }

        return hash;
    }

    /**
     * Actualiza el inventario de los manipuladores cambiando de posición algunos items
     * @param victim El jugador que se va leer el inventario
     * @return El inventario modificado para los manipuladores
     */

    public Inventory updateInventoryManipulator(Player victim) {
        Inventory inv = INVENTORIES.get(victim.getUniqueId());
        if (inv == null) {
            inv = createNewInventory(AviaTerraPlayer.getPlayer(victim));
            INVENTORIES.put(victim.getUniqueId(), inv);
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
