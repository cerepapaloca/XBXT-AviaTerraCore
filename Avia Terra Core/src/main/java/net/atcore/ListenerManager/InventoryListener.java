package net.atcore.ListenerManager;

import net.atcore.Moderation.Ban.CheckAutoBan;
import net.atcore.Moderation.Freeze;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.jetbrains.annotations.NotNull;

public class InventoryListener implements Listener {

    @EventHandler
    public void onInventoryClick(@NotNull InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        CheckAutoBan.checkAntiIlegalItems(player);
        CheckAutoBan.checkDupe(player);
        event.setCancelled(Freeze.isFreeze(player));
    }

    @EventHandler
    public void onInventoryOpen(@NotNull InventoryOpenEvent event) {
        event.setCancelled(Freeze.isFreeze((Player) event.getPlayer()));
    }
}
