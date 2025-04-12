package net.atcore.achievement.achievements;

import net.atcore.achievement.BaseAchievementSimple;
import net.atcore.achievement.InventoryChangeEvent;
import net.atcore.security.check.CheckerUtils;
import net.minecraft.advancements.AdvancementType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GetShulkerBoxAchievement extends BaseAchievementSimple<InventoryChangeEvent> {
    public GetShulkerBoxAchievement() {
        super(Material.SHULKER_BOX, RootAchievement.class, AdvancementType.TASK);
    }

    @Override
    public void onEvent(InventoryChangeEvent event) {
        for (ItemStack item : CheckerUtils.getItems(event)){
            if (item.getType() == Material.AIR || item.getItemMeta() == null) continue;
            if (item.getType().name().endsWith("SHULKER_BOX")) {
                grantAdvanced(event.getPlayer(), null);
                return;
            }
        }
    }

    @Override
    public void rewards(Player player) {

    }
}
