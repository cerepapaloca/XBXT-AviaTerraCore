package net.atcore.security.check.checker;

import net.atcore.achievement.InventoryChangeEvent;
import net.atcore.security.check.BaseChecker;
import net.atcore.security.check.CheckerUtils;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class FixItems extends BaseChecker<InventoryChangeEvent> {

    @Override
    public void onCheck(InventoryChangeEvent event) {
        List<ItemStack> items = CheckerUtils.getItems(event);
        for (ItemStack item : items) {
            if (item != null && item.getItemMeta() != null) {
                // Reduce la cantidad de items
                item.setAmount(Math.min(item.getAmount(), item.getMaxStackSize()));
            }
        }
    }
}
