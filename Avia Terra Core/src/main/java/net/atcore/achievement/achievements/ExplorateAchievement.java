package net.atcore.achievement.achievements;

import net.atcore.achievement.BaseAchievementSimple;
import net.minecraft.advancements.AdvancementType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

public class ExplorateAchievement extends BaseAchievementSimple<PlayerMoveEvent> {
    public ExplorateAchievement() {
        super(Material.IRON_BOOTS, "explorate", AdvancementType.TASK);
    }

    @Override
    public void onEvent(PlayerMoveEvent event) {
        if (isNearCardinalAndDiagonal(event.getTo())){
            grantAdvanced(event.getPlayer(), null);
        }
    }

    @Override
    public void rewards(Player player) {

    }

    @Override
    protected int getY(String path) {
        return 0;
    }

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
