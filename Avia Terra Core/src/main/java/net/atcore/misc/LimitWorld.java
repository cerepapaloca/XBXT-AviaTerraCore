package net.atcore.misc;

import net.atcore.messages.MessagesManager;
import net.atcore.messages.TypeMessages;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

@Deprecated
public class LimitWorld {

    public static void checkLimit(Player player) {
        checkX(player, 100);
        checkZ(player, 100);
        checkX(player, -100);
        checkZ(player, -100);
    }

    private static void checkX(Player player, int limit) {
        if (player.isOp()) return;
        boolean b;
        if (limit > 0){
            b = player.getLocation().getX() > limit;
        }else {
            b = player.getLocation().getX() < limit;
        }
        if (b) {
            Location loc = player.getLocation().clone();

            if (player.getLocation().getX() < 0) {
                loc.add(5, 0, 0);
            }else {
                loc.add(-5, 0, 0);
            }
            correctionY(player, loc);
        }
    }

    private static void checkZ(Player player, int limit) {
        if (player.isOp()) return;
        boolean b;
        if (limit > 0) {
            b = player.getLocation().getZ() > limit;
        }else {
            b = player.getLocation().getZ() < limit;
        }

        if (b){
            Location loc = player.getLocation().clone();

            if (player.getLocation().getZ() < 0) {
                loc.add(0, 0, 5);
            }else {
                loc.add(0, 0, -5);
            }
            correctionY(player, loc);
        }
    }

    private static void correctionY(Player player, Location loc) {
        int i = 0;
        while (!loc.getBlock().getType().equals(Material.AIR) || i > 100){
            loc.setY(loc.getBlockY() + 1);
            i++;
        }

        feedback(player);
        player.teleportAsync(loc, PlayerTeleportEvent.TeleportCause.PLUGIN);
    }

    private static void feedback(Player player) {
        MessagesManager.sendTitle(player, "", "⚠ Fin Del Mundo ⚠", 0, 4, 30, TypeMessages.ERROR);
        player.playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.3F, 0.95F);
    }

}
