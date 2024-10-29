package net.atcore.guns.weapons;

import net.atcore.guns.BaseWeapon;
import net.atcore.guns.ListCharger;
import net.atcore.guns.DataShoot;
import net.atcore.guns.ListWeapon;
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
