package net.atcore.guns.chargers;

import net.atcore.guns.ListAmmo;
import net.atcore.guns.ListCharger;
import net.atcore.guns.BaseCharger;
import net.atcore.guns.DataShoot;

public class M4_60 extends BaseCharger {

    public M4_60() {
        super(ListCharger.M4_60,
                ListAmmo.MM45_OTAN_VERDE,
                60,
                "M4 60T"
        );
    }

    @Override
    public void onShoot(DataShoot dataShoot) {

    }
}
