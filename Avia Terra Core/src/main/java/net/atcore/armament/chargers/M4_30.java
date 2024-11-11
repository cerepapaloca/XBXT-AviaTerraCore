package net.atcore.armament.chargers;

import java.util.List;

import static net.atcore.armament.ListAmmo.*;

public class M4_30 extends M4Family {

    public M4_30() {
        super(List.of(MM45_OTAN, MM45_OTAN, MM45_OTAN_VERDE),
                30,
                "M4 30",
                40
        );
    }
}
