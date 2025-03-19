package net.atcore.listener;

import net.atcore.AviaTerraCore;
import net.atcore.aviaterraplayer.AviaTerraPlayer;
import net.atcore.data.DataSection;
import net.atcore.data.sql.DataBaseRegister;
import net.atcore.messages.*;
import net.atcore.moderation.ban.BanManager;
import net.atcore.moderation.ban.ContextBan;
import net.atcore.moderation.ban.DataBan;
import net.atcore.security.AntiTwoPlayer;
import net.atcore.security.SecuritySection;
import net.atcore.security.login.LimboManager;
import net.atcore.security.login.LoginManager;
import net.atcore.security.login.model.LimboData;
import net.atcore.security.login.model.LoginData;
import net.atcore.utils.GlobalUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
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

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static net.atcore.security.login.LoginManager.onEnteringServer;

public class JoinAndQuitListener implements Listener {

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        //Esto es para que los jugadores no logueados
        if (LoginManager.isLimboMode(player)) {
            LoginData login = LoginManager.getDataLogin(player);
            LimboData limbo = login.getLimbo();
            // Carga los datos del jugador para que el servidor guarde sus datos como si estuviera logueado
            limbo.restorePlayer(player);
        }
        LimboManager.IN_PROCESS.remove(player.getUniqueId());
        UUID uuidLimbo = GlobalUtils.getRealUUID(player);
        UUID uuidPlayer = player.getUniqueId();
        AviaTerraPlayer atp = AviaTerraPlayer.getPlayer(player);
        // Esta tarea descargar los datos del jugador para ahorrar memoria
        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                DataSection.getCacheLimboFlies().unloadConfigFile(uuidLimbo.toString());
                DataSection.getPlayersDataFiles().unloadConfigFile(uuidPlayer.toString());
                atp.unloadPlayer();
            }
        }.runTaskLater(AviaTerraCore.getInstance(), 20*60*60);
        AviaTerraPlayer.TASKS_UNLOAD.put(uuidLimbo, task);

        if (LoginManager.getDataLogin(player) != null) {// si le llega a borrar el registro
            UUID uuid = GlobalUtils.getRealUUID(player);
            AviaTerraCore.enqueueTaskAsynchronously(() -> DataBaseRegister.checkRegister(player, uuid));

            // Borra a los jugadores cracked y semi cracked que no pudieron registrarse para evitar tener jugadores fantasmas
            if (LoginManager.getDataLogin(player).getRegister().isTemporary()){
                AviaTerraCore.enqueueTaskAsynchronously(() -> {
                    LoginManager.removeDataLogin(GlobalUtils.getRealName(player));
                    DataBaseRegister.removeRegister(player.getName(), "Servidor (EL registro es temporal)");
                    /*if (!Service.removeStatsPlayer(player.getUniqueId())){
                        MessagesManager.logConsole(String.format("Error al borrar las stats del jugador <|%s|>", player.getName()), TypeMessages.WARNING);
                    }
                    if (!AviaTerraPlayer.getPlayer(player).getPlayerDataFile().getFile().delete()){
                        MessagesManager.logConsole(String.format("Error al borrar ATPF del jugador <|%s|>", player.getName()), TypeMessages.WARNING);
                    }
                    if (!Service.removePlayerData(player.getUniqueId())){
                        MessagesManager.logConsole(String.format("Error al borrar PlayerData del jugador <|%s|>", player.getName()), TypeMessages.WARNING);
                    }*/
                });
            }
        }

        List<UUID> UUIDPlayers = List.copyOf(AviaTerraPlayer.getPlayer(player).getModerationPlayer().getManipulatorInventoryPlayer());
        UUIDPlayers.forEach(UUID -> Objects.requireNonNull(Bukkit.getPlayer(UUID)).closeInventory());
        event.quitMessage(null);
        List<CommandSender> senders = new ArrayList<>(Bukkit.getOnlinePlayers());
        senders.add(Bukkit.getConsoleSender());
        for (CommandSender sender : senders) {
            MessagesManager.sendString(sender, String.format(Message.EVENT_QUIT.getMessage(sender), event.getPlayer().getName()), Message.EVENT_QUIT.getTypeMessages(), false);
        }

        sendEmbed(player, Color.RED, "%s Se salio del servidor");
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(@NotNull PlayerJoinEvent event) {
        Player player = event.getPlayer();
        event.joinMessage(null);
        List<CommandSender> senders = new ArrayList<>(Bukkit.getOnlinePlayers());
        senders.add(Bukkit.getConsoleSender());
        for (CommandSender sender : senders) {
            MessagesManager.sendString(sender, String.format(Message.EVENT_JOIN.getMessage(sender), event.getPlayer().getName()), Message.EVENT_JOIN.getTypeMessages(), false);
        }
        onEnteringServer(player);
        AviaTerraCore.taskSynchronously(() -> {
            String displayName = AviaTerraPlayer.getPlayer(player).getNameColor();
            if (displayName != null) player.displayName(GlobalUtils.chatColorLegacyToComponent(displayName));
        });

        new BukkitRunnable() {
            @Override
            public void run() {
                GlobalUtils.addRangeVote(player);
            }
        }.runTaskLater(AviaTerraCore.getInstance(), 20L*2);

        if (LoginManager.getDataLogin(player).getRegister().isTemporary()) {
            sendEmbed(player, Color.YELLOW, "%s Se unió por primera vez");// TODO: Hay que reglar eso
        }else {
            sendEmbed(player, Color.GREEN, "%s Se unió al servidor");
        }
    }

    private void sendEmbed(@NotNull Player player, Color color, String message) {
        if (AviaTerraCore.jda != null) {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(color);
            String normalizedName = player.getName().startsWith(".") ? player.getName().substring(1) : player.getName();
            embed.setAuthor(String.format(message, player.getName()), null, "https://crafthead.net/cube/" + normalizedName);
            TextChannel textChannel = AviaTerraCore.jda.getTextChannelById(DiscordBot.JoinAndLeave);
            assert textChannel != null;
            textChannel.sendMessageEmbeds(embed.build()).queue();
        }
    }

    @EventHandler
    public void onLogin(@NotNull PlayerLoginEvent event) {
        Player player = event.getPlayer();
        DataBan ban = ContextBan.GLOBAL.onContext(player, event);
        if (ban != null){
            event.kickMessage(MessagesManager.applyFinalProprieties(GlobalUtils.kickPlayer(player, BanManager.formadMessageBan(ban)), TypeMessages.KICK, CategoryMessages.PRIVATE, false));
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
            event.kickMessage(MessagesManager.applyFinalProprieties(Message.SECURITY_KICK_ANTI_TWO_PLAYER.getMessageLocaleDefault(), TypeMessages.KICK, CategoryMessages.LOGIN, false));
        }
    }
}
