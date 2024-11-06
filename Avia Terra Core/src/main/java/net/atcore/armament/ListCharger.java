package net.atcore.armament;

import lombok.Getter;
import net.atcore.armament.chargers.M4_30;
import net.atcore.armament.chargers.M4_60;

@Getter
public enum ListCharger {
    M4_30(new M4_30()),
    M4_60(new M4_60());

    ListCharger(BaseCharger baseCharger) {
        this.charger = baseCharger;
    }

    private final BaseCharger charger;
}
