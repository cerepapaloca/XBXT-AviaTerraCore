package net.atcore.listener;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.atcore.AviaTerraCore;
import net.atcore.messages.MessagesType;
import net.atcore.security.SecuritySection;
import org.bukkit.Sound;

import java.util.UUID;

public class PacketListener {

    public PacketListener(){
        registerEvents();
    }


    private void registerEvents(){
        /*NO BORRAR POR SI ACASO
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(AviaTerraCore.getInstance(),PacketType.values()
        ){
            @Override
            public void onPacketReceiving(PacketEvent event) {
                Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&6Cliente " + "Names " + event.getPacketType()));
                Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&6datos " + event.getPacket().getModifier().getValues().toString()));
            }
            @Override
            public void onPacketSending(PacketEvent event) {
                Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&bServer " + "Names " + event.getPacketType()));
                Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&bdatos " + event.getPacket().getModifier().getValues().toString()));
            }
        });*/

        ///////////////////////////////////////////////////////////////////////////////

        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(AviaTerraCore.getInstance(),
                PacketType.Login.Client.ENCRYPTION_BEGIN
        ) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                event.setCancelled(true);//se cancela por qué si no el servidor le tira error al cliente por enviar un paquete que no debería
                SecuritySection.getSimulateOnlineMode().startEncryption(event.getPlayer(), event.getPacket());
            }
        });

        ///////////////////////////////////////////////////////////////////////////////

        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(AviaTerraCore.getInstance(),
                PacketType.Login.Client.START
        ){
            @Override
            public void onPacketReceiving(PacketEvent event) {
                event.setCancelled(SecuritySection.getSimulateOnlineMode().preStartLogin(event.getPlayer(), event.getPacket()));
            }
        });

        ///////////////////////////////////////////////////////////////////////////////

        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(
                AviaTerraCore.getInstance(),
                PacketType.Play.Server.CHAT
        ) {
            @Override
            public void onPacketSending(PacketEvent event) {
                ChatListener chatListener = ListenerManagerSection.getChatListener();
                if (chatListener.getLastPlayerMention() == null)return;
                UUID playerUUID = chatListener.getLastPlayerMention().getUniqueId();

                if (!playerUUID.equals(event.getPlayer().getUniqueId())) return;
                event.getPlayer().playSound(event.getPlayer(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.7f,1);
                chatListener.setLastPlayerMention(null);

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
                                extraObject.addProperty("text", MessagesType.INFO.getSecondColor() + extraText);
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

        ///////////////////////////////////////////////////////////////////////////////
    }
}
