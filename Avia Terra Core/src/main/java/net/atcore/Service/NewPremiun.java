package net.atcore.Service;

import com.comphenix.protocol.PacketLogging;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.*;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.MinecraftKey;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import lombok.Getter;
import net.atcore.AviaTerraCore;
import net.atcore.Messages.TypeMessages;
import org.bukkit.Bukkit;
import com.mojang.authlib.properties.Property;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

import javax.crypto.Cipher;
import java.math.BigInteger;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import static net.atcore.Messages.MessagesManager.sendMessageConsole;

public class NewPremiun {

    private final AESKeyGenerator keyGenerator;

    public NewPremiun() {
        try {
            keyGenerator = new AESKeyGenerator();
            registerEvents();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Getter
    private static final HashMap<String, InetAddress> ips = new HashMap<>();
    private static final HashMap<String, String> verifyTokens = new HashMap<>();
    private static final HashMap<String, PacketContainer> isSuccessPackets = new HashMap();

    public void registerEvents(){
        boolean Test = false;

        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(AviaTerraCore.getInstance(),PacketType.values()/*,
                PacketType.Login.Server.SUCCESS,
                PacketType.Login.Server.ENCRYPTION_BEGIN,
                PacketType.Login.Server.DISCONNECT,
                PacketType.Login.Server.CUSTOM_PAYLOAD,
                PacketType.Login.Server.SET_COMPRESSION,
                PacketType.Play.Server.LOGIN,
                PacketType.Login.Client.START,
                PacketType.Login.Client.ENCRYPTION_BEGIN,
                PacketType.Login.Client.CUSTOM_PAYLOAD*/
        ){
            @Override
            public void onPacketReceiving(PacketEvent event) {
                Bukkit.getConsoleSender().sendMessage(event.getPacketType().name() +  " Cliente");
            }
            @Override
            public void onPacketSending(PacketEvent event) {
                Bukkit.getConsoleSender().sendMessage(event.getPacketType().name() + " Server");
                if (event.getPacket().getType().equals(PacketType.Login.Server.SUCCESS)) {
                }
                if (Test) {
                    return;
                }
                if (event.getPacketType() == PacketType.Login.Server.SUCCESS) {

                    if (isSuccessPackets.containsKey(event.getPlayer().getName())){
                        //ProtocolLibrary.getProtocolManager().sendServerPacket(event.getPlayer(), isSuccessPackets.get(event.getPlayer().getName()));
                        isSuccessPackets.remove(event.getPlayer().getName());
                        return;
                    }else{

                        isSuccessPackets.put(event.getPlayer().getName(), event.getPacket());
                        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&c" +
                                event.getPacketType().name() + " BLOCK"));
                        event.setCancelled(true);
                    }

                }
                PacketContainer packet = event.getPacket();
                /*if (PacketType.Login.Server.SET_COMPRESSION.equals(event.getPacketType())){
                    if (!packet.getModifier().read(0).equals(-1)) {
                        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&c" +
                                event.getPacketType().name() + " BLOCK"));
                        event.setCancelled(true);
                    }
                }*/
            }
        });
        if (Test) return;
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
                    byte[] sharedSecret = decryptSharedSecret(encryptedSharedSecret);
                    byte[] token = decryptToken(encryptedToken);

                    // Verificar el token
                    if (Objects.equals(verifyTokens.get(event.getPlayer().getName()), Arrays.toString(token))) {
                        sendMessageConsole("ok", TypeMessages.SUCCESS);
                        //event.setCancelled(true); // Cancelar la conexión si la verificación falla

                        ////////////////////////////////////////

                        PacketContainer packetCompression = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Login.Server.SET_COMPRESSION);
                        packetCompression.getIntegers().write(0, 256);
                        // Agregar metadata o alguna identificación
                        packetCompression.getModifier().write(0, -1); // Ejemplo de identificación (un campo no usado).
                        //ProtocolLibrary.getProtocolManager().sendServerPacket(event.getPlayer(), packetCompression);


                        ////////////////////////////////////////

                        // Aquí manejas la fase de login inicial antes de enviar el paquete de login success
                        String username = event.getPlayer().getName();
                        String ip = ips.get(event.getPlayer().getName()).getHostAddress();
                        String serverId = generateServerId("", sharedSecret, keyGenerator.getPublicKey().getEncoded());

                        JsonObject playerData = MojangAPI.checkPlayerStatus(username, serverId, ip);
                        if (playerData == null) return;

                        String uuid = playerData.get("id").getAsString();
                        String name = playerData.get("name").getAsString();

                        JsonObject properties = playerData.getAsJsonArray("properties").get(0).getAsJsonObject();
                        String textureValue = properties.get("value").getAsString();
                        String textureSignature = properties.get("signature").getAsString();

                        UUID playerUUID = UUID.fromString(uuid.replaceFirst(
                                "(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})",
                                "$1-$2-$3-$4-$5"
                        ));

                        GameProfile gameProfile = new GameProfile(playerUUID, name);
                        gameProfile.getProperties().put("textures", new Property("textures", textureValue, textureSignature));

                        WrappedGameProfile wrappedProfile = WrappedGameProfile.fromHandle(gameProfile);

                        PacketContainer loginSuccessPacket = new PacketContainer(PacketType.Login.Server.SUCCESS);
                        loginSuccessPacket.getGameProfiles().write(0, wrappedProfile);
                        loginSuccessPacket.getBooleans().write(0, true);

                        ProtocolLibrary.getProtocolManager().sendServerPacket(event.getPlayer(), isSuccessPackets.get(event.getPlayer().getName()));
                        //ProtocolLibrary.getProtocolManager().sendServerPacket(event.getPlayer(), loginSuccessPacket);
                        //event.setCancelled(true);
                        ////////////////////////////////////////


                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    event.setCancelled(true); // Cancelar la conexión en caso de error
                }

            }
        });

        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(AviaTerraCore.getInstance(), PacketType.Login.Client.START){
            @Override
            public void onPacketReceiving(PacketEvent event) {
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

    private byte[] decryptSharedSecret(byte[] encryptedSharedSecret) throws Exception {
        // Inicializa el cifrador RSA con la clave privada del servidor
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, keyGenerator.getPrivateKey()); // privateKey debe ser la clave privada del servidor

        // Realiza el descifrado y devuelve el secreto compartido
        return cipher.doFinal(encryptedSharedSecret);
    }

    private byte[] decryptToken(byte[] encryptedToken) throws Exception {
        // Inicializa el cifrador RSA con la clave privada del servidor
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, keyGenerator.getPrivateKey()); // privateKey debe ser la clave privada del servidor

        // Realiza el descifrado y devuelve el token
        return cipher.doFinal(encryptedToken);
    }

    public static String generateServerId(String serverId, byte[] sharedSecret, byte[] publicKey) {
        try {
            // Concatena el serverId, sharedSecret y publicKey en una sola cadena
            MessageDigest sha1 = MessageDigest.getInstance("SHA-1");

            // Actualiza el digest con los datos
            sha1.update(serverId.getBytes(StandardCharsets.UTF_8));
            sha1.update(sharedSecret);
            sha1.update(publicKey);

            // Convierte el digest (hash) en hexadecimal
            return toHex(sha1.digest());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    // Convierte un array de bytes en una cadena hexadecimal
    private static String toHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

}
