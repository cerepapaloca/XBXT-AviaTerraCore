package net.atcore.moderation;

import net.atcore.aviaterraplayer.AviaTerraPlayer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Freeze {

    public static boolean isFreeze(@NotNull Player player) {
        if(AviaTerraPlayer.getPlayer(player).getModerationPlayer().isFreeze()){
            player.sendTitle(ChatColor.translateAlternateColorCodes('&',"&c¡¡ESTAS CONGELADO!!"), "", 5, 80, 40);
            return true;
        }
        return false;
    }
}
