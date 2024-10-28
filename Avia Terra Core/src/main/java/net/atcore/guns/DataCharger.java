package net.atcore.guns;

import lombok.Getter;
import lombok.Setter;
import net.atcore.utils.GlobalUtils;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

@Getter
@Setter
public class DataCharger {

    public DataCharger(ChargerList type, AmmoCaliber caliber, int ammoMax, double damage, String nameAmmo) {
        itemCharger = new ItemStack(Material.SUGAR);
        GlobalUtils.setPersistentDataItem(itemCharger, "chargerAmmo", PersistentDataType.INTEGER, ammoMax);
        GlobalUtils.setPersistentDataItem(itemCharger, "chargerType", PersistentDataType.STRING, type.name());
        this.nameAmmo = nameAmmo;
        this.caliber = caliber;
        this.ammoMax = ammoMax;
        this.damage = damage;
    }

    private final String nameAmmo;
    private final ItemStack itemCharger;
    private final double damage;
    private final AmmoCaliber caliber;
    private final int ammoMax;
    private Color color;
    private boolean isTrace;
    private float densityTrace;
}
