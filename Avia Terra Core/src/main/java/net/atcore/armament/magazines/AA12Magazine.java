package net.atcore.armament.magazines;

import net.atcore.armament.BaseMagazine;
import net.atcore.armament.ammo.Shot;

import java.util.List;

public class AA12Magazine extends BaseMagazine {
    public AA12Magazine() {
        super(List.of(Shot.class),
                List.of(Shot.class),
                45,
                "AA12-Cargador",
                40
        );
    }
}
