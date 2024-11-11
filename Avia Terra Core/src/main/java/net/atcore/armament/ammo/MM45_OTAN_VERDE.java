package net.atcore.armament.ammo;

import net.atcore.armament.BaseAmmo;
import net.atcore.armament.DataShoot;
import net.atcore.armament.ListAmmo;
import org.bukkit.Color;

public class MM45_OTAN_VERDE extends BaseAmmo {

    public MM45_OTAN_VERDE() {
        super(5,
                "trazado verde",
                Color.fromRGB(20,180,20),
                true,
                1,
                2
        );
    }

    @Override
    public void onShoot(DataShoot dataShoot) {

    }
}
