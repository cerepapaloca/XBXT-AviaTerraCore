package net.atcore.achievement.achievements;

import net.atcore.achievement.BaseAchievementSimple;
import net.atcore.security.check.CheckerUtils;
import net.minecraft.advancements.AdvancementType;
import org.bukkit.Material;
import org.bukkit.block.Container;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;

import java.util.HashSet;
import java.util.List;

public class ShulkerFullAchievement extends BaseAchievementSimple<InventoryClickEvent> {
    public ShulkerFullAchievement() {
        super(Material.GRAY_SHULKER_BOX, InventoryAchievement.class, AdvancementType.TASK);
    }

    @Override
    public void onEvent(InventoryClickEvent event) {
        List<ItemStack> list = CheckerUtils.getItems(event);
        if (event.getWhoClicked().getOpenInventory().getTopInventory().getType() == InventoryType.SHULKER_BOX &&
        isFull(event.getWhoClicked().getOpenInventory().getTopInventory().getContents())){
            grantAdvanced((Player) event.getWhoClicked(), null);
            return;
        }
        for (ItemStack item : list) {
            if (item == null || item.getType() == Material.AIR) continue;
            if (item.getType().name().endsWith("SHULKER_BOX")) {
                BlockStateMeta blockStateMeta = (BlockStateMeta) item.getItemMeta();
                if (blockStateMeta != null && blockStateMeta.getBlockState() instanceof Container container) {
                    Inventory containerInventory = container.getInventory();
                    if (isFull(containerInventory.getContents())) grantAdvanced((Player) event.getWhoClicked(), null);
                }
            }
        }

    }

    public boolean isFull(ItemStack[] items) {
        HashSet<Material> materials = new HashSet<>();
        for (ItemStack itemStack : items) {
            if (itemStack == null || itemStack.getType() == Material.AIR || itemStack.getItemMeta() == null) return false;
            if (itemStack.getMaxStackSize() != itemStack.getAmount()) return false;
            materials.add(itemStack.getType());
        }
        return (materials.size() == 1) ;
    }

    @Override
    public void rewards(Player player) {

    }
}
