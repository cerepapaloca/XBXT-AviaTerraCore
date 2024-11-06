package net.atcore.armament;

import lombok.Getter;
import net.atcore.armament.ammo.MM45_OTAN;
import net.atcore.armament.ammo.MM45_OTAN_VERDE;
import net.atcore.armament.ammo.MMxx_Prueba;

@Getter
public enum ListAmmo {
    MM45_OTAN(new MM45_OTAN()),
    MM45_OTAN_VERDE(new MM45_OTAN_VERDE()),
    MMxx_Prueba(new MMxx_Prueba());

    ListAmmo(BaseAmmo ammo) {
        this.ammo = ammo;
    }

    private final BaseAmmo ammo;
}