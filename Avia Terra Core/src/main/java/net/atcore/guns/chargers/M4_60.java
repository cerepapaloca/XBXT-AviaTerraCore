package net.atcore.guns.chargers;

import net.atcore.guns.AmmoCaliber;
import net.atcore.guns.ChargerList;
import net.atcore.guns.BaseCharger;
import net.atcore.guns.DataShoot;
import org.bukkit.Color;

public class M4_60 extends BaseCharger {

    public M4_60() {
        super(ChargerList.M4_60,
                AmmoCaliber.MM45_OTAN,
                60,
                3,
                "xxx",
                "M4 60"
        );
        color = Color.fromRGB(255,180, 10);
    }

    @Override
    public void onShoot(DataShoot dataShoot) {

    }
}
