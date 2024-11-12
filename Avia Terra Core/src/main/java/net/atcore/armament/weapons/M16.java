package net.atcore.armament.weapons;

import net.atcore.armament.BaseWeaponUltraKill;
import net.atcore.armament.DataShoot;
import net.atcore.armament.ListAmmo;

public class M16 extends BaseWeaponUltraKill {
    public M16() {
        super("M16",
                60,
                10,
                10,
                ListAmmo.MM45_OTAN_VERDE
        );
    }

    @Override
    public void onShoot(DataShoot dataShoot) {

    }
}
