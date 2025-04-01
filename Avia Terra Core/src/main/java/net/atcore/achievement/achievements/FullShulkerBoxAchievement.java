package net.atcore.achievement.achievements;

import net.atcore.achievement.AchievementsUtils;
import net.atcore.achievement.BaseAchievementSimple;
import net.atcore.achievement.PlayerInventoryChangeEvent;
import net.atcore.security.check.CheckerUtils;
import net.minecraft.advancements.AdvancementType;
import org.bukkit.Material;
import org.bukkit.block.Container;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;

import java.util.List;

public class FullShulkerBoxAchievement extends BaseAchievementSimple<PlayerInventoryChangeEvent> {
    public FullShulkerBoxAchievement() {
        super(Material.GRAY_SHULKER_BOX, GetShulkerBoxAchievement.class, AdvancementType.TASK);
    }

    @Override
    public void onEvent(PlayerInventoryChangeEvent event) {
        List<ItemStack> list = CheckerUtils.getItems(event);
        Player player = event.getPlayer();
        if (player.getOpenInventory().getTopInventory().getType() == InventoryType.SHULKER_BOX &&
        AchievementsUtils.isFull(player.getOpenInventory().getTopInventory().getContents())){
            grantAdvanced(player, null);
            return;
        }
        for (ItemStack item : list) {
            if (item == null || item.getType() == Material.AIR) continue;
            if (item.getType().name().endsWith("SHULKER_BOX")) {
                BlockStateMeta blockStateMeta = (BlockStateMeta) item.getItemMeta();
                if (blockStateMeta != null && blockStateMeta.getBlockState() instanceof Container container) {
                    Inventory containerInventory = container.getInventory();
                    if (AchievementsUtils.isFull(containerInventory.getContents())) grantAdvanced(player, null);
                }
            }
        }

    }

    @Override
    public void rewards(Player player) {

    }
}
