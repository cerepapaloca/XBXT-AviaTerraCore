package net.atcore.achievement.achievements.test;

import net.atcore.achievement.BaseAchievementStep;
import net.minecraft.advancements.AdvancementType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.List;

public class TestStep extends BaseAchievementStep<BlockBreakEvent> {

    public TestStep() {
        super(Material.OBSIDIAN, "bedrock/stone/step", AdvancementType.CHALLENGE);
    }

    @Override
    public void onEvent(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (event.getBlock().getType() == Material.OBSIDIAN) {
            grantAdvanced(player, "A");
        }
        if (event.getBlock().getType() == Material.STONE) {
            grantAdvanced(player, "B");
        }
        if (event.getBlock().getType() == Material.BEDROCK) {
            grantAdvanced(player, "C");
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
    protected List<String> listSteps() {
        return List.of("A", "B", "C");
    }
}
