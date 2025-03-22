package net.atcore.armament.magazines;

import net.atcore.armament.ammo.UURSMediumNormal;

import java.util.List;

public final class UURSMedium15 extends UURSMediumFamily {

    public UURSMedium15() {
        super(List.of(UURSMediumNormal.class),
                15,
                "15-Midcap",
                40
        );
    }
}
