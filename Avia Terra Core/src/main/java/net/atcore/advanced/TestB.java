package net.atcore.advanced;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

public class TestB extends BaseAchievementSimple<BlockBreakEvent> {

    public TestB() {
        super(Material.STONE, "Test 1", "Test Description 1", "bedrock/stone");
    }

    @Override
    public void onEvent(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (event.getBlock().getType() == Material.STONE) {
            grantAdvanced(player);
        }
    }

    @Override
    public void onGrantAdvanced(Player player) {

    }
}
