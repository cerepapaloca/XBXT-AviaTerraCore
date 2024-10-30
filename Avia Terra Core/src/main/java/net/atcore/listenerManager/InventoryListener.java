package net.atcore.listenerManager;

import net.atcore.armament.*;
import net.atcore.moderation.Ban.CheckAutoBan;
import net.atcore.moderation.Freeze;
import net.atcore.security.AntiExploit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

public class InventoryListener implements Listener {

    @EventHandler
    public void onInventoryClick(@NotNull InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ClickType clickType = event.getClick();
        Inventory inventory = event.getClickedInventory() != null ? event.getClickedInventory() : player.getInventory();
        CheckAutoBan.checkAntiIlegalItems(player, inventory);
        CheckAutoBan.checkDupe(player, inventory);
        AntiExploit.checkRangePurge(inventory);
        event.setCancelled(Freeze.isFreeze(player));
        if (clickType == ClickType.SWAP_OFFHAND) {
            BaseCompartment compartment = ArmamentUtils.getCompartment(event.getCurrentItem());
            if (compartment != null) {
                event.setCancelled(compartment.outCompartment(player, event.getCurrentItem()));
            }
        }
    }

    @EventHandler
    public void onInventoryOpen(@NotNull InventoryOpenEvent event) {
        Player player = (Player) event.getPlayer();
        Inventory inventory = event.getInventory();
        CheckAutoBan.checkAntiIlegalItems(player ,inventory);
        CheckAutoBan.checkDupe(player, inventory);
        AntiExploit.checkRangePurge(inventory);
        event.setCancelled(Freeze.isFreeze(player));
    }
}
