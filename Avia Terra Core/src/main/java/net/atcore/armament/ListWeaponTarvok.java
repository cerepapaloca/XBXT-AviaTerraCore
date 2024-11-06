package net.atcore.armament;

import lombok.Getter;
import net.atcore.armament.weapons.M4;

@Getter
public enum ListWeaponTarvok {
    M4(new M4());

    ListWeaponTarvok(BaseWeaponTarkov baseWeaponTarkov){
        this.weapon = baseWeaponTarkov;
    }

    private final BaseWeaponTarkov weapon;
}
