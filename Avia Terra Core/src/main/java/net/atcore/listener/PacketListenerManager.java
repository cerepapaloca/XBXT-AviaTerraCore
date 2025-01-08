package net.atcore.listener;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.atcore.AviaTerraCore;
import net.atcore.messages.MessagesType;
import net.atcore.security.Login.model.LimboData;
import net.atcore.security.Login.model.LoginData;
import net.atcore.security.Login.LoginManager;
import net.atcore.security.SecuritySection;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

public class PacketListenerManager {

    public PacketListenerManager(){

    }

    private final static HashMap<UUID, HashSet<PacketContainer>> PACKET_LISTENERS = new HashMap<>();

    public static void registerEvents(){
        /*NO BORRAR POR SI ACASO
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(AviaTerraCore.getInstance(),PacketType.values()
        ){
            @Override
            public void onPacketReceiving(PacketEvent event) {
                Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&6Cliente " + "Names " + event.getPacketType()));
                //Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&6datos " + event.getPacket().getModifier().getValues().toString()));
            }
            @Override
            public void onPacketSending(PacketEvent event) {
                Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&bServer " + "Names " + event.getPacketType()));
                //Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&bdatos " + event.getPacket().getModifier().getValues().toString()));
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
                PacketType.Play.Server.MAP_CHUNK
        ) {
            @Override
            public void onPacketSending(PacketEvent event) {
                LoginData login = LoginManager.getDataLogin(event.getPlayer());
                UUID uuid = event.getPlayer().getUniqueId();
                if (login.hasSession()){
                    PACKET_LISTENERS.remove(uuid);
                    return;
                }
                if (!PACKET_LISTENERS.containsKey(uuid)) {
                    PACKET_LISTENERS.put(uuid, new HashSet<>());
                }
                if (login.isLimboMode()){
                    LimboData limbo = login.getLimbo();
                    if (limbo.getPackets() == null){ // Pasa todos los paquetes guardados al limbo data
                        limbo.setPackets(PACKET_LISTENERS.get(uuid));
                    }
                    limbo.getPackets().add(event.getPacket());
                }else { // En caso de que no este en limbo mode lo va guardado
                    PACKET_LISTENERS.get(uuid).add(event.getPacket());
                }
                event.setCancelled(true);

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
                ChatListener chatListener = ListenerSection.getChatListener();
                if (chatListener.getLastPlayerMention() == null) return;
                UUID playerUUID = chatListener.getLastPlayerMention().getUniqueId();

                if (!playerUUID.equals(event.getPlayer().getUniqueId())) return;
                event.getPlayer().playSound(event.getPlayer(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.7f, 1);
                chatListener.setLastPlayerMention(null);

                // Obtener el mensaje original en formato JSON
                WrappedChatComponent chatComponent = event.getPacket().getChatComponents().read(0);

                if (chatComponent != null) {
                    String originalJsonMessage = chatComponent.getJson();
                    // Convertir el mensaje JSON en un objeto JsonObject
                    JsonObject jsonObject = JsonParser.parseString(originalJsonMessage).getAsJsonObject();

                    // Cambiar el color en la parte "extra"
                    if (jsonObject.has("extra")) {
                        JsonArray extraArray = jsonObject.getAsJsonArray("extra");

                        // Iterar a través de los elementos en "extra"
                        for (int i = 0; i < extraArray.size(); i++) {
                            try {
                                JsonObject extraObject = extraArray.get(i).getAsJsonObject();
                                if (extraObject.has("text")) {
                                    // Si el primer elemento tiene color eso quieres decir que se está tomando el nombre de usuario
                                    if (extraObject.has("color") && i == 0) continue;
                                    // Cambiar el color del texto
                                    extraObject.addProperty("color", "gold");
                                }
                            } catch (Exception ignored) {
                                //throw new RuntimeException("Couldn't parse extra");
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
