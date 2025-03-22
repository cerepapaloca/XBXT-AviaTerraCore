package net.atcore.armament.ammo;

import net.atcore.armament.ShootData;
import net.atcore.armament.Trace;

import java.util.List;

public interface TraceIsFire extends Trace {

    default void fire(List<ShootData> shootData){
        ShootData sd = shootData.getFirst();
        if (Math.random() > 0.8){
            if (sd.getVictim() != null) sd.getVictim().setFireTicks(40);
        }
    }
}
