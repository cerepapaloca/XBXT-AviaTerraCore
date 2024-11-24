package net.atcore.security;

import net.atcore.utils.GlobalUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class AntiTwoPlayer {

    public static boolean checkTwoPlayer(String name) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getName().equals(name)) {
                GlobalUtils.kickPlayer(p, "Hay un jugador con tu nombre");
                return true;
            }
        }
        return false;
    }
}
