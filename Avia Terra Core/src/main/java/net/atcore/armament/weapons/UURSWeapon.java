package net.atcore.armament.weapons;

import net.atcore.armament.BaseWeaponTarkov;
import net.atcore.armament.WeaponMode;
import net.atcore.armament.magazines.*;

import java.util.List;

public abstract class UURSWeapon extends BaseWeaponTarkov {
    protected UURSWeapon(int maxDistance, String displayName, double vague, WeaponMode mode, int cadence) {
        super(List.of(UURSMedium60.class, UURSMedium30.class, UURSMedium15.class), maxDistance, displayName, vague, mode, cadence);
    }
}
