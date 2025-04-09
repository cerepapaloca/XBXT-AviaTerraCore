package net.atcore.listener;

import io.papermc.paper.event.player.AsyncChatEvent;
import lombok.Getter;
import lombok.Setter;
import net.atcore.AviaTerraCore;
import net.atcore.aviaterraplayer.AviaTerraPlayer;
import net.atcore.command.commnads.TellCommand;
import net.atcore.messages.*;
import net.atcore.moderation.ChatModeration;
import net.atcore.moderation.ban.ContextBan;
import net.atcore.security.login.LoginManager;
import net.atcore.utils.GlobalUtils;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import static net.atcore.messages.Message.EVENT_CHAT_HOVER;
import static net.atcore.messages.MessagesManager.sendMessage;

@Setter
@Getter
public class ChatListener implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void onChat(AsyncChatEvent event) {
        Player sender = event.getPlayer();
        Component message = event.message();
        event.setCancelled(true);

        String textPlain = PlainTextComponentSerializer.plainText().serialize(message);

        if (!LoginManager.checkLogin(sender)) {
            sendMessage(sender, Message.LOGIN_LIMBO_CHAT_WRITE);
            return;
        }

        if (ContextBan.CHAT.onContext(sender, event) != null){//está baneado?
            return;
        }

        /*if (ChatModeration.antiSpam(sender, textPlain) || ChatModeration.antiBanWord(sender, textPlain)){
            return;//hay algo indecente?
        }*/

        TellCommand.lastNamePlayer = sender.getName();

        for (Player target : Bukkit.getOnlinePlayers()) {//busca todos los jugadores
            if (!LoginManager.getDataLogin(target).hasSession())return;
            AviaTerraPlayer atp = AviaTerraPlayer.getPlayer(target);
            // En caso de que tenga el jugador bloqueado no se le enviara el mensaje
            if (!atp.getPlayersBLock().contains(sender.getName())) {
                target.sendMessage(setFormat(message, sender, target, textPlain.contains(target.getName())));
            }
        }

        Bukkit.getConsoleSender().sendMessage(setFormat(message, sender, sender, textPlain.contains(sender.getName())));

        if (AviaTerraCore.jda != null) {
            AviaTerraCore.enqueueTaskAsynchronously(() -> {
                TextChannel channel = AviaTerraCore.jda.getTextChannelById(DiscordBot.chatId);
                if (channel !=  null) {
                    channel.sendMessage(PlainTextComponentSerializer.plainText().serialize(
                            GlobalUtils.chatColorLegacyToComponent(
                                    String.format(Message.EVENT_CHAT_FORMAT.getMessageLocaleDefault(), "**" + sender.getName() + "**", textPlain))
                    )).queue();
                }
            });
        }
    }

    public static Component setFormat(Component message, @NotNull Player sender, Player target, boolean isMention) {
        int distanceWalked = sender.getStatistic(Statistic.WALK_ONE_CM) + sender.getStatistic(Statistic.SPRINT_ONE_CM);
        long distanceWalkedKm = Math.round((distanceWalked / 100000.0)*10);
        int timePlayedTicks = sender.getStatistic(Statistic.PLAY_ONE_MINUTE);
        long timePlayedHours = Math.round((timePlayedTicks / 20.0 / 3600.0)*10);
        User user = AviaTerraCore.getLp().getUserManager().getUser(sender.getName());
        String groupName;
        if (user != null) {
            groupName = user.getPrimaryGroup();
        }else {
            groupName = "usuario";
        }
        String locale = sender.locale().getDisplayName(target.locale());

        Component hoverText = MessagesManager.applyFinalProprieties(
                String.format(EVENT_CHAT_HOVER.getMessage(target),
                        (float) distanceWalkedKm/10,
                        (float) timePlayedHours/10,
                        sender.getName(),
                        locale,
                        groupName.equals("default") ? "usuario" : groupName
                ),
                TypeMessages.INFO, CategoryMessages.PRIVATE, false
        );
        if (isMention) target.playSound(target, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.7f, 1);

        TextReplacementConfig.Builder config1 = TextReplacementConfig.builder().matchLiteral("%1$s")
                .replacement(sender.displayName()
                .hoverEvent(HoverEvent.showText(hoverText))
                .clickEvent(ClickEvent.suggestCommand("/w " + sender.getName()))
        );
        TextReplacementConfig.Builder config2 = TextReplacementConfig.builder().matchLiteral("%2$s")
                .replacement(isMention ? message.color(NamedTextColor.AQUA) : message.color(NamedTextColor.GRAY));

        return GlobalUtils.chatColorLegacyToComponent(Message.EVENT_CHAT_FORMAT.getMessage(target))
                .replaceText(config1.build())
                .replaceText(config2.build());
    }
}
