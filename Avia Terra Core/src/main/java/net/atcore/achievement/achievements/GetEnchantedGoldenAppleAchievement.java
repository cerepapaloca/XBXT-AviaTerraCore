package net.atcore.achievement.achievements;

import net.atcore.achievement.AchievementsUtils;
import net.atcore.achievement.BaseAchievementSimple;
import net.atcore.achievement.PlayerInventoryChangeEvent;
import net.minecraft.advancements.AdvancementType;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class GetEnchantedGoldenAppleAchievement extends BaseAchievementSimple<PlayerInventoryChangeEvent> {
    public GetEnchantedGoldenAppleAchievement() {
        super(Material.ENCHANTED_GOLDEN_APPLE, RootAchievement.class, AdvancementType.TASK);
    }

    @Override
    public void onEvent(PlayerInventoryChangeEvent event) {
        if (AchievementsUtils.containsItem(event, Material.ENCHANTED_GOLDEN_APPLE)) grantAdvanced(event.getPlayer(), null);
    }

    @Override
    public void rewards(Player player) {

    }
}
