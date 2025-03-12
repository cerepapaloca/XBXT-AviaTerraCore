package net.atcore.listener;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import net.atcore.AviaTerraCore;
import net.atcore.security.login.LimboManager;
import net.atcore.security.login.LoginManager;
import net.atcore.security.login.model.LimboData;
import net.atcore.security.login.model.LoginData;
import net.atcore.security.SecuritySection;
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
                Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&6datos " + event.getPacket().getModifier().getValues().toString()));
            }
            @Override
            public void onPacketSending(PacketEvent event) {
                if (event.getPacketType() == PacketType.Play.Server.MAP_CHUNK) return;
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
                SecuritySection.getSimulateOnlineMode().startProtocol(event.getPlayer(), event.getPacket());
            }
        });

        ///////////////////////////////////////////////////////////////////////////////

        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(AviaTerraCore.getInstance(),
                PacketType.Play.Server.MAP_CHUNK, PacketType.Play.Server.STATISTIC
        ) {
            @Override
            public void onPacketSending(PacketEvent event) {
                LoginData login = LoginManager.getDataLogin(event.getPlayer());
                UUID uuid = event.getPlayer().getUniqueId();
                if (!PACKET_LISTENERS.containsKey(uuid)) PACKET_LISTENERS.put(uuid, new HashSet<>());
                if (login == null){
                    PACKET_LISTENERS.get(uuid).add(event.getPacket());
                    return;
                }
                if (login.hasSession() || login.isBedrockPlayer()){
                    PACKET_LISTENERS.remove(uuid);
                    return;
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
                PacketType.Play.Server.POSITION
        ) {
            @Override
            public void onPacketSending(PacketEvent event) {
                Player player = event.getPlayer();
                try {
                    if (!LoginManager.getDataLogin(player).hasSession()) {
                        PacketContainer packet = event.getPacket();
                        packet.getDoubles().write(0, LimboManager.LIMBO_LOCATION.getX());
                        packet.getDoubles().write(1, LimboManager.LIMBO_LOCATION.getY());
                        packet.getDoubles().write(2, LimboManager.LIMBO_LOCATION.getZ());
                        packet.getFloat().write(0, LimboManager.LIMBO_LOCATION.getYaw());
                        packet.getFloat().write(1, LimboManager.LIMBO_LOCATION.getPitch());
                    }
                }catch (Exception ignored){

                }
            }
        });

        ///////////////////////////////////////////////////////////////////////////////
    }
}
