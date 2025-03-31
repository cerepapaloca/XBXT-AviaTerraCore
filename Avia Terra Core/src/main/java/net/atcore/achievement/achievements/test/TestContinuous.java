package net.atcore.achievement.achievements.test;

import net.atcore.achievement.BaseAchievementContinuous;
import net.minecraft.advancements.AdvancementType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

public class TestContinuous extends BaseAchievementContinuous<BlockBreakEvent> {

    public TestContinuous() {
        super(Material.BEDROCK, "bedrock", AdvancementType.TASK);
    }

    @Override
    public void onEvent(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (event.getBlock().getType() == Material.BEDROCK) {
            grantAdvanced(player, 1);
        }
    }

    @Override
    public void rewards(Player player) {

    }

    @Override
    protected int getY(String path) {
        return 0;
    }

    @Override
    public int getMetaValue() {
        return 4;
    }
}
