package net.atcore.achievement;

import lombok.Getter;
import net.atcore.AviaTerraCore;
import net.atcore.utils.AviaTerraScheduler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.WeakHashMap;

@Getter
public class InventoryChangeEvent extends InventoryEvent {
    private static final HandlerList handlers = new HandlerList();
    private final Player player;

    public InventoryChangeEvent(Player player) {
        super(player.getOpenInventory());
        this.player = player;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    @SuppressWarnings("unused")
    public static @NotNull HandlerList getHandlerList() {
        return handlers;
    }

    private static final WeakHashMap<UUID, Integer> playerInventoryHash = new WeakHashMap<>();

    public static void start() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    int hash = hashInventory(player.getInventory());
                    int currentHash = playerInventoryHash.computeIfAbsent(player.getUniqueId(),k -> hash);
                    if (currentHash != hash) {
                        playerInventoryHash.put(player.getUniqueId(), hash);
                        AviaTerraScheduler.runTask(() -> Bukkit.getPluginManager().callEvent(new InventoryChangeEvent(player)));
                    }
                }
            }
        }.runTaskTimerAsynchronously(AviaTerraCore.getInstance(), 0, 1); // Cada Tick
    }

    private static int hashInventory(Inventory inventory) {
        int hash = 7;
        for (ItemStack item : inventory.getContents()) {
            hash = 31 * hash + (item != null ? item.hashCode() : 0);
        }
        return hash;
    }
}
