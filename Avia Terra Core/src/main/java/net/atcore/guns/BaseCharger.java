package net.atcore.guns;

import lombok.Getter;
import lombok.Setter;
import net.atcore.utils.GlobalUtils;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

@Getter
@Setter
public abstract class BaseCharger {

    public BaseCharger(ChargerList type, AmmoCaliber caliber, int ammoMax, double damage, String nameAmmo, String displayName){
        this(type, caliber, ammoMax, damage, nameAmmo, 0.2F, displayName);
    }

    public BaseCharger(ChargerList type, AmmoCaliber caliber, int ammoMax, double damage, String nameAmmo, float densityTrace, String displayName) {
        itemCharger = new ItemStack(Material.SUGAR);
        ItemMeta itemMeta = itemCharger.getItemMeta();
        assert itemMeta != null;
        itemMeta.setDisplayName(displayName);
        itemCharger.setItemMeta(itemMeta);
        GlobalUtils.setPersistentDataItem(itemCharger, "chargerAmmo", PersistentDataType.INTEGER, ammoMax);
        GlobalUtils.setPersistentDataItem(itemCharger, "chargerType", PersistentDataType.STRING, type.name());
        this.chargerType = type;
        this.nameAmmo = nameAmmo;
        this.caliber = caliber;
        this.ammoMax = ammoMax;
        this.damage = damage;
        this.densityTrace = densityTrace;
        this.color = Color.fromRGB(110,110,110);
    }

    protected final ChargerList chargerType;
    protected final String nameAmmo;
    protected final ItemStack itemCharger;
    protected final double damage;
    protected final AmmoCaliber caliber;
    protected final int ammoMax;
    protected Color color;
    protected boolean isTrace;
    protected float densityTrace;

    public abstract void onShoot(DataShoot dataShoot);
}
