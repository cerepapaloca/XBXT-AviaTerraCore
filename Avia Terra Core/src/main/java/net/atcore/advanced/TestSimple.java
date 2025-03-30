package net.atcore.advanced;

import net.minecraft.advancements.AdvancementType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

public class TestSimple extends BaseAchievementSimple<BlockBreakEvent> {

    public TestSimple() {
        super(Material.OBSIDIAN, "Test", "Test Description", null, AdvancementType.GOAL);
    }

    @Override
    public void onEvent(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (event.getBlock().getType() == Material.OBSIDIAN) {
            grantAdvanced(player, null);
        }
    }

    @Override
    public void rewards(Player player) {

    }
}
