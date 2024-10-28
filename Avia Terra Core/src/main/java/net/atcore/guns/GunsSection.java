package net.atcore.guns;

import net.atcore.Section;
import net.atcore.guns.chargers.M4_30;
import net.atcore.guns.weapons.M4;

import java.util.HashMap;

import static net.atcore.utils.RegisterManager.register;

public class GunsSection implements Section {

    public static final HashMap<ChargerList, BaseCharger> dataChargers = new HashMap<>();
    public static final HashMap<WeaponList, BaseWeapon> baseWeapons = new HashMap<>();

    @Override
    public void enable() {
        register(new M4_30());
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
        return "";
    }
}
