package net.atcore.achievement.achievements.test;

import net.atcore.achievement.BaseAchievementProgressive;
import net.minecraft.advancements.AdvancementType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

public class TestProgressive extends BaseAchievementProgressive<BlockBreakEvent> {

    public TestProgressive() {
        super(Material.STONE, TestContinuous.class, AdvancementType.GOAL);
    }

    @Override
    public void onEvent(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (event.getBlock().getType() == Material.STONE) {
            grantAdvanced(player, null);
        }
    }

    @Override
    public void rewards(Player player) {

    }

    @Override
    protected int getMetaProgress() {
        return 5;
    }
}
