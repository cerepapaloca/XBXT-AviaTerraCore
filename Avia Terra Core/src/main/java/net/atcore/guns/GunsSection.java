package net.atcore.guns;

import net.atcore.Section;
import net.atcore.guns.ammo.MM45_OTAN;
import net.atcore.guns.ammo.MM45_OTAN_VERDE;
import net.atcore.guns.chargers.M4_30;
import net.atcore.guns.chargers.M4_60;
import net.atcore.guns.weapons.M4;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static net.atcore.utils.RegisterManager.register;

public class GunsSection implements Section {

    public static final HashMap<ListCharger, BaseCharger> dataChargers = new HashMap<>();
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
        return new ArrayList<>(Arrays.asList(list.split(",")));
    }
}
