package net.atcore.armament;

import lombok.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

@Getter
@Setter
@RequiredArgsConstructor
public class DataShoot {

    private final LivingEntity victim;
    private final Player shooter;
    private final BaseWeapon weapon;
    private final BaseCharger charger;
    private final BaseAmmo ammo;
    private final double distance;
    private double damage;
    private boolean cancelled = false;

}
