package net.atcore.guns;

import lombok.Getter;
import lombok.Setter;
import net.atcore.utils.GlobalUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

@Getter
@Setter
public abstract class BaseCharger {

    public BaseCharger(ListCharger type, ListAmmo caliber, int ammoMax, String displayName, int reloadTime){
        this(type, Collections.nCopies(ammoMax, caliber), ammoMax, displayName, reloadTime);

    }

    public BaseCharger(ListCharger type, List<ListAmmo> caliber, int ammoMax, String displayName, int reloadTime) {
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
        this.reloadTime = reloadTime;
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

        getProperties(itemCharger, true);
    }

    private final String displayName;
    private final ListCharger chargerType;
    private final ItemStack itemCharger;
    private final List<BaseAmmo> ammonList;
    private final int ammoMax;
    private final int reloadTime;//en TICK

    public String getProperties(ItemStack item, boolean setLore){
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return "?";
        String stringAmmo = (String) GlobalUtils.getPersistenData(item, "chargerAmmo", PersistentDataType.STRING);
        List<BaseAmmo> AmmoBaseList = new ArrayList<>();
        if (stringAmmo != null) {
            for (String nameAmmo : GunsSection.stringToList(stringAmmo)) AmmoBaseList.add(GunsSection.getAmmon(nameAmmo));
            int amountAmmo = AmmoBaseList.size();
            String loreCargador = String.format("""
                    %s
                    CARGADOR
                    Nombre %s
                    Municion %s
                    Munición maxima %s
                    Velocidad de recarga %ss
                    """,
                    setLore ? "" : " \n",
                    displayName,
                    amountAmmo,
                    ammoMax,
                    (reloadTime/20)
            );
            StringBuilder loreAmmo = new StringBuilder();
            if (!AmmoBaseList.isEmpty()) {
                Set<BaseAmmo> uniqueAmmo = new HashSet<>(AmmoBaseList);
                for(BaseAmmo ammo : uniqueAmmo){
                    loreAmmo.append(String.format("""
                                     \n
                                    MUNICIÓN
                                    Calibre: %s
                                    Daño: %s
                                    Trazador: %s
                                    """,
                            ammo.getNameAmmo(),
                            ammo.getDamage(),
                            ammo.isTrace() ? "si" : "no"
                    ));
                    if (!ammo.isTrace()) continue;
                    loreAmmo.append(String.format("""
                                    Color: %s
                                    Densidad del trazo: %s
                                    """,
                            GlobalUtils.colorToStringHex(ammo.getColor()),
                            ammo.getDensityTrace()
                    ));
                }
            }

            String finalLore = loreCargador + (amountAmmo > 0 ? loreAmmo.toString() : "");
            if (setLore){
                meta.setLore(GlobalUtils.StringToLoreString(finalLore, true));
                item.setItemMeta(meta);
            }
            return finalLore;
        }
        return "?";
    }

    public abstract void onShoot(DataShoot dataShoot);
}
