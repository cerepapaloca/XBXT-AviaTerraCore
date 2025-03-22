package net.atcore.armament.ammo;

import net.atcore.armament.BaseAmmo;
import net.atcore.armament.Initializer;
import net.atcore.armament.ShootData;
import net.atcore.armament.Trace;
import org.bukkit.Color;

import java.util.List;

@Initializer
public final class OtanMediumTrace extends BaseAmmo implements TraceIsFire {

    public OtanMediumTrace() {
        super(10,
                "5,56×45mm T",
                10
        );
    }

    @Override
    public Color getColorTrace() {
        return Color.fromRGB(255,128,0);
    }

    @Override
    public float getDensityTrace() {
        return 1f;
    }

    @Override
    public void onShoot(List<ShootData> shootData){
        fire(shootData);
    }
}
