package net.atcore.Service;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.*;
import com.github.games647.craftapi.model.auth.Verification;
import com.github.games647.craftapi.resolver.MojangResolver;
import com.github.games647.craftapi.resolver.RateLimitException;
import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;
import lombok.Getter;
import net.atcore.AviaTerraCore;
import net.atcore.Messages.TypeMessages;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import javax.crypto.*;
import java.io.IOException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.*;

import static com.comphenix.protocol.PacketType.Login.Client.START;
import static net.atcore.Messages.MessagesManager.sendMessageConsole;

public class SimulateOnlineMode {

    private final Encrypt keyGenerator;

    public SimulateOnlineMode() {
        super();
        try {
            keyGenerator = new Encrypt();
            registerEvents();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Getter
    private static final HashMap<String, InetAddress> ips = new HashMap<>();
    private static final HashMap<String, String> verifyTokens = new HashMap<>();

    public void registerEvents(){

        /*ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(AviaTerraCore.getInstance(),PacketType.values()
        ){
            @Override
            public void onPacketReceiving(PacketEvent event) {
                Bukkit.getConsoleSender().sendMessage(event.getPacketType().name() +  " Cliente");
            }
            @Override
            public void onPacketSending(PacketEvent event) {
                Bukkit.getConsoleSender().sendMessage(event.getPacketType().name() + " Server");
            }
        });*/
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(AviaTerraCore.getInstance(), PacketType.Login.Client.ENCRYPTION_BEGIN) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                event.setCancelled(true);
                PacketContainer packet = event.getPacket();
                byte[] encryptedSharedSecret = packet.getByteArrays().read(0);
                byte[] encryptedToken = packet.getByteArrays().read(1);

                // Aquí deberías descifrar el secreto compartido y el token
                try {
                    // Descifrar usando la clave privada del servidor
                    SecretKey sharedSecret = ServiceSection.getEncrypt().decryptSharedSecret(encryptedSharedSecret);
                    byte[] token =  ServiceSection.getEncrypt().decryptToken(encryptedToken);

                    // Verificar el token
                    if (Objects.equals(verifyTokens.get("cerespapaloca"), Arrays.toString(token))) {
                        sendMessageConsole("ok", TypeMessages.SUCCESS);
                        //event.getAsyncMarker().incrementProcessingDelay();
                        ////////////////////////////////////////

                        String serverId = generateServerId("",keyGenerator.getPublicKey(),  sharedSecret);
                        Player player = event.getPlayer();
                        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                            MojangResolver resolver = AviaTerraCore.getResolver();

                            Optional<Verification> response;
                            try {
                                response = resolver.hasJoined("olasdda", serverId, InetAddress.getByName("localhost"));
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            response.ifPresent(verification -> ServiceSection.getEncrypt().encryptConnection(verification, player));
                        });

                    }else{
                        Bukkit.getConsoleSender().sendMessage("no son iguales");
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

            }
        });

        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(AviaTerraCore.getInstance(), PacketType.Login.Client.START){
            @Override
            public void onPacketReceiving(PacketEvent event) {

                event.setCancelled(true);
                PacketContainer packetEncryption = new PacketContainer(PacketType.Login.Server.ENCRYPTION_BEGIN);
                // Genera el token
                byte[] token = new byte[4];
                new java.security.SecureRandom().nextBytes(token);
                verifyTokens.put(event.getPacket().getStrings().read(0), Arrays.toString(token));

                packetEncryption.getByteArrays().write(0, keyGenerator.getPublicKey().getEncoded());
                packetEncryption.getByteArrays().write(1, token);

                packetEncryption.getBooleans().write(0, true);

                ProtocolLibrary.getProtocolManager().sendServerPacket(event.getPlayer(), packetEncryption);
            }
        });
    }

    public static String generateServerId(String serverId, PublicKey publicKey, SecretKey sharedSecret) {

        Hasher hasher = Hashing.sha1().newHasher();
        hasher.putBytes(serverId.getBytes(StandardCharsets.ISO_8859_1));
        hasher.putBytes(sharedSecret.getEncoded());
        hasher.putBytes(publicKey.getEncoded());

        byte[] serverHash = hasher.hash().asBytes();
        return (new BigInteger(serverHash)).toString(16);
    }

    public static void FakeStartPacket(String username, UUID uuid, Player player) {

        PacketContainer startPacket;
        startPacket = new PacketContainer(START);
        startPacket.getStrings().write(0, username);
        startPacket.getUUIDs().write(0, uuid);

        ProtocolLibrary.getProtocolManager().receiveClientPacket(player, startPacket, false);
    }
}
