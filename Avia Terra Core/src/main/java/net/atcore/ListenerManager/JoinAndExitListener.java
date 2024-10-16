package net.atcore.ListenerManager;

import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedSignedProperty;
import com.github.games647.craftapi.model.skin.Textures;
import net.atcore.Moderation.Ban.CheckBan;
import net.atcore.Security.AntiTwoPlayer;
import net.atcore.Service.SimulateOnlineMode;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import static net.atcore.Messages.MessagesManager.*;

public class JoinAndExitListener implements Listener {

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        event.setQuitMessage(ChatColor.translateAlternateColorCodes('&',
                "&8[&4-&8] " + COLOR_ESPECIAL + event.getPlayer().getDisplayName() + COLOR_INFO + " se a ido."));
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        event.setJoinMessage(ChatColor.translateAlternateColorCodes('&',
                "&8[&a+&8] " + COLOR_ESPECIAL + event.getPlayer().getDisplayName() + COLOR_INFO + " se a unido."));
    }

    @EventHandler
    public void onLogin(PlayerLoginEvent event) {
        CheckBan.onLogin(event);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPreLogin(AsyncPlayerPreLoginEvent event) {
        if (AntiTwoPlayer.checkTwoPlayer(event.getName())){
            event.setKickMessage(ChatColor.translateAlternateColorCodes('&',COLOR_ERROR +
                    "¡¡Ya Este Jugador Esta Jugando!!"));
            event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_BANNED);
        }
        SimulateOnlineMode.getIps().put(event.getName(), event.getAddress());
    }

    private void applySkin(Player player, String skinData, String signature) {
        WrappedGameProfile gameProfile = WrappedGameProfile.fromPlayer(player);

        WrappedSignedProperty skin = WrappedSignedProperty.fromValues(Textures.KEY, skinData, signature);
        gameProfile.getProperties().put(Textures.KEY, skin);
    }
}
