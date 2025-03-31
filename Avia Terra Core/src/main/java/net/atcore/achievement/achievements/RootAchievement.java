package net.atcore.achievement.achievements;

import net.atcore.achievement.BaseAchievementSimple;
import net.minecraft.advancements.AdvancementType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

public class RootAchievement extends BaseAchievementSimple<BlockBreakEvent> {
    public RootAchievement() {
        super(Material.OBSIDIAN, null, AdvancementType.TASK);
    }

    @Override
    public void onEvent(BlockBreakEvent event) {

    }

    @Override
    public void rewards(Player player) {

    }

    @Override
    protected int getY(String path) {
        return 0;
    }
}
