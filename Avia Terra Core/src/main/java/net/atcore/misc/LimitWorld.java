package net.atcore.misc;

import net.atcore.messages.MessagesManager;
import net.atcore.messages.TypeMessages;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

public class LimitWorld {

    private static final int LIMIT_X = 100;

    public static void checkLimit(Player player) {
        if (player.getLocation().getX() > LIMIT_X) {
            Location loc = new Location(player.getWorld(), LIMIT_X - 5, player.getLocation().getY(), player.getLocation().getZ(), player.getLocation().getYaw(), player.getLocation().getPitch());
            int y = loc.getBlockY();

            if (player.getLocation().getX() < 0) {
                loc.add(5, 0, 0);
            }else {
                loc.add(-5, 0, 0);
            }
            int i = 0;
            while (!loc.getBlock().getType().equals(Material.AIR) || i > 100){
                loc.setY(loc.getBlockY() + 1);
                i++;
            }

            MessagesManager.sendTitle(player, "", "⚠ Fin Del Mundo ⚠", 0, 4, 30, TypeMessages.ERROR);
            player.playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.7F, 0.95F);
            player.teleport(loc, PlayerTeleportEvent.TeleportCause.PLUGIN);
        }
    }

}
