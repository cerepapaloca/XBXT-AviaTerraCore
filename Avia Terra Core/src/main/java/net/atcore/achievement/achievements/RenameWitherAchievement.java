package net.atcore.achievement.achievements;

import net.atcore.achievement.BaseAchievementSimple;
import net.minecraft.advancements.AdvancementType;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wither;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

public class RenameWitherAchievement extends BaseAchievementSimple<PlayerInteractEntityEvent> {
    public RenameWitherAchievement() {
        super(Material.NAME_TAG, DeathByWhitherAchievement.class, AdvancementType.TASK);
    }

    @Override
    public void onEvent(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        Entity entity = event.getRightClicked();
        ItemStack item = player.getInventory().getItem(event.getHand());

        if (item.getType() == Material.NAME_TAG && item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
            if (entity instanceof Wither) grantAdvanced(player, null);
        }
    }

    @Override
    public void rewards(Player player) {

    }
}
