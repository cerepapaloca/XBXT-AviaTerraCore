package net.atcore.listener;

import net.atcore.armament.*;
import net.atcore.inventory.ActionsInventoryManager;
import net.atcore.moderation.Freeze;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

public class InventoryListener implements Listener {

    @EventHandler
    public void onInventoryClick(@NotNull InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ClickType clickType = event.getClick();
        event.setCancelled(Freeze.isFreeze(player) ||
                ActionsInventoryManager.clickEvent(event) ||
                ArmamentActions.outAction(clickType, player, event.getCurrentItem())
        );
    }

    @EventHandler
    public void onInventoryOpen(@NotNull InventoryOpenEvent event) {
        Player player = (Player) event.getPlayer();
        Inventory inventory = event.getInventory();
        AntiExploit.checkRangePurge(inventory);
        event.setCancelled(Freeze.isFreeze(player));
    }

    @EventHandler
    public void onInventoryClose(@NotNull InventoryCloseEvent event) {
        ActionsInventoryManager.closeEvent(event);
    }

    @EventHandler
    public void onInventoryDrag(@NotNull InventoryDragEvent event) {
        event.setCancelled(ActionsInventoryManager.dragEvent(event));
    }
}
