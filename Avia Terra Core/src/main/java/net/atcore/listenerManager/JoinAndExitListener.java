package net.atcore.listenerManager;

import net.atcore.AviaTerraCore;
import net.atcore.AviaTerraPlayer;
import net.atcore.moderation.Ban.CheckBan;
import net.atcore.security.AntiTwoPlayer;
import net.atcore.security.Login.DataLimbo;
import net.atcore.security.Login.DataLogin;
import net.atcore.security.Login.LoginManager;
import net.atcore.service.ServiceSection;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static net.atcore.messages.MessagesManager.*;
import static net.atcore.security.Login.LoginManager.*;

public class JoinAndExitListener implements Listener {

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (!getListPlayerLoginIn().contains(player.getUniqueId())) {//esto debería ir en LoginManager pero bueno
            DataLogin login = LoginManager.getDataLogin(player);
            if (login == null)return;
            DataLimbo limbo = login.getLimbo();
            if (limbo == null)return;

            player.getInventory().setContents(limbo.getItems());
            player.teleport(limbo.getLocation());
            player.setGameMode(limbo.getGameMode());
        }
        //AviaTerraPlayer.getPlayer(player).getInventorySection().getBaseActions().closeInventory(AviaTerraPlayer.getPlayer(player));
        List<UUID> UUIDPlayers = List.copyOf(AviaTerraPlayer.getPlayer(player).getManipulatorInventoryPlayer());
        UUIDPlayers.forEach(UUID -> Objects.requireNonNull(Bukkit.getPlayer(UUID)).closeInventory());
        event.setQuitMessage(ChatColor.translateAlternateColorCodes('&',
                "&8[&4-&8] " + COLOR_ESPECIAL + event.getPlayer().getDisplayName() + COLOR_INFO + " se a ido."));
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        checkJoin(player);
        event.setJoinMessage(ChatColor.translateAlternateColorCodes('&',
                "&8[&a+&8] " + COLOR_ESPECIAL + event.getPlayer().getDisplayName() + COLOR_INFO + " se a unido."));
    }

    @EventHandler
    public void onLogin(PlayerLoginEvent event) {
        AviaTerraPlayer.addPlayer(event.getPlayer());
        ServiceSection.getSimulateOnlineMode().applySkin(event.getPlayer());
        CheckBan.onLogin(event);
    }

    @EventHandler
    public void onPreLogin(AsyncPlayerPreLoginEvent event) {
        if (AntiTwoPlayer.checkTwoPlayer(event.getName())){
            event.setKickMessage(ChatColor.translateAlternateColorCodes('&',COLOR_ERROR +
                    "¡¡Ya Este Jugador Esta Jugando!!"));
            event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_BANNED);
        }
    }
}
