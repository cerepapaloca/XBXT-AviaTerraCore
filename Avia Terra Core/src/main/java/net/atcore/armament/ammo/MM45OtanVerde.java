package net.atcore.armament.ammo;

import net.atcore.armament.BaseAmmo;
import net.atcore.armament.Initializer;
import org.bukkit.Color;

@Initializer
public final class MM45OtanVerde extends BaseAmmo {

    public MM45OtanVerde() {
        super(5,
                "trazado verde",
                Color.fromRGB(20,180,20),
                true,
                1,
                2,
                1
        );
    }
}
