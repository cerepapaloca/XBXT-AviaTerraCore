package net.atcore.advanced;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

public class Test extends BaseAchievementSimple<BlockBreakEvent> {

    public Test() {
        super(Material.OBSIDIAN, "Test", "Test Description", null);
    }

    @Override
    public void onEvent(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (event.getBlock().getType() == Material.OBSIDIAN) {
            grantAdvanced(player);
        }
    }

    @Override
    public void rewards(Player player) {

    }
}
