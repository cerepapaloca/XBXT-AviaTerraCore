package net.atcore.armament.weapons;

import net.atcore.armament.BaseWeaponUltraKill;
import net.atcore.armament.WeaponMode;
import net.atcore.armament.ammo.Shot;

public final class Test2 extends BaseWeaponUltraKill {

    public Test2() {
        super("Test 2",
                50,
                2,
                50,
                15,
                Shot.class,
                WeaponMode.SEMI,
                50
        );
    }
}
