package net.atcore.guns.weapons;

import net.atcore.guns.BaseWeapon;
import net.atcore.guns.ChargerList;
import net.atcore.guns.DataShoot;
import net.atcore.guns.WeaponList;
import org.bukkit.entity.Player;

import java.util.List;

public class M4 extends BaseWeapon {


    public M4() {
        super(WeaponList.M4,
                List.of(ChargerList.M4_30, ChargerList.M4_60),
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
