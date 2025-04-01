package net.atcore.achievement.achievements;

import net.atcore.achievement.AchievementsUtils;
import net.atcore.achievement.BaseAchievementSimple;
import net.atcore.achievement.PlayerInventoryChangeEvent;
import net.minecraft.advancements.AdvancementType;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class GetEndCrystalAchievement extends BaseAchievementSimple<PlayerInventoryChangeEvent> {
    public GetEndCrystalAchievement() {
        super(Material.END_CRYSTAL, KillPlayerAchievement.class, AdvancementType.TASK);
    }

    @Override
    public void onEvent(PlayerInventoryChangeEvent event) {
        if (AchievementsUtils.containsItem(event, Material.END_CRYSTAL)) grantAdvanced(event.getPlayer(), null);
    }

    @Override
    public void rewards(Player player) {

    }
}
