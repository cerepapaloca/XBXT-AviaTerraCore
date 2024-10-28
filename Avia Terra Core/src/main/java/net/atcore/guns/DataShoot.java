package net.atcore.guns;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

@Getter
@AllArgsConstructor
public class DataShoot {

    private final LivingEntity victim;
    private final Player shooter;
    private final BaseWeapon weapon;
    private final BaseCharger charger;
    private final double Distance;

}
