package net.atcore.achievement.achievements;

import net.atcore.achievement.BaseAchievementSimple;
import net.atcore.achievement.SynchronouslyEvent;
import net.minecraft.advancements.AdvancementType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;

public class InventoryAchievement extends BaseAchievementSimple<EntityPickupItemEvent> implements SynchronouslyEvent {
    public InventoryAchievement() {
        super(Material.GREEN_SHULKER_BOX, "inventory", AdvancementType.TASK);
    }

    @Override
    public void onEvent(EntityPickupItemEvent event) {
        ItemStack item = event.getItem().getItemStack();
        if (event.getEntity() instanceof Player player) {
            if (item.getType() == Material.AIR || item.getItemMeta() == null) return;
            if (item.getType().name().endsWith("SHULKER_BOX")) grantAdvanced(player, null);
        }
    }

    @Override
    public void rewards(Player player) {

    }

    @Override
    protected int getY(String path) {
        return 1;
    }
}
