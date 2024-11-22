package net.atcore.listenerManager;

import lombok.Getter;
import lombok.Setter;
import net.atcore.messages.TypeMessages;
import net.atcore.moderation.Ban.ContextBan;
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

import java.net.SocketException;

import static net.atcore.messages.MessagesManager.*;
import static net.atcore.moderation.Ban.CheckAutoBan.checkAutoBanChat;
@Setter
@Getter
public class ChatListener implements Listener {

    private Player lastPlayerMention;

    @EventHandler(priority = EventPriority.HIGH)
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();


        if (!LoginManager.checkLoginIn(player)) {
            sendMessage(player, "Te tienes que loguear para escribir en el chat", TypeMessages.ERROR);
            event.setCancelled(true);
            return;
        }

        LuckPerms luckPerms = LuckPermsProvider.get();
        User user = luckPerms.getUserManager().getUser(player.getUniqueId());
        if (user != null) {
            String prefix = user.getCachedData().getMetaData().getPrefix();

            try {
                checkAutoBanChat(player , event.getMessage());//se le va a banear?
            } catch (SocketException e) {
                sendMessageConsole("Hubo un problema con las bases de datos al banear " + player.getName(), TypeMessages.ERROR);
                throw new RuntimeException(e);
            }

            if (ContextBan.CHAT.onContext(player, event)){//está baneado?
                event.setCancelled(true);
                return;
            }
            if (ChatModeration.antiSpam(player, message) || ChatModeration.antiBanWord(player, message)){
                event.setCancelled(true);
                return;//hay algo indecente?
            }
            event.setMessage(ChatColor.GRAY + message);
            event.setFormat(prefix + " %1$s » %2$s");

            for (Player Player : Bukkit.getOnlinePlayers()) {//busca todos los jugadores
                if (message.contains(Player.getName())){
                    lastPlayerMention = Player;
                }
            }
        }
    }
}
