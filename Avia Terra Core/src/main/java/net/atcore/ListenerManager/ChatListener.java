package net.atcore.ListenerManager;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import net.atcore.Messages.TypeMessages;
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

import static net.atcore.Messages.MessagesManager.COLOR_ESPECIAL;
import static net.atcore.Messages.MessagesManager.sendMessageConsole;
import static net.atcore.Moderation.Ban.CheckAutoBan.checkAutoBanChat;
import static net.atcore.Moderation.Ban.CheckBan.checkChat;
import static net.atcore.Moderation.ChatModeration.CheckChatModeration;

public class ChatListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();
        PacketContainer packet =  ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.CHAT);

        // Añadir el mensaje (en formato JSON)
        packet.getChatComponents().write(0, WrappedChatComponent.fromText("test"));
        ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);

        e.setCancelled(true);

        LuckPerms luckPerms = LuckPermsProvider.get();
        User user = luckPerms.getUserManager().getUser(player.getUniqueId());
        if (user != null) {
            String prefix = user.getCachedData().getMetaData().getPrefix();

            if (checkChat(e)) return;
            checkAutoBanChat(player , e.getMessage());
            if (CheckChatModeration(e.getPlayer(), e.getMessage(), prefix)) return;

            sendMessageConsole("&r" + prefix + "    " + player.getName() + " » &7" + e.getMessage(), TypeMessages.INFO);
            broadcastChat(e.getMessage(), user);
        }
    }

    private void broadcastChat(String message, User user) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!message.contains(player.getName())) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        user.getCachedData().getMetaData().getPrefix() + "    " + player.getName() + " » &7" + message));
            }else {
                player.playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        user.getCachedData().getMetaData().getPrefix() + "    " + player.getName() + " » " + COLOR_ESPECIAL + message));
            }
        }
    }
}
