package net.atcore.Security;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class AntiTwoPlayer {

    public static boolean checkTwoPlayer(String name) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }
}
