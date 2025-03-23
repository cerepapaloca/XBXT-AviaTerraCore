package net.atcore.armament.ammo;

import net.atcore.armament.BaseAmmo;

public final class Shot extends BaseAmmo implements net.atcore.armament.Shot {
    public Shot() {
        super(12,
                "Cartucho 12C",
                2
        );
    }

    @Override
    public int getAmount() {
        return 8;
    }
}
