package net.atcore.armament;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

@Getter
@Setter
public abstract class BaseArmament {

    public BaseArmament(String displayName, ItemStack item) {
        this.displayName = displayName;
        this.itemArmament = item;
        ItemMeta meta = itemArmament.getItemMeta();
        if (meta == null) return;
        meta.setDisplayName(displayName);
        meta.removeItemFlags(ItemFlag.HIDE_ATTRIBUTES);//se oculta datos del item para que no se vea feo
        meta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.removeItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        meta.removeItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        itemArmament.setItemMeta(meta);
    }

    protected final String displayName;
    protected String name;
    protected final ItemStack itemArmament;

    protected abstract void updateLore(ItemStack item,@Nullable ItemStack itemAuxiliar);
}
