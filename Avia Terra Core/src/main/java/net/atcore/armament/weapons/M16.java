package net.atcore.armament.weapons;

import net.atcore.armament.BaseWeaponUltraKill;
import net.atcore.armament.ListAmmo;
import net.atcore.armament.WeaponMode;

public class M16 extends BaseWeaponUltraKill {
    public M16() {
        super("M16",
                60,
                2,
                30,
                10,
                ListAmmo.MM45_OTAN_VERDE,
                WeaponMode.AUTOMATIC,
                6
        );
    }

}
