package net.atcore.armament.weapons;

import net.atcore.armament.BaseWeaponTarkov;
import net.atcore.armament.ListCharger;
import net.atcore.armament.DataShoot;
import net.atcore.armament.ListWeaponTarvok;
import org.bukkit.entity.Player;

import java.util.List;

public class M4 extends BaseWeaponTarkov {


    public M4() {
        super(ListWeaponTarvok.M4,
                List.of(ListCharger.M4_30, ListCharger.M4_60),
                50,
                "m4",
                0
        );
    }

    @Override
    public void onShoot(DataShoot dataShoot) {

    }

    @Override
    public void onReloading(Player player) {

    }
}
