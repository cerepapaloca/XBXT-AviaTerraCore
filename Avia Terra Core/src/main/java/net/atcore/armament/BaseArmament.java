package net.atcore.armament;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

@Getter
@Setter
public abstract class BaseArmament {

    public BaseArmament(String displayName, ItemStack item, String name) {
        this.displayName = displayName;
        this.name = name;
        this.itemArmament = item;
    }

    protected final String displayName;
    protected final String name;
    protected final ItemStack itemArmament;

    protected abstract void updateLore(ItemStack item,@Nullable ItemStack itemAuxiliar);
}
