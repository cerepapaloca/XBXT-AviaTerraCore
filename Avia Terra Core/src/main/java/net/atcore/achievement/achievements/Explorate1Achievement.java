package net.atcore.achievement.achievements;

import net.atcore.achievement.AchievementsUtils;
import net.atcore.achievement.BaseAchievementSimple;
import net.minecraft.advancements.AdvancementType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

public class Explorate1Achievement extends BaseAchievementSimple<PlayerMoveEvent> {
    public Explorate1Achievement() {
        super(Material.COMPASS, RootAchievement.class, AdvancementType.TASK);
    }

    @Override
    public void onEvent(PlayerMoveEvent event) {
        if (AchievementsUtils.isNearCardinalAndDiagonal(event.getTo())){
            grantAdvanced(event.getPlayer(), null);
        }
    }

    @Override
    public void rewards(Player player) {

    }
}
