package net.atcore.achievement.achievements;

import net.atcore.achievement.AchievementsUtils;
import net.atcore.achievement.BaseAchievementSimple;
import net.atcore.achievement.InventoryChangeEvent;
import net.minecraft.advancements.AdvancementType;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class GetEndCrystalAchievement extends BaseAchievementSimple<InventoryChangeEvent> {
    public GetEndCrystalAchievement() {
        super(Material.END_CRYSTAL, KillPlayerAchievement.class, AdvancementType.TASK);
    }

    @Override
    public void onEvent(InventoryChangeEvent event) {
        if (AchievementsUtils.containsItem(event, Material.END_CRYSTAL)) grantAdvanced(event.getPlayer(), null);
    }

    @Override
    public void rewards(Player player) {

    }
}
