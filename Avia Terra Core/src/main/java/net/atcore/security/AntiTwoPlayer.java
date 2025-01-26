package net.atcore.security;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class AntiTwoPlayer {

    public static boolean checkTwoPlayer(String name) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }
}
