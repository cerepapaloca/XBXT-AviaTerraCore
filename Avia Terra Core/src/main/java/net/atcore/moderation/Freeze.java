package net.atcore.moderation;

import lombok.Getter;
import net.atcore.AviaTerraCore;
import net.atcore.AviaTerraPlayer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.UUID;
public class Freeze {

    public static boolean isFreeze(@NotNull Player player) {
        if(AviaTerraCore.getPlayer(player).isFreeze()){
            player.sendTitle(ChatColor.translateAlternateColorCodes('&',"&c¡¡ESTAS CONGELADO!!"), "", 5, 80, 40);
            return true;
        }
        return false;
    }
}
