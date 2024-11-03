package net.atcore.armament;

import net.atcore.Section;
import net.atcore.armament.ammo.MM45_OTAN;
import net.atcore.armament.ammo.MM45_OTAN_VERDE;
import net.atcore.armament.ammo.MMxx_Prueba;
import net.atcore.armament.chargers.M4_30;
import net.atcore.armament.chargers.M4_60;
import net.atcore.armament.weapons.M16;
import net.atcore.armament.weapons.M4;

import java.util.*;

import static net.atcore.utils.RegisterManager.register;

public class ArmamentSection implements Section {

    @Override
    public void enable() {
        //primero la munición luego los cargadores y por último las armas
        register(new MM45_OTAN());
        register(new MM45_OTAN_VERDE());
        register(new MMxx_Prueba());
        /////////////////
        register(new M4_30());
        register(new M4_60());
        /////////////////
        register(new M4());
        /////////////////
        /////////////////
        register(new M16());
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


}
