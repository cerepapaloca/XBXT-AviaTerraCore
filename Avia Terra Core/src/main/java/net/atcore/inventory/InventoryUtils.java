package net.atcore.inventory;

import lombok.experimental.UtilityClass;
import net.atcore.inventory.inventors.ManipulatorInventory;
import net.atcore.utils.GlobalUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

@UtilityClass
public class InventoryUtils {

    public void fillItems(Inventory inventory, ItemStack items, int a, int b) {
        for (int i = a; i < b; i++) {
            inventory.setItem(i, items);
        }
    }

    public ItemStack newItems(Material material, String name, int amount, @Nullable String lore) {
        ItemStack item = new ItemStack(material, amount);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        if (lore != null) meta.setLore(GlobalUtils.StringToLoreString(lore, true));
        for (ItemFlag flag : ItemFlag.values()) {
            meta.addItemFlags(flag);
        }
        item.setItemMeta(meta);
        return item;
    }
}
