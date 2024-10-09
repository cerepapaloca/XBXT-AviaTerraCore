package net.atcore.Moderation;

import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.ArrayList;
import java.util.UUID;
@Getter
public class FreezeListener implements Listener {

    private final ArrayList<UUID> playerFreeze = new ArrayList<>();

    @EventHandler(priority = EventPriority.HIGH)
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if(playerFreeze.contains(player.getUniqueId())){
            player.sendTitle(ChatColor.translateAlternateColorCodes('&',"&c¡¡ESTAS CONGELADO!!"), "", 5, 80, 40);
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if(playerFreeze.contains(player.getUniqueId())){
            player.sendTitle(ChatColor.translateAlternateColorCodes('&',"&c¡¡ESTAS CONGELADO!!"), "", 5, 80, 40);
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInteract(InventoryOpenEvent event) {
        Player player = (Player) event.getPlayer();
        if(playerFreeze.contains(player.getUniqueId())){
            player.sendTitle(ChatColor.translateAlternateColorCodes('&',"&c¡¡ESTAS CONGELADO!!"), "", 5, 80, 40);
            event.setCancelled(true);
        }
    }
}
