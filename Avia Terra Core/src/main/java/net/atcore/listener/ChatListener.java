package net.atcore.listener;

import io.papermc.paper.event.player.AsyncChatEvent;
import lombok.Getter;
import lombok.Setter;
import net.atcore.AviaTerraCore;
import net.atcore.messages.*;
import net.atcore.moderation.ChatModeration;
import net.atcore.moderation.ban.ContextBan;
import net.atcore.security.Login.LoginManager;
import net.atcore.utils.GlobalUtils;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
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
import static net.atcore.messages.MessagesManager.sendString;
import static net.atcore.moderation.ban.CheckAutoBan.checkAutoBanChat;
@Setter
@Getter
public class ChatListener implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void onChat(AsyncChatEvent event) {
        Player player = event.getPlayer();
        Component message = event.message();
        event.setCancelled(true);
        String textPlain = PlainTextComponentSerializer.plainText().serialize(message);

        if (!LoginManager.checkLogin(player)) {
            sendMessage(player, Message.LOGIN_LIMBO_CHAT_WRITE.getMessage(player), TypeMessages.ERROR);
            return;
        }

        checkAutoBanChat(player , textPlain);//se le va a banear?
        if (ContextBan.CHAT.onContext(player, event) != null){//está baneado?
            return;
        }

        if (ChatModeration.antiSpam(player, textPlain) || ChatModeration.antiBanWord(player, textPlain)){
            return;//hay algo indecente?
        }

        for (Player target : Bukkit.getOnlinePlayers()) {//busca todos los jugadores
            target.sendMessage(mainFormat(message, player, target, textPlain.contains(target.getName())));
        }
        Bukkit.getConsoleSender().sendMessage(mainFormat(message, player, player, textPlain.contains(player.getName())));

        TextChannel channel = AviaTerraCore.jda.getTextChannelById(DiscordBot.chatId);
        if (channel !=  null) {
            channel.sendMessage(PlainTextComponentSerializer.plainText().serialize(
                    GlobalUtils.chatColorLegacyToComponent(
                            String.format(Message.EVENT_CHAT_FORMAT.getMessageLocaleDefault(), "**" + player.getName() + "**", textPlain))
            )).queue();
        }
    }

    public Component mainFormat(Component message, @NotNull Player sender, Player target, boolean isMention) {
        int distanceWalked = sender.getStatistic(Statistic.WALK_ONE_CM) + sender.getStatistic(Statistic.SPRINT_ONE_CM);
        long distanceWalkedKm = Math.round((distanceWalked / 100000.0)*10);
        int timePlayedTicks = sender.getStatistic(Statistic.PLAY_ONE_MINUTE);
        long timePlayedHours = Math.round((timePlayedTicks / 20.0 / 3600.0)*10);
        String locale = sender.locale().getDisplayName();

        Component hoverText = MessagesManager.applyFinalProprieties(
                String.format(EVENT_CHAT_HOVER.getMessage(sender), (float) distanceWalkedKm/10, (float) timePlayedHours/10, sender.getName(), locale),
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
