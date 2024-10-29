package net.atcore.guns.ammo;

import net.atcore.guns.BaseAmmo;
import net.atcore.guns.DataShoot;
import net.atcore.guns.ListAmmo;
import org.bukkit.Color;

public class MM45_OTAN extends BaseAmmo {

    public MM45_OTAN() {
        super(ListAmmo.MM45_OTAN,
                5,
                "prueba",
                Color.fromRGB(20,20,20),
                true,
                5
        );
    }

    @Override
    public void onShoot(DataShoot dataShoot) {

    }
}
