package net.atcore.guns.chargers;

import net.atcore.guns.ListAmmo;
import net.atcore.guns.ListCharger;
import net.atcore.guns.BaseCharger;
import net.atcore.guns.DataShoot;

import java.util.List;

public class M4_30 extends BaseCharger {

    public M4_30() {
        super(ListCharger.M4_30,
                List.of(ListAmmo.MM45_OTAN, ListAmmo.MM45_OTAN, ListAmmo.MM45_OTAN_VERDE),
                30,
                "M4 30",
                40);
    }

    @Override
    public void onShoot(DataShoot dataShoot) {

    }
}
