package net.atcore.guns.ammo;

import net.atcore.guns.BaseAmmo;
import net.atcore.guns.DataShoot;
import net.atcore.guns.ListAmmo;
import org.bukkit.Color;

public class MM45_OTAN_VERDE extends BaseAmmo {

    public MM45_OTAN_VERDE() {
        super(ListAmmo.MM45_OTAN_VERDE,
                5,
                "trazado verde",
                Color.fromRGB(20,180,20),
                true,
                1
        );
    }

    @Override
    public void onShoot(DataShoot dataShoot) {

    }
}
