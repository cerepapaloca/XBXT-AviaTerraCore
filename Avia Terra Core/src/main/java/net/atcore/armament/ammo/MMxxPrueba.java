package net.atcore.armament.ammo;

import net.atcore.armament.BaseAmmo;
import net.atcore.armament.Initializer;
import net.atcore.armament.Shot;
import org.bukkit.Color;

@Initializer
public final class MMxxPrueba extends BaseAmmo {

    public MMxxPrueba() {
        super(10,
                "prueba",
                Color.fromRGB(255,255,255),
                false,
                2,
                10
        );
    }
}
