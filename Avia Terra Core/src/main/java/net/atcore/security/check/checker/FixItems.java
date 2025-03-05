package net.atcore.security.check.checker;

import net.atcore.security.check.CheckerUtils;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class FixItems extends InventoryChecker{

    @Override
    public void onCheck(Event event) {
        if (event instanceof InventoryEvent inventoryEvent){
            List<ItemStack> items = CheckerUtils.getItems(inventoryEvent);
            for (ItemStack item : items) {
                if (item != null && item.getItemMeta() != null) {
                    // Reduce la cantidad de items
                    item.setAmount(Math.min(item.getAmount(), item.getMaxStackSize()));
                }
            }
        }
    }
}
