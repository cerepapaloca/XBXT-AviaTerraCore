package net.atcore.armament.weapons;

import net.atcore.armament.BaseWeapon;
import net.atcore.armament.ListCharger;
import net.atcore.armament.DataShoot;
import net.atcore.armament.ListWeapon;
import org.bukkit.entity.Player;

import java.util.List;

public class M4 extends BaseWeapon {


    public M4() {
        super(ListWeapon.M4,
                List.of(ListCharger.M4_30, ListCharger.M4_60),
                50,
                "m4");
    }

    @Override
    public void onShoot(DataShoot dataShoot) {

    }

    @Override
    public void onReloading(Player player) {

    }
}
