package net.atcore.armament.magazines;

import net.atcore.armament.BaseAmmo;
import net.atcore.armament.BaseMagazine;
import net.atcore.armament.ammo.*;

import java.util.List;

public abstract class UURSMediumFamily extends BaseMagazine {
    public UURSMediumFamily(List<Class<? extends BaseAmmo>> defaultCaliber, int ammoMax, String displayName, int reloadTime) {
        super(List.of(UURSMediumTrace.class, UURSMediumNormal.class, UURSMediumPenetration.class),
                defaultCaliber,
                ammoMax,
                displayName,
                reloadTime
        );
    }

}
