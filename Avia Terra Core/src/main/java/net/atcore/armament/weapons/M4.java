package net.atcore.armament.weapons;

import net.atcore.armament.BaseWeaponTarkov;
import net.atcore.armament.ListMagazine;
import net.atcore.armament.DataShoot;
import org.bukkit.entity.Player;

import java.util.List;

public class M4 extends BaseWeaponTarkov {


    public M4() {
        super(List.of(ListMagazine.M4_30, ListMagazine.M4_60),
                50,
                "m4",
                1
        );
    }

    @Override
    public void onShoot(DataShoot dataShoot) {

    }

    @Override
    public void onReloading(Player player) {

    }
}
