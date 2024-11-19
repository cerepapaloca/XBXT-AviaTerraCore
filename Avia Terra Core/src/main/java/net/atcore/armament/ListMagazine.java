package net.atcore.armament;

import lombok.Getter;
import net.atcore.armament.magazines.M4_30;
import net.atcore.armament.magazines.M4_60;

@Getter
public enum ListMagazine {
    M4_30(new M4_30()),
    M4_60(new M4_60());

    ListMagazine(BaseMagazine baseMagazine) {
        baseMagazine.setName(name());
        this.magazine = baseMagazine;
    }

    private final BaseMagazine magazine;
}
