package net.atcore.armament;

import lombok.Getter;
import lombok.Setter;
import net.atcore.utils.GlobalUtils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

@Getter
@Setter
public abstract class BaseAmmo extends BaseArmament {

    protected BaseAmmo(double damage,
                       String displayName,
                       float penetration,
                       int projectiles
    ) {
        this(damage,
                displayName,
                Color.fromRGB(80,80,80),
                false,
                1F,
                penetration,
                projectiles
        );
    }

    protected BaseAmmo(double damage,
                       String displayName,
                       Color color,
                       boolean isTrace,
                       float densityTrace,
                       float penetration,
                       int projectiles
    ) {
        super(displayName, new ItemStack(Material.END_ROD), "ammo");
        this.damage = damage;
        this.color = color;
        this.isTrace = isTrace;
        this.densityTrace = densityTrace;
        this.penetration = penetration;
        this.projectiles = projectiles;
        updateLore(itemArmament, null);
    }

    private final float penetration;
    private final double damage;
    private final Color color;
    private final boolean isTrace;
    private final float densityTrace;
    private final int projectiles;

    public String getProperties(){
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
        properties.append(String.format("""
                                    Color: <|%s|>
                                    Densidad del trazo: <|%s|>
                                    """,
                "<" + GlobalUtils.BukkitColorToStringHex(color) + ">" + GlobalUtils.BukkitColorToStringHex(color),
                densityTrace
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
