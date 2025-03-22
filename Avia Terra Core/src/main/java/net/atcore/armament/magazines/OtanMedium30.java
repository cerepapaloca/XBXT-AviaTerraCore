package net.atcore.armament.magazines;

import net.atcore.armament.Initializer;
import net.atcore.armament.ammo.OtanMediumNormal;

import java.util.List;

@Initializer
public final class OtanMedium30 extends OtamMediumFamily {

    public OtanMedium30() {
        super(List.of(OtanMediumNormal.class),
                30,
                "30-PMAG",
                40
        );
    }
}
