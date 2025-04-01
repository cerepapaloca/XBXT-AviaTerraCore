package net.atcore.achievement.achievements;

import net.atcore.achievement.BaseAchievementStep;
import net.atcore.security.check.CheckerUtils;
import net.minecraft.advancements.AdvancementType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class AllShulkerBoxAchievement extends BaseAchievementStep<InventoryClickEvent> {
    public AllShulkerBoxAchievement() {
        super(Material.WHITE_SHULKER_BOX, InventoryAchievement.class, AdvancementType.TASK);
    }

    @Override
    protected List<String> listSteps() {
        return List.of("SHULKER_BOX", "WHITE_SHULKER_BOX", "ORANGE_SHULKER_BOX", "MAGENTA_SHULKER_BOX", "LIGHT_BLUE_SHULKER_BOX", "YELLOW_SHULKER_BOX",
                "LIME_SHULKER_BOX", "PINK_SHULKER_BOX", "GRAY_SHULKER_BOX", "LIGHT_GRAY_SHULKER_BOX", "CYAN_SHULKER_BOX", "PURPLE_SHULKER_BOX", "BLUE_SHULKER_BOX",
                "BROWN_SHULKER_BOX", "GREEN_SHULKER_BOX", "RED_SHULKER_BOX", "BLACK_SHULKER_BOX"
        );
    }

    @Override
    public void onEvent(InventoryClickEvent event) {
        List<ItemStack> list = CheckerUtils.getItems(event);
        for (ItemStack item : list) {
            if (item == null || item.getType() == Material.AIR) continue;
            if (item.getType().name().endsWith("SHULKER_BOX")) {
                grantAdvanced((Player) event.getWhoClicked(), item.getType().name());
            }
        }
    }

    @Override
    public void rewards(Player player) {

    }
}
