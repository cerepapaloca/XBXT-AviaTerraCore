package net.atcore.listener;

import io.papermc.paper.chat.ChatRenderer;
import io.papermc.paper.event.player.AsyncChatEvent;
import io.papermc.paper.event.player.ChatEvent;
import lombok.Getter;
import lombok.Setter;
import net.atcore.AviaTerraCore;
import net.atcore.data.DataSection;
import net.atcore.messages.*;
import net.atcore.moderation.ban.ContextBan;
import net.atcore.moderation.ChatModeration;
import net.atcore.security.Login.LoginManager;
import net.atcore.utils.GlobalUtils;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import static net.atcore.messages.MessagesManager.sendMessage;
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

        if (!LoginManager.checkLoginIn(player)) {
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
            player.sendMessage(finalFormat(message, player, target, textPlain.contains(player.getName())));
        }

        TextChannel channel = AviaTerraCore.jda.getTextChannelById(DiscordBot.chatId);
        if (channel !=  null) {
            channel.sendMessage(textPlain).queue();
        }
    }

    public Component finalFormat(Component message, Player sender, Player target, boolean isMention) {
        Component displayName = sender.displayName();
        int distanceWalked = sender.getStatistic(Statistic.WALK_ONE_CM) + sender.getStatistic(Statistic.SPRINT_ONE_CM);
        double distanceWalkedKm = distanceWalked / 100000.0;
        int timePlayedTicks = sender.getStatistic(Statistic.PLAY_ONE_MINUTE);
        double timePlayedHours = timePlayedTicks / 20.0 / 3600.0;

        Component hoverText = MessagesManager.applyFinalProprieties(
                String.format("""
                Distancia recorrida: <|%s KM|>
                Tiempo jugado: <|%s H|>
                Nombre: <|%s|>
                """, distanceWalkedKm, timePlayedHours, sender.getName()), TypeMessages.INFO, CategoryMessages.PRIVATE, false
        );
        if (isMention) target.playSound(target, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.7f, 1);
        return displayName.hoverEvent(HoverEvent.showText(hoverText)).append(Component.text(" > ")).append(isMention ? message.color(NamedTextColor.AQUA) : message);
    }
}
