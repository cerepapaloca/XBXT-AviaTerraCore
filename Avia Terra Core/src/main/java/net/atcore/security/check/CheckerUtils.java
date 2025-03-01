package net.atcore.security.check;

import lombok.experimental.UtilityClass;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@UtilityClass
public class CheckerUtils {


    public @NotNull List<ItemStack> getItems(InventoryEvent event) {
        HumanEntity player = event.getView().getPlayer();
        List<ItemStack> items = new ArrayList<>(Arrays.stream(player.getOpenInventory().getTopInventory().getContents()).filter(item -> item != null && item.getType() != Material.AIR).toList());
        items.addAll(Arrays.stream(player.getInventory().getContents()).filter(item -> item != null && item.getType() != Material.AIR).toList());
        return items;
    }
}
