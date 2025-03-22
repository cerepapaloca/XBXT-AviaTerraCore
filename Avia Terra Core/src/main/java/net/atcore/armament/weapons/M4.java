package net.atcore.armament.weapons;

import net.atcore.armament.BaseWeaponTarkov;
import net.atcore.armament.Initializer;
import net.atcore.armament.WeaponMode;
import net.atcore.armament.magazines.OtanMedium30;
import net.atcore.armament.magazines.OtanMedium60;

import java.util.List;

@Initializer
public final class M4 extends OtanWeapon {

    public M4() {
        super(50,
                "M4",
                2,
                WeaponMode.AUTOMATIC,
                2
        );
    }
}
