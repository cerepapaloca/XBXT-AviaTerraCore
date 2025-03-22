package net.atcore.armament.magazines;

import net.atcore.armament.ammo.OtanMediumNormal;

import java.util.List;

public final class OtanMedium15 extends OtamMediumFamily {

    public OtanMedium15() {
        super(List.of(OtanMediumNormal.class),
                30,
                "15-PMAG",
                40
        );
    }
}
