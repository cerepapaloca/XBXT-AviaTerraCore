package net.atcore.achievement;

import lombok.Getter;
import net.atcore.AviaTerraCore;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.UUID;

@Getter
public class PlayerInventoryChangeEvent extends InventoryEvent {
    private static final HandlerList handlers = new HandlerList();
    private final Player player;

    public PlayerInventoryChangeEvent(Player player) {
        super(player.getOpenInventory());
        this.player = player;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    private static final HashMap<UUID, Integer> playerInventoryHash = new HashMap<>();

    public static void start() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    int hash = hashInventory(player.getInventory());
                    int currentHash = playerInventoryHash.computeIfAbsent(player.getUniqueId(),k -> hash);
                    if (currentHash != hash) {
                        playerInventoryHash.put(player.getUniqueId(), hash);
                        Bukkit.getPluginManager().callEvent(new PlayerInventoryChangeEvent(player));
                    }
                }
            }
        }.runTaskTimer(AviaTerraCore.getInstance(), 0, 1); // Cada Tick
    }

    private static int hashInventory(Inventory inventory) {
        int hash = 7;
        for (ItemStack item : inventory.getContents()) {
            hash = 31 * hash + (item != null ? item.hashCode() : 0);
        }
        return hash;
    }
}
