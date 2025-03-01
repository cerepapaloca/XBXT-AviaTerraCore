package net.atcore.armament;

import lombok.Getter;
import lombok.Setter;
import net.atcore.AviaTerraCore;
import net.atcore.messages.CategoryMessages;
import net.atcore.messages.MessagesManager;
import net.atcore.messages.TypeMessages;
import net.atcore.utils.GlobalUtils;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Getter
@Setter
public abstract class BaseArmament {

    public BaseArmament(String displayName, ItemStack item) {
        this.displayName = displayName;
        this.itemArmament = item;
        this.name = this.getClass().getName();
        ItemMeta meta = itemArmament.getItemMeta();
        if (meta == null) return;
        meta.displayName(MessagesManager.applyFinalProprieties(displayName, TypeMessages.ERROR, CategoryMessages.PRIVATE, false));
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);//se oculta datos del item para que no se vea feo
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        meta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        meta.addItemFlags(ItemFlag.HIDE_ARMOR_TRIM);
        meta.addItemFlags(ItemFlag.HIDE_DESTROYS);
        itemArmament.setItemMeta(meta);// TODO: Cambiar a esto por un sistema de uuids
        new BukkitRunnable(){
            public void run() {
                GlobalUtils.setPersistentData(itemArmament,"armament", PersistentDataType.STRING, name);
            }
        }.runTaskLater(AviaTerraCore.getInstance(), 1);
        ArmamentUtils.ARMAMENTS.add(this);
    }

    protected final String displayName;
    protected final String name;
    protected final ItemStack itemArmament;

    protected abstract void updateLore(ItemStack item, @Nullable ItemStack itemAuxiliar);

    /**
     * Es la abstracción principal del evento de disparo para que el
     * armamento pueda hacer interacciones especiales al momento
     * de hacer el disparo
     *
     * @param shootData da una instancia nueva de {@link ShootData}
     */

    public abstract void onShoot(List<ShootData> shootData);
}
