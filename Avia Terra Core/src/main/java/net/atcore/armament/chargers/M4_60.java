package net.atcore.armament.chargers;

import net.atcore.armament.ListCharger;

import java.util.List;

import static net.atcore.armament.ListAmmo.*;

public class M4_60 extends M4Family {

    public M4_60(String name) {
        super(name,
                List.of(MM45_OTAN_VERDE),
                60,
                "M4 60E",
                60
        );
    }
}
