package net.atcore.listener;

import lombok.Getter;
import lombok.Setter;
import net.atcore.messages.Message;
import net.atcore.messages.TypeMessages;
import net.atcore.moderation.ban.ContextBan;
import net.atcore.moderation.ChatModeration;
import net.atcore.security.Login.LoginManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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

    private Player lastPlayerMention;

    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.HIGH)
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();


        if (!LoginManager.checkLoginIn(player)) {
            sendMessage(player, Message.LOGIN_LIMBO_CHAT_WRITE.getMessage(player), TypeMessages.ERROR);
            event.setCancelled(true);
            return;
        }

        checkAutoBanChat(player , event.getMessage());//se le va a banear?
        if (ContextBan.CHAT.onContext(player, event) != null){//está baneado?
            event.setCancelled(true);
            return;
        }
        if (ChatModeration.antiSpam(player, message) || ChatModeration.antiBanWord(player, message)){
            event.setCancelled(true);
            return;//hay algo indecente?
        }
        event.setMessage(ChatColor.GRAY + message);
        event.setFormat(ChatColor.translateAlternateColorCodes('&', Message.EVENT_FORMAT_CHAT.getMessage(player)));

        for (Player Player : Bukkit.getOnlinePlayers()) {//busca todos los jugadores
            if (message.contains(Player.getName())){
                lastPlayerMention = Player;
            }
        }
    }
}
