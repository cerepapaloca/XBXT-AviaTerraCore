package net.atcore.achievement.achievements;

import net.atcore.achievement.BaseAchievementProgressive;
import net.minecraft.advancements.AdvancementType;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.meta.ItemMeta;

public class BreakEnderChestAchievement extends BaseAchievementProgressive<BlockBreakEvent> {

    public BreakEnderChestAchievement() {
        super(Material.OBSIDIAN, GetEnderChestAchievement.class, AdvancementType.TASK);
    }

    @Override
    public void onEvent(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        if (block.getType() == Material.ENDER_CHEST) {
            if (player.getInventory().getItemInMainHand().getItemMeta() != null) {
                ItemMeta meta = player.getInventory().getItemInMainHand().getItemMeta();
                Material material = player.getInventory().getItemInMainHand().getType();
                if (material == Material.IRON_PICKAXE || material == Material.DIAMOND_PICKAXE || material == Material.NETHERITE_PICKAXE){
                    if (!meta.hasEnchant(Enchantment.SILK_TOUCH)) grantAdvanced(player, 1);
                }
            }
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
