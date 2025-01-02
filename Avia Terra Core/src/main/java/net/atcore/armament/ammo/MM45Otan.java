package net.atcore.armament.ammo;

import net.atcore.armament.BaseAmmo;
import net.atcore.armament.Initializer;
import org.bukkit.Color;

@Initializer
public final class MM45Otan extends BaseAmmo {

    public MM45Otan() {
        super(5,
                "prueba",
                Color.fromRGB(20,20,20),
                true,
                5,
                10,
                1
        );
    }
}
