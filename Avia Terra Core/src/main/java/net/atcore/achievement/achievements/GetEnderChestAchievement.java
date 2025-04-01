package net.atcore.achievement.achievements;

import net.atcore.achievement.AchievementsUtils;
import net.atcore.achievement.BaseAchievementSimple;
import net.atcore.achievement.PlayerInventoryChangeEvent;
import net.minecraft.advancements.AdvancementType;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class GetEnderChestAchievement extends BaseAchievementSimple<PlayerInventoryChangeEvent> {
    public GetEnderChestAchievement() {
        super(Material.ENDER_CHEST, GetShulkerBoxAchievement.class, AdvancementType.TASK);
    }

    @Override
    public void onEvent(PlayerInventoryChangeEvent event) {
        if (AchievementsUtils.containsItem(event, Material.ENDER_CHEST)) grantAdvanced(event.getPlayer(), null);
    }

    @Override
    public void rewards(Player player) {

    }
}
