package net.atcore.armament;

import lombok.Getter;
import lombok.Setter;
import net.atcore.utils.GlobalUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

@Getter
@Setter
public abstract class BaseAmmo extends BaseArmament {

    protected BaseAmmo(double damage,
                       String displayName,
                       float penetration
    ) {
        super(displayName, new ItemStack(Material.END_ROD));
        this.damage = damage;
        this.penetration = penetration;
        updateLore(itemArmament, null);
    }

    private final float penetration;
    private final double damage;

    public String getProperties(){
        boolean isTrace = this instanceof Trace;
        StringBuilder properties = new StringBuilder();
        properties.append(String.format("""
                  \n
                 MUNICIÓN
                 Calibre: <|%s|>
                 Daño: <|%s|>
                 Trazador: <|%s|>
                 """,
                displayName,
                damage,
                isTrace ? "<|si|>" : "<|no|>"
        ));
        if (!isTrace) return properties.toString();
        Trace trace = (Trace) this;
        properties.append(String.format("""
                                    Color: <|%s|>
                                    Densidad del trazo: <|%s|>
                                    """,
                "<" + GlobalUtils.BukkitColorToStringHex(trace.getColorTrace()) + ">" + GlobalUtils.BukkitColorToStringHex(trace.getColorTrace()),
                trace.getDensityTrace()
        ));
        return properties.toString();
    }

    @Override
    public void updateLore(ItemStack itemStack, ItemStack itemStack2) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        assert itemMeta != null;
        itemMeta.lore(GlobalUtils.stringToLoreComponent(getProperties(), true));
        itemArmament.setItemMeta(itemMeta);
    }

    @Override
    public void onShoot(List<ShootData> shootData){

    }
}
