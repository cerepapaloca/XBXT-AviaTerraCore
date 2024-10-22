package net.atcore.ListenerManager;

import net.atcore.Messages.TypeMessages;
import net.atcore.Security.Login.LoginManager;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import static net.atcore.Messages.MessagesManager.*;
import static net.atcore.Moderation.Ban.CheckAutoBan.checkAutoBanChat;
import static net.atcore.Moderation.Ban.CheckBan.checkChat;
import static net.atcore.Moderation.ChatModeration.CheckChatModeration;

public class ChatListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        event.setCancelled(true);

        if (!LoginManager.checkLoginIn(player, true)) {
            sendMessage(player, "Te tienes que loguear para escribir en el chat", TypeMessages.ERROR);
            return;
        }

        LuckPerms luckPerms = LuckPermsProvider.get();
        User user = luckPerms.getUserManager().getUser(player.getUniqueId());
        if (user != null) {
            String prefix = user.getCachedData().getMetaData().getPrefix();

            if (checkChat(event)) return;//está baneado?
            checkAutoBanChat(player , event.getMessage());//se le va a banear?
            if (CheckChatModeration(player, event.getMessage(), prefix)) return;//hay algo indecente?

            sendMessageConsole("&r" + prefix + "    " + player.getName() + " » &7" + event.getMessage(), TypeMessages.INFO);
            broadcastChat(event.getMessage(), user, player);
        }
    }

    private void broadcastChat(String message, User user, Player p) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!message.contains(player.getName())) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        user.getCachedData().getMetaData().getPrefix() + "    " + p.getDisplayName() + " » &7" + message));
            }else {
                player.playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        user.getCachedData().getMetaData().getPrefix() + "    " + p.getDisplayName() + " » " + COLOR_ESPECIAL + message));
            }
        }
    }
}
