package net.atcore.armament.weapons;

import net.atcore.armament.WeaponMode;

public final class AR15 extends OtanWeapon {
    public AR15() {
        super(200,
                "AR15",
                1,
                WeaponMode.SEMI,
                2
        );
    }
}
