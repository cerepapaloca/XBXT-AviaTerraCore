package net.atcore.achievement.achievements;

import net.atcore.achievement.BaseAchievementSimple;
import net.atcore.achievement.SynchronouslyEvent;
import net.atcore.listener.DeathListener;
import net.atcore.utils.AviaTerraScheduler;
import net.minecraft.advancements.AdvancementType;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;

public class KillPlayerAchievement extends BaseAchievementSimple<PlayerDeathEvent> implements SynchronouslyEvent {
    public KillPlayerAchievement() {
        super(Material.IRON_SWORD, RootAchievement.class, AdvancementType.TASK);
    }

    @Override
    public void onEvent(PlayerDeathEvent event) {
        Player player = event.getEntity();
        LivingEntity killer = player.getKiller();
        if (killer == null) killer = DeathListener.getKillerByDamage(player);
        if (killer == null) return;
        if (killer instanceof Player playerKiller) AviaTerraScheduler.enqueueTaskAsynchronously(() -> grantAdvanced(playerKiller, null));
    }

    @Override
    public void rewards(Player player) {

    }
}
