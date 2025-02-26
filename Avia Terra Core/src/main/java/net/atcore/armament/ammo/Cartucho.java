package net.atcore.armament.ammo;

import net.atcore.armament.BaseAmmo;
import net.atcore.armament.Shot;
import org.bukkit.Color;

public final class Cartucho extends BaseAmmo implements Shot {
    public Cartucho() {
        super(2,
                "Cartucho de escopeta",
                2
        );
    }

    @Override
    public int getAmount() {
        return 8;
    }
}
