package net.atcore.listener;

import net.atcore.AviaTerraCore;
import net.atcore.achievement.BaseAchievement;
import net.atcore.aviaterraplayer.AviaTerraPlayer;
import net.atcore.data.sql.DataBaseRegister;
import net.atcore.messages.CategoryMessages;
import net.atcore.messages.Message;
import net.atcore.messages.MessagesManager;
import net.atcore.messages.TypeMessages;
import net.atcore.moderation.ban.BanManager;
import net.atcore.moderation.ban.ContextBan;
import net.atcore.moderation.ban.DataBan;
import net.atcore.security.AntiTwoPlayer;
import net.atcore.security.SecuritySection;
import net.atcore.security.login.LimboManager;
import net.atcore.security.login.LoginManager;
import net.atcore.security.login.model.LimboData;
import net.atcore.security.login.model.LoginData;
import net.atcore.utils.AviaTerraScheduler;
import net.atcore.utils.GlobalUtils;
import org.bukkit.craftbukkit.util.WeakCollection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

import static net.atcore.security.login.LoginManager.onEnteringServer;

public class JoinAndQuitListener implements Listener {

    public static final WeakCollection<UUID> uniquePlayers = new WeakCollection<>();

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        event.quitMessage(null);

        //Esto es para que los jugadores no logueados
        if (LoginManager.isLimboMode(player)) {
            LoginData login = LoginManager.getDataLogin(player);
            LimboData limbo = login.getLimbo();
            // Carga los datos del jugador para que el servidor guarde sus datos como si estuviera logueado
            limbo.restorePlayer(player);
        }
        LimboManager.inProcess.remove(player.getUniqueId());
        UUID uuidLimbo = GlobalUtils.getRealUUID(player);
        AviaTerraPlayer atp = AviaTerraPlayer.getPlayer(player);
        // Esta tarea descargar los datos del jugador para ahorrar memoria
        BukkitTask task = new BukkitRunnable() {
            private final UUID uuid = player.getUniqueId();
            @Override
            public void run() {
                atp.unloadPlayer();
                AviaTerraPlayer.TASKS_UNLOAD.remove(uuid);
            }
        }.runTaskLater(AviaTerraCore.getInstance(), 20*60*60);
        player.setInvulnerable(false);
        AviaTerraPlayer.TASKS_UNLOAD.put(uuidLimbo, task);
        //MessagesManager.logConsole("`<red>-</red>` jugador: <|" + player.getName() + "|>", TypeMessages.INFO, CategoryMessages.PLAY);
        MessagesManager.quitAndJoinMessage(player, Message.EVENT_QUIT);
        if (LoginManager.getDataLogin(player) != null) {// si le llega a borrar el registro
            UUID uuid = GlobalUtils.getRealUUID(player);
            AviaTerraScheduler.enqueueTaskAsynchronously(() -> DataBaseRegister.checkRegister(player, uuid));
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(@NotNull PlayerJoinEvent event) {
        Player player = event.getPlayer();
        event.joinMessage(null);
        //MessagesManager.logConsole("`<green>+</green>` jugador: <|" + player.getName() + "|>", TypeMessages.INFO, CategoryMessages.PLAY);

        onEnteringServer(player);
        AviaTerraScheduler.enqueueTaskAsynchronously(() -> {
            if (AviaTerraPlayer.getPlayer(player).getNameColor() == null) return;
            player.displayName(GlobalUtils.chatColorLegacyToComponent(AviaTerraPlayer.getPlayer(player).getNameColor()));
        });

        AviaTerraScheduler.runTaskLater(20L*2, () -> {
            if (LoginManager.getDataLogin(player) != null && LoginManager.getDataLogin(player).hasSession()) AviaTerraScheduler.enqueueTaskAsynchronously(() -> {
                BaseAchievement.sendAllAchievement(player);
                GlobalUtils.addRangeVote(player);
            });
        });

        MessagesManager.quitAndJoinMessage(player, Message.EVENT_JOIN);
    }

    @EventHandler
    public void onLogin(@NotNull PlayerLoginEvent event) {
        Player player = event.getPlayer();
        DataBan ban = ContextBan.GLOBAL.onContext(player, event);
        if (ban != null){
            event.kickMessage(MessagesManager.applyFinalProprieties(player, GlobalUtils.kickPlayer(player, BanManager.formadMessageBan(ban)), TypeMessages.KICK, CategoryMessages.PRIVATE, false));
            event.setResult(PlayerLoginEvent.Result.KICK_BANNED);
            return;
        }
        AviaTerraPlayer.addPlayer(player);
        UUID realUUID = GlobalUtils.getRealUUID(player);
        if (AviaTerraPlayer.TASKS_UNLOAD.containsKey(realUUID)) AviaTerraPlayer.TASKS_UNLOAD.remove(realUUID).cancel();
        SecuritySection.getSimulateOnlineMode().applySkin(player);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPreLogin(@NotNull AsyncPlayerPreLoginEvent event) {
        if (AntiTwoPlayer.checkTwoPlayer(event.getName())){
            event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
            event.kickMessage(MessagesManager.applyFinalProprieties(null, Message.SECURITY_KICK_ANTI_TWO_PLAYER.getMessageLocaleDefault(), TypeMessages.KICK, CategoryMessages.LOGIN, false));
        }
    }
}
