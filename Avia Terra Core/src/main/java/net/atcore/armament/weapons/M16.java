package net.atcore.armament.weapons;

import net.atcore.armament.WeaponMode;

public final class M16 extends OtanWeapon {

    public M16() {
        super(150,
                "M16",
                4,
                WeaponMode.AUTOMATIC,
                2
        );
    }
}
