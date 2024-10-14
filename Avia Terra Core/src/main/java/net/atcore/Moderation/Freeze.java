package net.atcore.Moderation;

import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.UUID;
@Getter
public class Freeze {

    private static final ArrayList<UUID> playerFreeze = new ArrayList<>();

    public static boolean isFreeze(@NotNull Player player) {
        if(playerFreeze.contains(player.getUniqueId())){
            player.sendTitle(ChatColor.translateAlternateColorCodes('&',"&c¡¡ESTAS CONGELADO!!"), "", 5, 80, 40);
            return true;
        }
        return false;
    }
}
