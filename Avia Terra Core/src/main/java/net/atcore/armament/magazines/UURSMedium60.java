package net.atcore.armament.magazines;

import net.atcore.armament.ammo.UURSMediumNormal;

import java.util.List;

public final class UURSMedium60 extends UURSMediumFamily {

    public UURSMedium60() {
        super(List.of(UURSMediumNormal.class),
                60,
                "30-Midcap",
                40
        );
    }
}
