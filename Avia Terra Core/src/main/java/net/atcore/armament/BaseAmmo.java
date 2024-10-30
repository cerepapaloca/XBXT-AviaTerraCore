package net.atcore.armament;

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
public abstract class BaseAmmo extends BaseArmament {

    protected BaseAmmo(ListAmmo listAmmon, double damage, String name) {
        this(listAmmon, damage, name, Color.fromRGB(80,80,80), false, 1F);
    }

    protected BaseAmmo(ListAmmo listAmmon, double damage, String name, Color color, boolean isTrace, float densityTrace) {
        super(name, new ItemStack(Material.SNOWBALL));
        this.damage = damage;
        this.listAmmon = listAmmon;
        this.color = color;
        this.isTrace = isTrace;
        this.densityTrace = densityTrace;
        ItemMeta itemMeta = itemArmament.getItemMeta();
        assert itemMeta != null;
        itemMeta.setDisplayName(displayName);
        itemMeta.setLore(GlobalUtils.StringToLoreString(getProperties(), true));
        itemArmament.setItemMeta(itemMeta);
        GlobalUtils.setPersistentDataItem(itemArmament, "nameAmmo", PersistentDataType.STRING, listAmmon.name());
    }

    private final ListAmmo listAmmon;
    private double damage;
    private Color color;
    private boolean isTrace;
    private float densityTrace;

    public String getProperties(){
        StringBuilder properties = new StringBuilder();
        properties.append(String.format("""
                  \n
                 MUNICIÓN
                 Calibre: %s
                 Daño: %s
                 Trazador: %s
                 """,
                displayName,
                damage,
                isTrace ? "si" : "no"
        ));
        if (!isTrace) return properties.toString();
        properties.append(String.format("""
                                    Color: %s
                                    Densidad del trazo: %s
                                    """,
                GlobalUtils.colorToStringHex(color),
                densityTrace
        ));
        return properties.toString();
    }

    public abstract void onShoot(DataShoot dataShoot);
}
