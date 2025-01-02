package net.atcore.armament.weapons;

import net.atcore.armament.BaseWeaponUltraKill;
import net.atcore.armament.Initializer;
import net.atcore.armament.WeaponMode;
import net.atcore.armament.ammo.MM45Otan;

@Initializer
public final class M16 extends BaseWeaponUltraKill {

    public M16() {
        super("M16",
                60,
                2,
                30,
                10,
                MM45Otan.class,
                WeaponMode.SEMI,
                6
        );
    }

}
