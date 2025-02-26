package net.atcore.armament.ammo;

import net.atcore.armament.BaseAmmo;
import net.atcore.armament.Initializer;
import net.atcore.armament.Trace;
import net.kyori.adventure.text.TextReplacementConfig;
import org.bukkit.Color;

@Initializer
public final class MM45OtanVerde extends BaseAmmo implements Trace {

    public MM45OtanVerde() {
        super(5,
                "trazado verde",
                2
        );
    }

    @Override
    public Color getColorTrace() {
        return Color.fromRGB(20,180,20);
    }

    @Override
    public float getDensityTrace() {
        return 1f;
    }
}
