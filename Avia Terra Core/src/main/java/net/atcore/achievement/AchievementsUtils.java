package net.atcore.achievement;

import lombok.experimental.UtilityClass;
import org.bukkit.Location;

@UtilityClass
public class AchievementsUtils {

    public static boolean isNearCardinalAndDiagonal(Location location) {

        int px = location.getBlockX();
        int pz = location.getBlockZ();
        int tx = 0;
        int tz = 0;

        // Verificar si el jugador está exactamente a 6 bloques en x o z
        if (Math.abs(px - tx) < 6) return true; // En x+ o x-
        if (Math.abs(pz - tz) < 6) return true; // En z+ o z-

        // Verificar si el jugador está exactamente a 6 bloques en las diagonales
        return (Math.abs(Math.abs(px) - Math.abs(pz)) < 6); // Diagonal
    }
}
