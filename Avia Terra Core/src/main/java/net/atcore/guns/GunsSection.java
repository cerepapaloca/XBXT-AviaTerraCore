package net.atcore.guns;

import net.atcore.Section;

import java.util.HashMap;

public class GunsSection implements Section {

    public static final HashMap<ChargerList, DataCharger> dataChargers = new HashMap<>();
    public static final HashMap<WeaponList, BaseWeapon> baseWeapons = new HashMap<>();

    @Override
    public void enable() {
        dataChargers.put(ChargerList.M4_30, new DataCharger(ChargerList.M4_30, AmmoCaliber.MM45_OTAN, 30, 3, "5.21cd"));
        ////////////////////////////////
        baseWeapons.put(WeaponList.M4, new BaseWeapon(WeaponList.M4, ChargerList.M4_30, 50, "La M4"));
    }

    @Override
    public void disable() {

    }

    @Override
    public void reloadConfig() {

    }

    @Override
    public String getName() {
        return "";
    }
}
