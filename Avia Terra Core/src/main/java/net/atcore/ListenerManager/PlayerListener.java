package net.atcore.ListenerManager;

import net.atcore.Moderation.Freeze;
import net.atcore.Security.Login.LoginManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerListener implements Listener {

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (!LoginManager.getListPlayerLoginIn().contains(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
            return;
        }
        event.setCancelled(Freeze.isFreeze(event.getPlayer()));
        event.setCancelled(Freeze.isFreeze(event.getPlayer()));
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (!LoginManager.getListPlayerLoginIn().contains(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
            return;
        }
        event.setCancelled(Freeze.isFreeze(event.getPlayer()));
        event.setCancelled(Freeze.isFreeze(event.getPlayer()));
    }
}
