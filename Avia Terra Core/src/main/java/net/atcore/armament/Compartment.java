package net.atcore.armament;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface Compartment {
    void reload(Player player);
    boolean outCompartment(Player player, ItemStack item);
}
