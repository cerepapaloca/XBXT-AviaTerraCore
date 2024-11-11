package net.atcore.armament.chargers;

import net.atcore.armament.*;

import java.util.List;

public class M4Family extends BaseCharger {
    public M4Family(List<ListAmmo> defaultCaliber, int ammoMax, String displayName, int reloadTime) {
        super(List.of(ListAmmo.MM45_OTAN, ListAmmo.MM45_OTAN_VERDE),
                defaultCaliber,
                ammoMax,
                displayName,
                reloadTime
        );
    }

    @Override
    public void onShoot(DataShoot dataShoot) {

    }
}
