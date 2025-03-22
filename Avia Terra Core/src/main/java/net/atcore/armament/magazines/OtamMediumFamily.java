package net.atcore.armament.magazines;

import net.atcore.armament.*;
import net.atcore.armament.ammo.OtanMediumNormal;
import net.atcore.armament.ammo.OtanMediumPenetration;
import net.atcore.armament.ammo.OtanMediumTrace;

import java.util.List;

public abstract class OtamMediumFamily extends BaseMagazine {
    public OtamMediumFamily(List<Class<? extends BaseAmmo>> defaultCaliber, int ammoMax, String displayName, int reloadTime) {
        super(List.of(OtanMediumTrace.class, OtanMediumNormal.class, OtanMediumPenetration.class),
                defaultCaliber,
                ammoMax,
                displayName,
                reloadTime
        );
    }

}
