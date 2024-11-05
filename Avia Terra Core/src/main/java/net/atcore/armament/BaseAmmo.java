package net.atcore.armament;

import lombok.Getter;
import lombok.Setter;
import net.atcore.messages.CategoryMessages;
import net.atcore.messages.MessagesManager;
import net.atcore.utils.GlobalUtils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

@Getter
@Setter
public abstract class BaseAmmo extends BaseArmament {

    protected BaseAmmo(ListAmmo listAmmon, double damage, String name, float penetration) {
        this(listAmmon, damage, name, Color.fromRGB(80,80,80), false, 1F, penetration);
    }

    protected BaseAmmo(ListAmmo listAmmon, double damage, String name, Color color, boolean isTrace, float densityTrace, float penetration) {
        super(name, new ItemStack(Material.SNOWBALL));
        this.damage = damage;
        this.listAmmon = listAmmon;
        this.color = color;
        this.isTrace = isTrace;
        this.densityTrace = densityTrace;
        this.penetration = penetration;
        ItemMeta itemMeta = itemArmament.getItemMeta();
        assert itemMeta != null;
        itemMeta.setDisplayName(displayName);
        itemMeta.setLore(GlobalUtils.StringToLoreString(MessagesManager.addProprieties(getProperties(), null, CategoryMessages.PRIVATE, false), true));
        itemArmament.setItemMeta(itemMeta);
        GlobalUtils.setPersistentDataItem(itemArmament, "nameAmmo", PersistentDataType.STRING, listAmmon.name());
    }

    private final float penetration;
    private final ListAmmo listAmmon;
    private final double damage;
    private final Color color;
    private final boolean isTrace;
    private final float densityTrace;

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
                ChatColor.of(GlobalUtils.colorToStringHex(color)) + GlobalUtils.colorToStringHex(color),
                densityTrace
        ));
        return properties.toString();
    }

    @Override
    public void updateLore(ItemStack itemStack, ItemStack itemStack2) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        assert itemMeta != null;
        itemMeta.setLore(GlobalUtils.StringToLoreString(MessagesManager.addProprieties(getProperties(), null, CategoryMessages.PRIVATE, false), true));
        itemArmament.setItemMeta(itemMeta);
    }

    public abstract void onShoot(DataShoot dataShoot);
}
