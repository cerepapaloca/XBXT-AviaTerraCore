package net.atcore.armament.ammo;

import net.atcore.armament.BaseAmmo;
import net.atcore.armament.ShootData;
import org.bukkit.Color;

import java.util.List;

public final class UURSMediumTrace extends BaseAmmo implements TraceIsFire {

    public UURSMediumTrace() {
        super(12,
                "7,62x39mm T",
                20
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
