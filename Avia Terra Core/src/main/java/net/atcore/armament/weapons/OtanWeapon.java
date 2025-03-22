package net.atcore.armament.weapons;

import net.atcore.armament.BaseWeaponTarkov;
import net.atcore.armament.WeaponMode;
import net.atcore.armament.magazines.OtanMedium15;
import net.atcore.armament.magazines.OtanMedium30;
import net.atcore.armament.magazines.OtanMedium60;

import java.util.List;

public abstract class OtanWeapon extends BaseWeaponTarkov {
    protected OtanWeapon(int maxDistance, String displayName, double vague, WeaponMode mode, int cadence) {
        super(List.of(OtanMedium30.class, OtanMedium60.class, OtanMedium15.class), maxDistance, displayName, vague, mode, cadence);
    }
}
