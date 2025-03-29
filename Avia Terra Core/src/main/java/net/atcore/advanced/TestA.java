package net.atcore.advanced;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

public class TestA extends BaseAchievementSimple<BlockBreakEvent> {

    public TestA() {
        super(Material.BEDROCK, "Test 1", "Test Description 1", "bedrock");
    }

    @Override
    public void onEvent(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (event.getBlock().getType() == Material.BEDROCK) {
            grantAdvanced(player);
        }
    }

    @Override
    public void onGrantAdvanced(Player player) {

    }
}
