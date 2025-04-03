package net.atcore.achievement.achievements;

import net.atcore.achievement.AchievementsUtils;
import net.atcore.achievement.BaseAchievementContinuous;
import net.minecraft.advancements.AdvancementType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

public class TravelsHighway2Achievement extends BaseAchievementContinuous<PlayerMoveEvent> {
    public TravelsHighway2Achievement() {
        super(Material.GOLDEN_BOOTS, TravelsHighway1Achievement.class, AdvancementType.TASK);
    }

    @Override
    public void onEvent(PlayerMoveEvent event) {
        if (AchievementsUtils.isNearCardinalAndDiagonal(event.getTo())){
            double distance = event.getTo().distance(event.getFrom());
            grantAdvanced(event.getPlayer(), distance);
        }
    }

    @Override
    public void rewards(Player player) {

    }

    @Override
    public int getMetaValue() {
        return 5_000;
    }
}
