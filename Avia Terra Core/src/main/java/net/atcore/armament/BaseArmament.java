package net.atcore.armament;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

@Getter
@Setter
public abstract class BaseArmament {

    public BaseArmament(String name, ItemStack item) {
        this.displayName = name;
        this.itemArmament = item;
    }

    protected final String displayName;
    protected final ItemStack itemArmament;

    protected abstract void updateLore(ItemStack item,@Nullable ItemStack itemAuxiliar);
}
