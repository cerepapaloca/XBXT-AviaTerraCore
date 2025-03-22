package net.atcore.armament.magazines;

import net.atcore.armament.Initializer;
import net.atcore.armament.ammo.OtanMediumTrace;

import java.util.List;

@Initializer
public final class OtanMedium60 extends OtamMediumFamily {

    public OtanMedium60() {
        super(List.of(OtanMediumTrace.class),
                60,
                "60-PMAG",
                60
        );
    }
}
