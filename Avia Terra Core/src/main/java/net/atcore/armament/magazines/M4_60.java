package net.atcore.armament.magazines;

import net.atcore.armament.Initializer;
import net.atcore.armament.ammo.MM45OtanVerde;

import java.util.List;

@Initializer
public final class M4_60 extends M4Family {

    public M4_60() {
        super(List.of(MM45OtanVerde.class),
                60,
                "M4 60E",
                60
        );
    }
}
