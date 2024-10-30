package net.atcore.armament;

import lombok.experimental.UtilityClass;
import net.atcore.utils.GlobalUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
@UtilityClass
public class ArmamentUtils {

    public static final HashMap<ListCharger, BaseCharger> baseChargers = new HashMap<>();
    public static final HashMap<ListWeapon, BaseWeapon> baseWeapons = new HashMap<>();
    public static final HashMap<ListAmmo, BaseAmmo> baseAmmo = new HashMap<>();

    public String listToString(List<String> list){
        return list.toString().replace(" ", "").replace("[", "").replace("]", "");
    }

    public @NotNull List<String> stringToList(String list){
        List<String> finalList = new ArrayList<>(Arrays.asList(list.split(",")));
        if (finalList.getFirst().isBlank()){
            return new ArrayList<>();
        }else{
            return finalList;
        }
    }

    @Nullable
    public BaseCompartment getCompartment(ItemStack itemStack){
        BaseCompartment s = getWeapon(itemStack);//es muy feo pero funciona
        if (s != null){
            return s;
        }else {
            return getCharger(itemStack);
        }
    }

    @Nullable
    public BaseAmmo getAmmon(@Nullable String s){
        ListAmmo list;
        if (s == null){
            return null;
        }
        try{
            list = ListAmmo.valueOf(s);
        }catch (Exception e){
            return null;
        }
        return baseAmmo.get(list);
    }

    public BaseCharger getCharger(@NotNull Player player){
        return getCharger(player.getInventory().getItemInMainHand());
    }

    /**
     * Obtienes la clase {@link BaseCharger} si es un arma si no regresa
     * nulo en caso de que no
     */

    public BaseCharger getCharger(@NotNull ItemStack item) {
        if (item.getItemMeta() == null) return null;
        String chargeName = (String) GlobalUtils.getPersistenData(item, "chargerType", PersistentDataType.STRING);
        if (chargeName == null) return null;
        return getCharger(chargeName);
    }

    @Nullable
    public BaseCharger getCharger(@Nullable String s){
        ListCharger list;
        try{
            list = ListCharger.valueOf(s);
        }catch (Exception e){
            return null;
        }
        return baseChargers.get(list);
    }

    public BaseWeapon getWeapon(@NotNull Player player){
        return getWeapon(player.getInventory().getItemInMainHand());
    }

    /**
     * Obtienes la clase {@link BaseWeapon} si es un arma si no regresa
     * nulo en caso de que no
     */

    public BaseWeapon getWeapon(@NotNull ItemStack item) {
        if (item.getItemMeta() == null) return null;
        String weaponName = (String) GlobalUtils.getPersistenData(item, "weaponName", PersistentDataType.STRING);
        if (weaponName == null) return null;
        return getWeapon(weaponName);
    }

    @Nullable
    public BaseWeapon getWeapon(@Nullable String s){
        ListWeapon list;
        try{
            list = ListWeapon.valueOf(s);
        }catch (Exception e){
            return null;
        }
        return baseWeapons.get(list);
    }
}
