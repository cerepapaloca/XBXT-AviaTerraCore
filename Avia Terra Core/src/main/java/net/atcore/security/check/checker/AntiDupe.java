package net.atcore.security.check.checker;

import lombok.AllArgsConstructor;
import net.atcore.achievement.InventoryChangeEvent;
import net.atcore.security.check.BaseChecker;
import net.atcore.security.check.CheckerUtils;
import net.atcore.utils.AviaTerraScheduler;
import net.atcore.utils.GlobalUtils;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class AntiDupe extends BaseChecker<InventoryChangeEvent> {

    public AntiDupe() {
        bypassOp = false;
    }

    @Override
    public void onCheck(InventoryChangeEvent event) {
        List<ItemStack> itemsToRemove = CheckerUtils.getItems(event).stream().map(itemStack -> {
            if (itemStack.getItemMeta() == null) return null;
            String string = (String) GlobalUtils.getPersistenData(itemStack, "uuid", PersistentDataType.STRING);
            if (string == null) return null;
            if (string.equals("?")) {
                GlobalUtils.setPersistentData(itemStack, "uuid", PersistentDataType.STRING, UUID.randomUUID().toString());
            }
            return itemStack;
        }).toList();

        AviaTerraScheduler.enqueueTaskAsynchronously(() -> {
            HashMap<String, AntiDupeItem> itemCounts = new HashMap<>();
            for (ItemStack item : itemsToRemove) {
                if (item == null) continue;
                if (item.getItemMeta() == null) continue;
                String uniqueId = (String) GlobalUtils.getPersistenData(item, "uuid", PersistentDataType.STRING);
                if (uniqueId != null){
                    item.setAmount(1);
                    AntiDupeItem antiDupeItem = itemCounts.getOrDefault(uniqueId, new AntiDupeItem(uniqueId));
                    antiDupeItem.items.add(item);
                    itemCounts.put(uniqueId, antiDupeItem);
                }
            }
            AviaTerraScheduler.runTask(() -> {
                for (AntiDupeItem entry : itemCounts.values()) {
                    if (entry.items.size() > 1) entry.items.forEach(itemStack -> itemStack.setAmount(0));
                }
            });
        });
    }

    @AllArgsConstructor
    private static class AntiDupeItem {
        private final String uuid;
        private final List<ItemStack> items = new ArrayList<>();
    }
}
