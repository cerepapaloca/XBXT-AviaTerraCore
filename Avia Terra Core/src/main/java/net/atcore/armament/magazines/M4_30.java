package net.atcore.armament.magazines;

import net.atcore.armament.Initializer;
import net.atcore.armament.ammo.MM45Otan;
import net.atcore.armament.ammo.MM45OtanVerde;

import java.util.List;

@Initializer
public final class M4_30 extends M4Family {

    public M4_30() {
        super(List.of(MM45Otan.class, MM45Otan.class, MM45OtanVerde.class),
                30,
                "M4 30",
                40
        );
    }
}
