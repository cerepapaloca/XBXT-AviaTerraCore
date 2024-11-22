package net.atcore.listenerManager;

import net.atcore.armament.*;
import net.atcore.inventory.ActionsInventoryManager;
import net.atcore.moderation.Ban.CheckAutoBan;
import net.atcore.moderation.Freeze;
import net.atcore.security.AntiExploit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

public class InventoryListener implements Listener {

    @EventHandler
    public void onInventoryClick(@NotNull InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ClickType clickType = event.getClick();
        Inventory inventory = event.getClickedInventory() != null ? event.getClickedInventory() : player.getInventory();
        CheckAutoBan.checkAntiIlegalItems(player, inventory);
        if (clickType.equals(ClickType.LEFT) || clickType.equals(ClickType.RIGHT)) CheckAutoBan.checkDupe(player, inventory);
        event.setCancelled(Freeze.isFreeze(player) ||
                ActionsInventoryManager.clickEvent(event) ||
                ArmamentActions.outAction(clickType, player, event.getCurrentItem())
        );
    }

    @EventHandler
    public void onInventoryOpen(@NotNull InventoryOpenEvent event) {
        Player player = (Player) event.getPlayer();
        Inventory inventory = event.getInventory();
        CheckAutoBan.checkAntiIlegalItems(player ,inventory);
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
