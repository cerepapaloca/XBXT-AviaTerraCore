package net.atcore.armament.weapons;

import net.atcore.armament.BaseWeaponUltraKill;
import net.atcore.armament.Initializer;
import net.atcore.armament.WeaponMode;
import net.atcore.armament.ammo.MM45Otan;

@Initializer
public final class Test1 extends BaseWeaponUltraKill {

    public Test1() {
        super("test 1",
                50,
                1,
                120,
                10,
                MM45Otan.class,
                WeaponMode.AUTOMATIC,
                2);
    }
}
