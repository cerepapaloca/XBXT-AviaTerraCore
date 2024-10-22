package net.atcore.ListenerManager;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.atcore.AviaTerraCore;
import net.atcore.Messages.TypeMessages;
import net.atcore.Moderation.Ban.CheckBan;
import net.atcore.Moderation.ChatModeration;
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

import java.util.UUID;

import static net.atcore.Messages.MessagesManager.*;
import static net.atcore.Moderation.Ban.CheckAutoBan.checkAutoBanChat;

public class ChatListener implements Listener {

    public ChatListener() {
        registerPacketListeners();
    }

    private UUID playerUUID;

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();


        if (!LoginManager.checkLoginIn(player, true)) {
            sendMessage(player, "Te tienes que loguear para escribir en el chat", TypeMessages.ERROR);
            event.setCancelled(true);
            return;
        }

        LuckPerms luckPerms = LuckPermsProvider.get();
        User user = luckPerms.getUserManager().getUser(player.getUniqueId());
        if (user != null) {
            String prefix = user.getCachedData().getMetaData().getPrefix();

            checkAutoBanChat(player , event.getMessage());//se le va a banear?

            if (CheckBan.checkChat(player)){//está baneado?
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
                    playerUUID = Player.getUniqueId();
                }
            }
        }
    }

    private void registerPacketListeners() {
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(
                AviaTerraCore.getInstance(),
                PacketType.Play.Server.CHAT
        ) {
            @Override
            public void onPacketSending(PacketEvent event) {

                if (playerUUID == null)return;
                if (!playerUUID.equals(event.getPlayer().getUniqueId())) return;
                event.getPlayer().playSound(event.getPlayer(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.7f,1);
                playerUUID = null;

                // Obtener el mensaje original en formato JSON
                WrappedChatComponent chatComponent = event.getPacket().getChatComponents().read(0);

                if (chatComponent != null) {
                    String originalJsonMessage = chatComponent.getJson();

                    // de texto a json
                    JsonObject jsonObject = JsonParser.parseString(originalJsonMessage).getAsJsonObject();

                    //cambiar el color en la parte extra
                    if (jsonObject.has("extra")) {
                        JsonArray extraArray = jsonObject.getAsJsonArray("extra");

                        //puede venir varios
                        for (int i = 0; i < extraArray.size(); i++) {
                            JsonObject extraObject = extraArray.get(i).getAsJsonObject();
                            if (extraObject.has("text")) {
                                String extraText = extraObject.get("text").getAsString();
                                extraObject.addProperty("text", ChatColor.AQUA + extraText);
                            }
                        }
                    }

                    // Convertir el objeto JSON de nuevo a cadena
                    String modifiedJsonMessage = jsonObject.toString();

                    // Actualizar el paquete con el nuevo mensaje JSON modificado
                    event.getPacket().getChatComponents().write(0, WrappedChatComponent.fromJson(modifiedJsonMessage));
                }
            }
        });
    }
}
