package net.atcore.achievement.achievements;

import net.atcore.achievement.BaseAchievementSimple;
import net.minecraft.advancements.AdvancementType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

public class EscapesUsedChorusFruit extends BaseAchievementSimple<PlayerTeleportEvent> {
    public EscapesUsedChorusFruit() {
        super(Material.CHORUS_FRUIT, KillPlayerAchievement.class, AdvancementType.TASK);
    }

    @Override
    public void onEvent(PlayerTeleportEvent event) {
        if (event.getCause().equals(PlayerTeleportEvent.TeleportCause.CHORUS_FRUIT)) {
            Location loc = event.getFrom();
            if (!loc.clone().add(1, 0, 0).getBlock().isSolid()) return;
            if (!loc.clone().add(-1, 0, 0).getBlock().isSolid()) return;
            if (!loc.clone().add(0, 0, 1).getBlock().isSolid()) return;
            if (!loc.clone().add(0, 0, -1).getBlock().isSolid()) return;

            if (!loc.clone().add(1, 1, 0).getBlock().isSolid()) return;
            if (!loc.clone().add(-1, 1, 0).getBlock().isSolid()) return;
            if (!loc.clone().add(0, 1, 1).getBlock().isSolid()) return;
            if (!loc.clone().add(0, 1, -1).getBlock().isSolid()) return;

            if (!loc.clone().add(0, 2, 0).getBlock().isSolid()) return;
            grantAdvanced(event.getPlayer(), null);
        }
    }

    @Override
    public void rewards(Player player) {

    }
}
