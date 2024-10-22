package net.atcore.Moderation.Ban;

import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.jetbrains.annotations.NotNull;

public class CheckBan {


    public static void onLogin(@NotNull PlayerLoginEvent event) {//No sé por qué cuando es la primeras vez que entras al servidor se dispara 4 veces seguidas
        Player player = event.getPlayer();
        String s = ManagerBan.checkBan(player, event.getAddress(), ContextBan.GLOBAL);
        if (s != null && !s.isEmpty()) {
            event.setKickMessage(s);
            event.setResult(PlayerLoginEvent.Result.KICK_BANNED);
        }
    }

    public static boolean checkChat(@NotNull AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String s = ManagerBan.checkBan(player, ContextBan.CHAT);
        return s != null && !s.isEmpty();
    }
}
