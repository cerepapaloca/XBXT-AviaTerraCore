package net.atcore.Moderation;

import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.UUID;
public class Freeze {

    @Getter
    private static final HashSet<UUID> playerFreeze = new HashSet<>();

    public static boolean isFreeze(@NotNull Player player) {
        if(playerFreeze.contains(player.getUniqueId())){
            player.sendTitle(ChatColor.translateAlternateColorCodes('&',"&c¡¡ESTAS CONGELADO!!"), "", 5, 80, 40);
            return true;
        }
        return false;
    }
}
