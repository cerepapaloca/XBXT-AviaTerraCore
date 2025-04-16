package net.atcore.security.check.checker;

import net.atcore.messages.Message;
import net.atcore.messages.MessagesManager;
import net.atcore.security.check.BaseChecker;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class RoofNether extends BaseChecker<PlayerMoveEvent> {


    @Override
    public void onCheck(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        World world = player.getWorld();
        if (world.getEnvironment() == World.Environment.NETHER){
            Location location = player.getLocation();
            if (location.getBlockY() > 127){
                Location loc = location.clone().add(0, -6, 0);
                int i = 0;
                while (loc.getBlock().isSolid() && i < 50){
                    loc.add(0, -2, 0);
                    i++;
                }

                player.teleportAsync(loc.add(0, -1,0), PlayerTeleportEvent.TeleportCause.PLUGIN);
                player.playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
                MessagesManager.sendTitle(player, "", Message.MISC_NETHER_ROOF.getMessage(player), 0, 80, 60, Message.MISC_NETHER_ROOF.getTypeMessages());
            }
        }
    }
}
