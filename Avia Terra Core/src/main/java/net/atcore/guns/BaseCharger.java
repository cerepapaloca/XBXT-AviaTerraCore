package net.atcore.guns;

import lombok.Getter;
import lombok.Setter;
import net.atcore.utils.GlobalUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public abstract class BaseCharger {

    public BaseCharger(ListCharger type, ListAmmo caliber, int ammoMax, String displayName){
        this(type, Collections.nCopies(ammoMax, caliber), ammoMax, displayName);

    }

    public BaseCharger(ListCharger type, List<ListAmmo> caliber, int ammoMax, String displayName) {
        List<ListAmmo> list = new ArrayList<>();
        for (int i = 0; i < ammoMax; i++) {
            list.add(caliber.get(i % caliber.size()));
        }
        List<BaseAmmo> listAmmo = new ArrayList<>();
        for (ListAmmo ammo : list) {
            listAmmo.add(GunsSection.baseAmmo.get(ammo));
        }
        List<String> listAmmoName = new ArrayList<>();
        for (ListAmmo ammo : list) {
            listAmmoName.add(ammo.name());
        }
        this.chargerType = type;
        this.ammonList = listAmmo;
        this.ammoMax = ammoMax;
        this.displayName = displayName;
        itemCharger = new ItemStack(Material.SUGAR);
        ItemMeta itemMeta = itemCharger.getItemMeta();
        assert itemMeta != null;
        itemMeta.setDisplayName(displayName);
        itemCharger.setItemMeta(itemMeta);
        GlobalUtils.setPersistentDataItem(itemCharger, "chargerAmmo", PersistentDataType.STRING, GunsSection.listToString(listAmmoName));
        GlobalUtils.setPersistentDataItem(itemCharger, "chargerType", PersistentDataType.STRING, type.name());

        getProperties(itemCharger);
    }

    private final String displayName;
    private final ListCharger chargerType;
    private final ItemStack itemCharger;
    private final List<BaseAmmo> ammonList;
    private final int ammoMax;

    public String getProperties(ItemStack item){
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return "?";
        List<String> list = GunsSection.stringToList((String) GlobalUtils.getPersistenData(item, "chargerAmmo", PersistentDataType.STRING));
        int amountAmmo = list.size();
        if (list.getFirst().isBlank()) amountAmmo = 0;
        String s = "CARGADOR\n" +
                "Nombre: " + displayName + "\n" +
                "Munición: " + amountAmmo + "\n" +
                "Munición maxima: " + ammoMax + "\n" +
                "Velocidad de carga: ? \n \n" +
                (amountAmmo != 0 ?
                "MUNICIÓN \n" +
                "Calibre: " + ammonList.getFirst().getNameAmmo() + "\n" +
                "Daño: " + ammonList.getFirst().getDamage() +
                "Trazadora: " + (ammonList.getFirst().isTrace() ? "si": "no") + "\n" +
                (ammonList.getFirst().isTrace() ?
                "Color: " + GlobalUtils.colorToStringHex(ammonList.getFirst().getColor()) + "\n" +
                "Densidad del trazo: " + ammonList.getFirst().getDensityTrace(): "") : "SIN BALAS");

        meta.setLore(GlobalUtils.StringToLoreString(s, true));
        item.setItemMeta(meta);
        return s;
    }

    public abstract void onShoot(DataShoot dataShoot);
}
