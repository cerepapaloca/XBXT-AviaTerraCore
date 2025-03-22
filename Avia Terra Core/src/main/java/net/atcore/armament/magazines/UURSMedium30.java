package net.atcore.armament.magazines;

import net.atcore.armament.ammo.UURSMediumNormal;

import java.util.List;

public final class UURSMedium30 extends UURSMediumFamily {

    public UURSMedium30() {
        super(List.of(UURSMediumNormal.class),
                30,
                "30-Midcap",
                40
        );
    }
}
