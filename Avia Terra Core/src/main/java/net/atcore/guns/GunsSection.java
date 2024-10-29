package net.atcore.guns;

import net.atcore.Section;
import net.atcore.guns.ammo.MM45_OTAN;
import net.atcore.guns.ammo.MM45_OTAN_VERDE;
import net.atcore.guns.chargers.M4_30;
import net.atcore.guns.chargers.M4_60;
import net.atcore.guns.weapons.M4;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static net.atcore.utils.RegisterManager.register;

public class GunsSection implements Section {

    public static final HashMap<ListCharger, BaseCharger> baseChargers = new HashMap<>();
    public static final HashMap<ListWeapon, BaseWeapon> baseWeapons = new HashMap<>();
    public static final HashMap<ListAmmo, BaseAmmo> baseAmmo = new HashMap<>();

    @Override
    public void enable() {
        //primero la munición luego los cargadores y por último las armas
        register(new MM45_OTAN());
        register(new MM45_OTAN_VERDE());
        /////////////////
        register(new M4_30());
        register(new M4_60());
        /////////////////
        register(new M4());
    }

    @Override
    public void disable() {

    }

    @Override
    public void reloadConfig() {

    }

    @Override
    public String getName() {
        return "Armas";
    }

    public static String listToString(List<String> list){
        return list.toString().replace(" ", "").replace("[", "").replace("]", "");
    }

    public static @NotNull List<String> stringToList(String list){
        List<String> finalList = new ArrayList<>(Arrays.asList(list.split(",")));
        if (finalList.getFirst().isBlank()){
            return new ArrayList<>();
        }else{
            return finalList;
        }
    }

    @Nullable
    public static BaseAmmo getAmmon(@Nullable String s){
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

    @Nullable
    public static BaseCharger getCharger(@Nullable String s){
        ListCharger list;
        try{
            list = ListCharger.valueOf(s);
        }catch (Exception e){
            return null;
        }
        return baseChargers.get(list);
    }

    @Nullable
    public static BaseWeapon getWeapon(@Nullable String s){
        ListWeapon list;
        try{
            list = ListWeapon.valueOf(s);
        }catch (Exception e){
            return null;
        }
        return baseWeapons.get(list);
    }
}
