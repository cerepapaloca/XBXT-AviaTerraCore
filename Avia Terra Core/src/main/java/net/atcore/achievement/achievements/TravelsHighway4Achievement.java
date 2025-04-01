package net.atcore.achievement.achievements;

import net.atcore.achievement.AchievementsUtils;
import net.atcore.achievement.BaseAchievementContinuous;
import net.atcore.achievement.SynchronouslyEvent;
import net.minecraft.advancements.AdvancementType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

public class TravelsHighway4Achievement extends BaseAchievementContinuous<PlayerMoveEvent> implements SynchronouslyEvent {
    public TravelsHighway4Achievement() {
        super(Material.NETHERITE_BOOTS, TravelsHighway3Achievement.class, AdvancementType.CHALLENGE);
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
        return 1_000_000;
    }
}
