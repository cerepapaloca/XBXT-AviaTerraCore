package net.atcore.armament.ammo;

import net.atcore.armament.BaseAmmo;
import net.atcore.armament.Initializer;

@Initializer
public final class OtanMediumNormal extends BaseAmmo {

    public OtanMediumNormal() {
        super(10,
                "5,56×45mm",
                10
        );
    }
}
