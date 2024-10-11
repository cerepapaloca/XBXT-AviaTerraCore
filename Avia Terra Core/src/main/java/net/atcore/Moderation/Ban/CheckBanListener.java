package net.atcore.Moderation.Ban;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.jetbrains.annotations.NotNull;

public class CheckBanListener implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void onLogin(@NotNull PlayerLoginEvent event) {//No sé por qué cuando es la primeras vez que entras al servidor se dispara 4 veces seguidas
        Player player = event.getPlayer();
        String s = BanManager.checkBan(player, event.getAddress(), ContextBan.GLOBAL);
        if (s != null && !s.isEmpty()) {
            event.setKickMessage(s);
            event.setResult(PlayerLoginEvent.Result.KICK_OTHER);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onChat(@NotNull AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String s = BanManager.checkBan(player, ContextBan.CHAT);
        if (s != null && !s.isEmpty()) {
            event.setCancelled(true);
            event.setMessage(s);
        }
    }
}
