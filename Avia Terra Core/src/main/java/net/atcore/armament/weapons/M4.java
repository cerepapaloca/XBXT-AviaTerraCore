package net.atcore.armament.weapons;

import net.atcore.armament.BaseWeaponTarkov;
import net.atcore.armament.Initializer;
import net.atcore.armament.WeaponMode;
import net.atcore.armament.magazines.M4_30;
import net.atcore.armament.magazines.M4_60;

import java.util.List;

@Initializer
public final class M4 extends BaseWeaponTarkov {

    public M4() {
        super(List.of(M4_30.class, M4_60.class),
                50,
                "m4",
                1,
                WeaponMode.AUTOMATIC,
                1
        );
    }
}
