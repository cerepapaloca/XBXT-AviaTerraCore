package net.atcore.armament.weapons;

import net.atcore.armament.BaseWeaponTarkov;
import net.atcore.armament.ListMagazine;
import net.atcore.armament.DataShoot;
import net.atcore.armament.WeaponMode;
import org.bukkit.entity.Player;

import java.util.List;

public class M4 extends BaseWeaponTarkov {


    public M4() {
        super(List.of(ListMagazine.M4_30, ListMagazine.M4_60),
                50,
                "m4",
                1,
                WeaponMode.AUTOMATIC,
                1
        );
    }
}
