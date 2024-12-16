package net.atcore.armament;

import lombok.Getter;
import lombok.Setter;
import net.atcore.AviaTerraCore;
import net.atcore.utils.GlobalUtils;
import org.bukkit.Bukkit;
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
        meta.setDisplayName(GlobalUtils.applyGradient("<#BCBCBC>" + displayName + "<#777777>", 'l'));
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);//se oculta datos del item para que no se vea feo
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        meta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        meta.addItemFlags(ItemFlag.HIDE_ARMOR_TRIM);
        meta.addItemFlags(ItemFlag.HIDE_DESTROYS);
        itemArmament.setItemMeta(meta);
        new BukkitRunnable(){
            public void run() {
                GlobalUtils.setPersistentData(itemArmament,armament + "Name", PersistentDataType.STRING, name);
            }
        }.runTaskLater(AviaTerraCore.getInstance(), 1);
    }

    protected final String displayName;
    protected String name;//esta variable es un final
    protected final ItemStack itemArmament;

    protected abstract void updateLore(ItemStack item, @Nullable ItemStack itemAuxiliar);

    /**
     * Es la abstracción principal del evento de disparo para que el
     * armamento pueda hacer interacciones especiales al momento
     * de hacer el disparo
     *
     * @param dataShoot da una instancia nueva de {@link DataShoot}
     */

    public abstract void onShoot(DataShoot dataShoot);
}
