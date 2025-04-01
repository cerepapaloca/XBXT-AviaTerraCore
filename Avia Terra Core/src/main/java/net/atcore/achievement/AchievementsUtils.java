package net.atcore.achievement;

import lombok.experimental.UtilityClass;
import net.atcore.security.check.CheckerUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;

@UtilityClass
public class AchievementsUtils {

    public boolean isNearCardinalAndDiagonal(Location location) {

        int px = location.getBlockX();
        int pz = location.getBlockZ();
        int tx = 0;
        int tz = 0;

        // Verificar si el jugador está exactamente a 6 bloques en x o z
        if (Math.abs(px - tx) < 6) return true; // En x+ o x-
        if (Math.abs(pz - tz) < 6) return true; // En z+ o z-

        // Verificar si el jugador está exactamente a 6 bloques en las diagonales
        return (Math.abs(Math.abs(px) - Math.abs(pz)) < 6); // Diagonal
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

    public static boolean containsItem(InventoryEvent event, Material material) {
        for (ItemStack item : CheckerUtils.getItems(event)) {
            if (item.getType() == Material.AIR || item.getItemMeta() == null) continue;
            if (item.getType().equals(material)) {
                return true;
            }
        }
        return false;
    }
}
