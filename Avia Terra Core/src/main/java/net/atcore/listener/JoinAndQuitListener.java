package net.atcore.listener;

import net.atcore.AviaTerraCore;
import net.atcore.aviaterraplayer.AviaTerraPlayer;
import net.atcore.data.sql.DataBaseRegister;
import net.atcore.messages.CategoryMessages;
import net.atcore.messages.Message;
import net.atcore.messages.MessagesManager;
import net.atcore.messages.MessagesType;
import net.atcore.moderation.ban.ContextBan;
import net.atcore.security.AntiTwoPlayer;
import net.atcore.security.Login.model.LimboData;
import net.atcore.security.Login.model.LoginData;
import net.atcore.security.Login.LoginManager;
import net.atcore.security.SecuritySection;
import net.atcore.utils.GlobalUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static net.atcore.security.Login.LoginManager.*;

public class JoinAndQuitListener implements Listener {

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        LoginData login = LoginManager.getDataLogin(player);
        if (LoginManager.isLimboMode(player)) {//Esto es para que los jugadores no logueados
            LimboData limbo = login.getLimbo();
            limbo.restorePlayer(player);//hace que el servidor guarde los datos del jugador como si tuviera logueado
        }

        if (LoginManager.getDataLogin(player) != null) {// si le llega a borrar el registro
            // Borra a los jugadores no premium que no pudieron registrarse para evitar tener jugadores fantasmas
            if (LoginManager.getDataLogin(player.getUniqueId()).getRegister().isTemporary()){
                AviaTerraCore.getInstance().enqueueTaskAsynchronously(() ->
                        DataBaseRegister.removeRegister(player.getName(), "Servidor"));
            }
        }

        List<UUID> UUIDPlayers = List.copyOf(AviaTerraPlayer.getPlayer(player).getModerationPlayer().getManipulatorInventoryPlayer());
        UUIDPlayers.forEach(UUID -> Objects.requireNonNull(Bukkit.getPlayer(UUID)).closeInventory());
        event.quitMessage(GlobalUtils.chatColorLegacyToComponent(MessagesManager.addProprieties(
                String.format(Message.EVENT_QUIT.getMessage(), event.getPlayer().getName()),
                MessagesType.INFO, false, false)));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        event.joinMessage(GlobalUtils.chatColorLegacyToComponent(MessagesManager.addProprieties(
                String.format(Message.EVENT_JOIN.getMessage(), event.getPlayer().getName()),
                MessagesType.INFO, false, false)));
        onEnteringServer(player);
        new BukkitRunnable() {
            @Override
            public void run() {
                GlobalUtils.addRangeVote(player);
            }
        }.runTaskLater(AviaTerraCore.getInstance(), 20L*2);
    }

    @EventHandler
    public void onLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        AviaTerraPlayer.addPlayer(player);
        SecuritySection.getSimulateOnlineMode().applySkin(player);
        ContextBan.GLOBAL.onContext(player, event);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPreLogin(AsyncPlayerPreLoginEvent event) {
        if (AntiTwoPlayer.checkTwoPlayer(event.getName())){
            event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
            event.kickMessage(MessagesManager.applyFinalProprieties(Message.SECURITY_KICK_ANTI_TWO_PLAYER.getMessage(), MessagesType.KICK, CategoryMessages.LOGIN, false));
        }
    }
}
