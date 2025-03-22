package net.atcore.security.check;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

public abstract class InventoryChecker extends BaseCheckerMulti {

    @SuppressWarnings("unchecked")
    public InventoryChecker() {
        super(InventoryOpenEvent.class, InventoryClickEvent.class);
    }

}
