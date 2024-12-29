package net.atcore.armament.magazines;

import net.atcore.armament.*;
import net.atcore.armament.ammo.MM45Otan;
import net.atcore.armament.ammo.MM45OtanVerde;

import java.util.List;

public class M4Family extends BaseMagazine {
    public M4Family(List<Class<? extends BaseAmmo>> defaultCaliber, int ammoMax, String displayName, int reloadTime) {
        super(List.of(MM45Otan.class, MM45OtanVerde.class),
                defaultCaliber,
                ammoMax,
                displayName,
                reloadTime
        );
    }

}
