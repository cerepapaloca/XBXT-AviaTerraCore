package net.atcore.armament;

import lombok.Getter;
import net.atcore.armament.weapons.M16;

@Getter
public enum ListWeaponUltraKill {
    M16(new M16());

    ListWeaponUltraKill(BaseWeaponUltraKill weaponUltraKill){
        this.weapon = weaponUltraKill;
    }

    private final BaseWeaponUltraKill weapon;
}
