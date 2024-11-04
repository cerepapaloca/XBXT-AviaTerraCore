package net.atcore.armament.weapons;

import net.atcore.armament.BaseWeaponUltraKill;
import net.atcore.armament.DataShoot;
import net.atcore.armament.ListAmmo;
import net.atcore.armament.ListWeaponUltraKill;
import org.bukkit.Color;

public class M16 extends BaseWeaponUltraKill {
    public M16() {
        super(ListWeaponUltraKill.M16,
                "M16",
                60,
                10,
                0,
                ListAmmo.MM45_OTAN_VERDE
        );
    }

    @Override
    public void onShoot(DataShoot dataShoot) {

    }
}
