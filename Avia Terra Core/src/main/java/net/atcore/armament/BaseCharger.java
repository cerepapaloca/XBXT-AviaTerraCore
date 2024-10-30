package net.atcore.armament;

import lombok.Getter;
import lombok.Setter;
import net.atcore.utils.GlobalUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

@Getter
@Setter
public abstract class BaseCharger extends BaseArmament{

    public BaseCharger(ListCharger type, List<ListAmmo> compatibleCaliber, ListAmmo defaultCaliber, int ammoMax, String displayName, int reloadTime){
        this(type, compatibleCaliber, Collections.nCopies(ammoMax, defaultCaliber), ammoMax, displayName, reloadTime);

    }

    public BaseCharger(ListCharger type, List<ListAmmo> compatibleCaliber, List<ListAmmo> defaultCaliber, int ammoMax, String displayName, int reloadTime) {
        super(displayName, new ItemStack(Material.SUGAR));
        List<String> listAmmoName = new ArrayList<>();
        for (BaseAmmo ammo : listAmmoFill(defaultCaliber))listAmmoName.add(ammo.getList().name());
        this.displayName = displayName;
        this.chargerType = type;
        this.DefaultammonList = listAmmoFill(defaultCaliber);
        this.compatibleAmmonList = listAmmoToBaseAmmo(compatibleCaliber);
        this.ammoMax = ammoMax;
        this.reloadTime = reloadTime;
        ItemMeta itemMeta = itemArmament.getItemMeta();
        assert itemMeta != null;
        itemMeta.setDisplayName(displayName);
        itemArmament.setItemMeta(itemMeta);
        GlobalUtils.setPersistentDataItem(itemArmament, "chargerAmmo", PersistentDataType.STRING, GunsSection.listToString(listAmmoName));
        GlobalUtils.setPersistentDataItem(itemArmament, "chargerType", PersistentDataType.STRING, type.name());
        getProperties(itemArmament, true);
    }

    private final String displayName;
    private final ListCharger chargerType;
    private final List<BaseAmmo> DefaultammonList;
    private final List<BaseAmmo> compatibleAmmonList;
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
                            ammo.getDisplayName(),
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
    private List<BaseAmmo> listAmmoToBaseAmmo(List<ListAmmo> baseAmmoList){
        List<BaseAmmo> listAmmo = new ArrayList<>();
        for (ListAmmo ammo : baseAmmoList) {
            listAmmo.add(GunsSection.baseAmmo.get(ammo));
        }

        return listAmmo;
    }
    
    private List<BaseAmmo> listAmmoFill(List<ListAmmo> ammoList){
        List<ListAmmo> listAmmonFill = new ArrayList<>();
        for (int i = 0; i < ammoMax; i++) {
            listAmmonFill.add(ammoList.get(i % ammoList.size()));
        }
        return listAmmoToBaseAmmo(listAmmonFill);
    }

    public abstract void onShoot(DataShoot dataShoot);
}
