package net.atcore.armament;

import lombok.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

/**
 * Son datos que obtiene al momento de realizar un disparo
 * y se puede modificar y se dispara en {@link BaseArmament#onShoot(java.util.List) onShoot}
 */

@Getter
@Setter
@RequiredArgsConstructor
public class ShootData {

    private final LivingEntity victim;
    private final Player shooter;
    private final BaseWeapon weapon;
    private final BaseMagazine charger;
    private final BaseAmmo ammo;
    private final double distance;
    private double damage;
    private boolean cancelled = false;
    private float hardnessPenetration;

}
