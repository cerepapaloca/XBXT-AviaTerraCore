package net.atcore.armament;

import lombok.Getter;
import lombok.Setter;
import net.atcore.utils.GlobalUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

@Getter
@Setter
public abstract class BaseWeapon extends BaseArmament implements ShootWeapon{
    public BaseWeapon(String name, ItemStack item, int MaxDistance, String nameListe) {
        super(name, item);
        this.maxDistance = MaxDistance;
        GlobalUtils.setPersistentDataItem(itemArmament, "weaponName", PersistentDataType.STRING, nameListe);
    }

    protected final int maxDistance;

    @Override
    public abstract void shoot(Player player);
}
