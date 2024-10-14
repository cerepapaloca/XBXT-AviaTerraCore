package net.atcore.ListenerManager;

import net.atcore.Moderation.Ban.CheckBan;
import net.atcore.Security.AntiTwoPlayer;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;

import static net.atcore.Messages.MessagesManager.COLOR_ERROR;

public class JoinListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {

    }

    @EventHandler
    public void onLogin(PlayerLoginEvent event) {
        CheckBan.onLogin(event);
    }

    @EventHandler
    public void onPreLogin(PlayerLoginEvent event) {
        if (AntiTwoPlayer.checkTwoPlayer(event.getEventName())){
            event.setKickMessage(ChatColor.translateAlternateColorCodes('&',COLOR_ERROR +
                    "¡¡Ya Este Jugador Esta Jugando!!"));
            event.setResult(PlayerLoginEvent.Result.KICK_OTHER);
        }
    }
}
