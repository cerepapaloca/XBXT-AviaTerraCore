package net.atcore.listener;

import lombok.Getter;
import lombok.Setter;
import net.atcore.messages.Message;
import net.atcore.messages.MessagesType;
import net.atcore.moderation.ban.ContextBan;
import net.atcore.moderation.ChatModeration;
import net.atcore.security.Login.LoginManager;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import static net.atcore.messages.MessagesManager.*;
import static net.atcore.moderation.ban.CheckAutoBan.checkAutoBanChat;
@Setter
@Getter
public class ChatListener implements Listener {

    private Player lastPlayerMention;

    @EventHandler(priority = EventPriority.HIGH)
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();


        if (!LoginManager.checkLoginIn(player)) {
            sendMessage(player, Message.LOGIN_LIMBO_CHAT_WRITE.getMessage(), MessagesType.ERROR);
            event.setCancelled(true);
            return;
        }

        LuckPerms luckPerms = LuckPermsProvider.get();
        User user = luckPerms.getUserManager().getUser(player.getUniqueId());
        if (user != null) {
            String prefix = user.getCachedData().getMetaData().getPrefix();
            checkAutoBanChat(player , event.getMessage());//se le va a banear?
            if (ContextBan.CHAT.onContext(player, event)){//está baneado?
                event.setCancelled(true);
                return;
            }
            if (ChatModeration.antiSpam(player, message) || ChatModeration.antiBanWord(player, message)){
                event.setCancelled(true);
                return;//hay algo indecente?
            }
            event.setMessage(ChatColor.GRAY + message);
            event.setFormat(prefix + Message.EVENT_FORMAT_CHAT.getMessage());

            for (Player Player : Bukkit.getOnlinePlayers()) {//busca todos los jugadores
                if (message.contains(Player.getName())){
                    lastPlayerMention = Player;
                }
            }
        }
    }
}
