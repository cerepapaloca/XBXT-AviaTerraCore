package net.atcore.achievement.achievements;

import net.atcore.achievement.AchievementsUtils;
import net.atcore.achievement.BaseAchievementSimple;
import net.atcore.achievement.InventoryChangeEvent;
import net.atcore.security.check.CheckerUtils;
import net.minecraft.advancements.AdvancementType;
import org.bukkit.Material;
import org.bukkit.block.Container;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;

public class FullChestFullShulkerBoxAchievement extends BaseAchievementSimple<InventoryChangeEvent> {
    public FullChestFullShulkerBoxAchievement() {
        super(Material.CHEST, FullShulkerBoxAchievement.class, AdvancementType.GOAL);
    }

    @Override
    public void onEvent(InventoryChangeEvent event) {
        int amountInventoryFull = 0;

        for (ItemStack item : CheckerUtils.getItems(event)) {
            if (item == null || item.getType() == Material.AIR) continue;
            if (item.getType().name().endsWith("SHULKER_BOX")) {
                BlockStateMeta blockStateMeta = (BlockStateMeta) item.getItemMeta();
                if (blockStateMeta != null && blockStateMeta.getBlockState() instanceof Container container) {
                    Inventory containerInventory = container.getInventory();
                    if (AchievementsUtils.isFull(containerInventory.getContents()))amountInventoryFull++;
                }
            }
        }
        if (amountInventoryFull == 90)grantAdvanced(event.getPlayer(), null);
    }



    @Override
    public void rewards(Player player) {

    }
}
