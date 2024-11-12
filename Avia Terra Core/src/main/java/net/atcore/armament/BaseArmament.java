package net.atcore.armament;

import lombok.Getter;
import lombok.Setter;
import net.atcore.AviaTerraCore;
import net.atcore.utils.GlobalUtils;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Nullable;

@Getter
@Setter
public abstract class BaseArmament {

    public BaseArmament(String displayName, ItemStack item, String armament) {
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
        new BukkitRunnable(){
            @Override
            public void run() {
                GlobalUtils.setPersistentDataItem(itemArmament,  armament + "Name", PersistentDataType.STRING, name);
            }
        }.runTaskLater(AviaTerraCore.getInstance(), 1);
    }

    protected final String displayName;
    protected String name;//esta variable es un final
    protected final ItemStack itemArmament;

    protected abstract void updateLore(ItemStack item,@Nullable ItemStack itemAuxiliar);
}
