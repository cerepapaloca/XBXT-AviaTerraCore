package net.atcore.ListenerManager;

import net.atcore.Messages.TypeMessages;
import net.atcore.Moderation.Ban.CheckBan;
import net.atcore.Security.AntiTwoPlayer;
import net.atcore.Security.Login.LoginManager;
import net.atcore.Service.ServiceSection;
import net.atcore.Utils.GlobalUtils;
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
        Player player = event.getPlayer();

        if (LoginManager.getListRegister().get(player.getName()).getPasswordShaded() != null) {
            if (!LoginManager.isLoginIn(player, false)) {
                sendMessage(player, "login porfa", TypeMessages.INFO);
                LoginManager.startTimeOut(player, "Tardaste mucho en iniciar sesión");
            }
        }else{
            if (LoginManager.getListRegister().get(player.getName()).getUuidPremium() == null) {
                LoginManager.startTimeOut(player, "Tardaste mucho en registrarte");
                sendMessage(player, "registrate porfa ", TypeMessages.INFO);
            }
        }

        /*if (LoginManager.getListSession().get(player.getName()).getUuidPremium() != null) {
            player.sendTitle(ChatColor.translateAlternateColorCodes('&',COLOR_ESPECIAL + "Te haz logueado!"),
                    ChatColor.translateAlternateColorCodes('&',COLOR_ESPECIAL + "&oCuenta premium"), 20, 20*3, 40);
        }*/

        event.setJoinMessage(ChatColor.translateAlternateColorCodes('&',
                "&8[&a+&8] " + COLOR_ESPECIAL + event.getPlayer().getDisplayName() + COLOR_INFO + " se a unido."));
    }

    @EventHandler
    public void onLogin(PlayerLoginEvent event) {
        ServiceSection.getSimulateOnlineMode().applySkin(event.getPlayer());
        CheckBan.onLogin(event);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPreLogin(AsyncPlayerPreLoginEvent event) {
        if (AntiTwoPlayer.checkTwoPlayer(event.getName())){
            event.setKickMessage(ChatColor.translateAlternateColorCodes('&',COLOR_ERROR +
                    "¡¡Ya Este Jugador Esta Jugando!!"));
            event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_BANNED);
        }
    }
}
