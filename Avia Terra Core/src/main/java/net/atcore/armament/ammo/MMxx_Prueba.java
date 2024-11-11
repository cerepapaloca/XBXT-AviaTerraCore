package net.atcore.armament.ammo;

import net.atcore.armament.BaseAmmo;
import net.atcore.armament.DataShoot;
import org.bukkit.Color;

public class MMxx_Prueba extends BaseAmmo {

    public MMxx_Prueba() {
        super(10,
                "prueba",
                Color.fromRGB(255,255,255),
                false,
                2,
                10
        );
    }

    @Override
    public void onShoot(DataShoot dataShoot) {

    }
}
