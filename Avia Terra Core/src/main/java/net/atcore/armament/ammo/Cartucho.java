package net.atcore.armament.ammo;

import net.atcore.armament.BaseAmmo;
import org.bukkit.Color;

public final class Cartucho extends BaseAmmo {
    public Cartucho() {
        super(2,
                "Cartucho de escopeta",
                Color.fromRGB(180,20,20),
                true,
                1,
                2,
                6
        );
    }
}
