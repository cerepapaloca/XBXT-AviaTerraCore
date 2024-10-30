package net.atcore.armament;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class BaseCompartment extends BaseArmament {

    public BaseCompartment(String name, ItemStack item) {
        super(name, item);
    }

    public abstract void reload(Player player);

    public abstract boolean outCompartment(Player player, ItemStack item);
}
