package net.atcore.armament.ammo;

import net.atcore.armament.BaseAmmo;
import net.atcore.armament.Initializer;
import net.atcore.armament.Trace;
import org.bukkit.Color;

@Initializer
public final class MM45Otan extends BaseAmmo implements Trace {

    public MM45Otan() {
        super(5,
                "prueba",
                10
        );
    }

    @Override
    public Color getColorTrace() {
        return Color.fromRGB(20,20,20);
    }

    @Override
    public float getDensityTrace() {
        return 0.1f;
    }
}
