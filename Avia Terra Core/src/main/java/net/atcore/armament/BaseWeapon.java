package net.atcore.armament;

import lombok.Getter;
import lombok.Setter;
import net.atcore.utils.GlobalUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

@Getter
@Setter
public abstract class BaseWeapon extends BaseArmament implements ShootWeapon{
    public BaseWeapon(String name, ItemStack item, int MaxDistance, String nameListe, double precision) {
        super(name, item);
        this.maxDistance = MaxDistance;
        this.precision = precision;
        GlobalUtils.setPersistentDataItem(itemArmament, "weaponName", PersistentDataType.STRING, nameListe);
    }

    protected final int maxDistance;
    protected final double precision;

    @Override
    public abstract void shoot(Player player);
}
