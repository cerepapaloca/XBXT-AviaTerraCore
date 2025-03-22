package net.atcore.armament.weapons;

import net.atcore.armament.BaseWeaponTarkov;
import net.atcore.armament.WeaponMode;
import net.atcore.armament.ammo.Shot;
import net.atcore.armament.magazines.AA12Magazine;

import java.util.List;

public final class AA12 extends BaseWeaponTarkov {
    public AA12() {
        super(List.of(AA12Magazine.class),
                30,
                "AA12",
                1,
                WeaponMode.SEMI,
                2
        );
    }
}
