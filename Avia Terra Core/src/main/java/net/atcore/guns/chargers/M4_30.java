package net.atcore.guns.chargers;

import net.atcore.guns.AmmoCaliber;
import net.atcore.guns.ChargerList;
import net.atcore.guns.BaseCharger;
import net.atcore.guns.DataShoot;

public class M4_30 extends BaseCharger {

    public M4_30() {
        super(ChargerList.M4_30,
                AmmoCaliber.MM45_OTAN,
                30,
                3,
                "xxx",
                "M4 30"
        );
    }

    @Override
    public void onShoot(DataShoot dataShoot) {

    }
}
