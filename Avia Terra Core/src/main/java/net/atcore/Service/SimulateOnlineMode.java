package net.atcore.Service;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.*;
import com.comphenix.protocol.injector.netty.channel.NettyChannelInjector;
import com.comphenix.protocol.injector.temporary.TemporaryPlayerFactory;
import com.comphenix.protocol.reflect.FuzzyReflection;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.reflect.accessors.Accessors;
import com.comphenix.protocol.reflect.accessors.FieldAccessor;
import com.comphenix.protocol.utility.MinecraftReflection;
import com.comphenix.protocol.wrappers.BukkitConverters;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedProfilePublicKey;
import com.github.games647.craftapi.model.auth.Verification;
import com.github.games647.craftapi.resolver.MojangResolver;
import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;
import com.google.common.io.Resources;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import lombok.Getter;
import lombok.val;
import net.atcore.AviaTerraCore;
import net.atcore.Messages.TypeMessages;
import net.atcore.Security.Login.LoginManager;
import net.atcore.Security.Login.SessionLogin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.util.*;
import java.util.function.Function;

import static com.comphenix.protocol.PacketType.Login.Client.START;
import static com.comphenix.protocol.PacketType.Login.Server.ENCRYPTION_BEGIN;
import static net.atcore.Messages.MessagesManager.sendMessageConsole;

public class SimulateOnlineMode {

    private final Encrypt keyGenerator;

    public SimulateOnlineMode() {
        super();
        try {
            keyGenerator = ServiceSection.getEncrypt();
            registerEvents();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Method encryptMethod;
    private static Method encryptKeyMethod;


    @Getter
    private static final HashMap<String, InetAddress> ips = new HashMap<>();
    private static final HashMap<String, String> verifyTokens = new HashMap<>();
    private static final HashMap<String, Verification> listUUIDPremium = new HashMap<>();

    public void registerEvents(){
        /*
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(AviaTerraCore.getInstance(),PacketType.values()
        ){
            @Override
            public void onPacketReceiving(PacketEvent event) {
                Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&6Cliente " + "Names " + event.getPacketType()));
                Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&6datos " + event.getPacket().getModifier().getValues().toString()));
                Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&6datos " + event.getPacket().getMeta("")));

            }
            @Override
            public void onPacketSending(PacketEvent event) {
                Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&bServer " + "Names " + event.getPacketType()));
                Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&bdatos " + event.getPacket().getModifier().getValues().toString()));
                Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&bdatos " + event.getPacket().getModifier().getTarget().toString()));
            }
        });*/

        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(AviaTerraCore.getInstance(), PacketType.Login.Server.SUCCESS) {
            @Override
            public void onPacketSending(PacketEvent event) {
                if (Bukkit.getOnlineMode())return;
                event.setCancelled(true);
                /*try {

                }finally {
                    event.getPlayer().kickPlayer("Inicio de exitoso exitoso");
                }*/

                PacketContainer packet = event.getPacket();

                String name = packet.getGameProfiles().read(0).getName();
                Verification verification = listUUIDPremium.get(name);

                GameProfile gameProfile = new GameProfile(verification.getId(), name);
                gameProfile.getProperties().put("textures", new Property("textures", verification.getProperties()[0].getValue(), verification.getProperties()[0].getSignature()));
                WrappedGameProfile wrappedProfile = WrappedGameProfile.fromHandle(gameProfile);
                PacketContainer loginSuccessPacket = new PacketContainer(PacketType.Login.Server.SUCCESS);
                loginSuccessPacket.getGameProfiles().write(0, wrappedProfile);
                loginSuccessPacket.getBooleans().write(0, true);

                ProtocolLibrary.getProtocolManager().sendServerPacket(event.getPlayer(), loginSuccessPacket, false);
                listUUIDPremium.remove(name);
            }
        });

        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(AviaTerraCore.getInstance(), PacketType.Login.Client.ENCRYPTION_BEGIN) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                if (Bukkit.getOnlineMode()) return;
                event.setCancelled(true);

                PacketContainer packet = event.getPacket();
                byte[] encryptedSharedSecret = packet.getByteArrays().read(0);
                byte[] encryptedToken = packet.getByteArrays().read(1);

                // Aquí deberías descifrar el secreto compartido y el token
                try {
                    // Descifrar usando la clave privada del servidor
                    byte[] sharedSecret = ServiceSection.getEncrypt().decryptData(encryptedSharedSecret);
                    byte[] token =  ServiceSection.getEncrypt().decryptData(encryptedToken);

                    // Verificar el token
                    if (verifyTokens.containsKey(Arrays.toString(token))) {
                        String key = verifyTokens.get(Arrays.toString(token));
                        verifyTokens.remove(Arrays.toString(token));

                        String name = key.split("\\|")[0];
                        String ip = key.split("\\|")[1];
                        ////////////////////////////////////////

                        String serverId = generateServerId("",keyGenerator.getPublicKey(),  sharedSecret);
                        Player player = event.getPlayer();

                        MojangResolver resolver = AviaTerraCore.getResolver();

                        Optional<Verification> response;
                        try {
                            response = resolver.hasJoined(name, serverId, InetAddress.getByName("localhost"));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        response.ifPresent(verification ->{

                            if (ServiceSection.getEncrypt().encryptConnection(verification, name) && event.getPlayer().getAddress().toString().equals(ip)) {

                                FakeStartPacket(verification.getName(), verification.getId(), player);
                                sendMessageConsole("ok", TypeMessages.SUCCESS);
                                enableEncryption(new SecretKeySpec(sharedSecret, "AES"), event.getPlayer());

                                String userName = verification.getName();
                                listUUIDPremium.put(userName, verification);
                            }
                        } );

                    }else{
                        Bukkit.getConsoleSender().sendMessage("no son iguales");
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

            }
        });

        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(AviaTerraCore.getInstance(),
                PacketType.Login.Client.START){
            @Override
            public void onPacketReceiving(PacketEvent event) {
                if (Bukkit.getOnlineMode()) return;

                UUID uuid = event.getPacket().getUUIDs().read(0);
                String name = event.getPacket().getStrings().read(0);

                SessionLogin session = LoginManager.getListSession().get(name);
                if (session == null || session.getEndTime() < System.currentTimeMillis()) {
                    switch (LoginManager.isPremium(name)){
                        case PREMIUM -> {
                            event.setCancelled(true);
                            StartLoginPremium(name, uuid, event.getPacket(), event.getPlayer());
                        }
                        case CRACKED -> StartLoginCracked(name, uuid);
                        case UNKNOWN -> {
                            PacketContainer kickPaket1 = new PacketContainer(PacketType.Login.Server.DISCONNECT);
                            try {
                                ProtocolLibrary.getProtocolManager().sendServerPacket(event.getPlayer(), kickPaket1);
                            }finally {
                                event.getPlayer().kickPlayer("Disconnect");
                            }
                            kickPaket1.getChatComponents().write(0, WrappedChatComponent.fromText("Error Al contectar"));

                        }
                    }

                }
            }
        });
    }



    private void StartLoginCracked(String name, UUID uuid) {

    }

    private void StartLoginPremium(String name, UUID uuid, PacketContainer packet, Player sender) {

        String ip = sender.getAddress().getAddress().getHostAddress();
        //core.addLoginAttempt(ip, username); //lista de login pendientes

        //byte[] verify = verifyTokens.get()

        for (String nameAndIp : verifyTokens.keySet()) {

            if (!Objects.equals(nameAndIp.split("\\|")[0], name))continue;

            //ClientPublicKey clientKey = source.getClientKey();

            /*
            BukkitLoginSession playerSession = new BukkitLoginSession(username, verify, clientKey, registered, profile);
            plugin.putSession(sender.getAddress(), playerSession);
            synchronized (packetEvent.getAsyncMarker().getProcessingLock()) {
                packetEvent.setCancelled(true);
            }
            val profileKey = packet.getOptionals(BukkitConverters.getWrappedPublicKeyDataConverter())
                    .optionRead(0);


            if (profileKey.flatMap(Function.identity()).isPresent()){
                WrappedProfilePublicKey.WrappedProfileKeyData profile = profileKey.flatMap(Function.identity()).get();
                System.out.println("Key Data: " + Arrays.toString(profile.getKey().getEncoded()));
                try {
                    if (verifyClientKey(profile.getExpireTime(), profile.getSignature(), profile.getKey(), Instant.now(), uuid)){
                        Bukkit.getConsoleSender().sendMessage("ok");
                    }else {
                        Bukkit.getConsoleSender().sendMessage("error");
                    }

                } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException | IOException | InvalidKeySpecException e) {
                    throw new RuntimeException(e);
                }
            }else {
                Bukkit.getConsoleSender().sendMessage("error 2");
            }*/



            PacketContainer packetEncryption = new PacketContainer(PacketType.Login.Server.ENCRYPTION_BEGIN);
            // Genera el token
            byte[] token = new byte[4];
            new java.security.SecureRandom().nextBytes(token);

            verifyTokens.put(Arrays.toString(token), name + "|" + sender.getAddress().toString());

            packetEncryption.getByteArrays().write(0, keyGenerator.getPublicKey().getEncoded());
            packetEncryption.getByteArrays().write(1, token);

            packetEncryption.getBooleans().write(0, true);

            ProtocolLibrary.getProtocolManager().sendServerPacket(sender, packetEncryption);
            break;
        }


    }

    public static boolean verifyClientKey(Instant instant , byte[] signature , PublicKey clientKey, Instant verifyTimestamp, UUID premiumId)
            throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, IOException, InvalidKeySpecException {
        if (!verifyTimestamp.isBefore(instant)) {
            throw new RuntimeException("timestamp is before instant " + instant);
        }

        Signature verifier = Signature.getInstance("SHA1withRSA");

        verifier.initVerify(loadMojangSessionKey());
        verifier.update(toSignable(instant ,clientKey, premiumId));
        return verifier.verify(signature);
    }

    private static PublicKey loadMojangSessionKey()
            throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        val keyUrl = AviaTerraCore.class.getClassLoader().getResource("yggdrasil_session_pubkey.der");
        val keyData = Resources.toByteArray(keyUrl);
        val keySpec = new X509EncodedKeySpec(keyData);

        return KeyFactory.getInstance("RSA").generatePublic(keySpec);
    }

    private static byte[] toSignable(Instant instant ,PublicKey publicKey, UUID ownerPremiumId) {

        byte[] keyData = publicKey.getEncoded();

        UUID playerUUID = UUID.randomUUID();
        Instant timestamp = Instant.now();

        ByteBuffer buffer = ByteBuffer.allocate(publicKey.getEncoded().length + 16 + 8); // 16 bytes para UUID, 8 bytes para timestamp
        buffer.putLong(playerUUID.getMostSignificantBits());
        buffer.putLong(playerUUID.getLeastSignificantBits());
        buffer.putLong(timestamp.toEpochMilli());
        buffer.put(publicKey.getEncoded());

        //byte[] dataToSign = buffer.array();
        return buffer.array();
    }

    private static String generateServerId(String serverId, PublicKey publicKey, byte[] sharedSecret) {

        Hasher hasher = Hashing.sha1().newHasher();
        hasher.putBytes(serverId.getBytes(StandardCharsets.ISO_8859_1));
        hasher.putBytes(sharedSecret);
        hasher.putBytes(publicKey.getEncoded());

        byte[] serverHash = hasher.hash().asBytes();
        return (new BigInteger(serverHash)).toString(16);
    }

    private static void FakeStartPacket(String username, UUID uuid, Player player) {
//        PacketContainer packetProtocol = new PacketContainer(PacketType.Handshake.Client.SET_PROTOCOL);
//        packetProtocol.getIntegers().write(0,767);
//        packetProtocol.getStrings().write(0,Bukkit.getIp().toString().split(":")[0]);
//        packetProtocol.getIntegers().write(1, Bukkit.getPort());
//        packetProtocol.getProtocols().write(0, PacketType.Protocol.LOGIN);

        //ProtocolLibrary.getProtocolManager().receiveClientPacket(player, packetProtocol, false);

        PacketContainer startPacket = new PacketContainer(START);
        startPacket.getStrings().write(0, username);
        startPacket.getUUIDs().write(0, uuid);

        ProtocolLibrary.getProtocolManager().receiveClientPacket(player, startPacket, false);
    }

    private static final String ENCRYPTION_CLASS_NAME = "MinecraftEncryption";
    private static Method cipherMethod;

    private boolean enableEncryption(SecretKey loginKey, Player player) throws IllegalArgumentException {
        Bukkit.getConsoleSender().sendMessage("Enabling onlinemode encryption for " + player.getAddress());
        // Initialize method reflections
        if (encryptKeyMethod == null || encryptMethod == null) {
            Class<?> networkManagerClass = MinecraftReflection.getNetworkManagerClass();
            try {
                encryptKeyMethod = FuzzyReflection.fromClass(networkManagerClass)
                        .getMethodByParameters("a", SecretKey.class);
            } catch (IllegalArgumentException exception) {
                encryptMethod = FuzzyReflection.fromClass(networkManagerClass)
                        .getMethodByParameters("a", Cipher.class, Cipher.class);

                Class<?> encryptionClass = MinecraftReflection.getMinecraftClass(
                        "util." + ENCRYPTION_CLASS_NAME, ENCRYPTION_CLASS_NAME
                );

                cipherMethod = FuzzyReflection.fromClass(encryptionClass)
                        .getMethodByParameters("a", int.class, Key.class);
            }
        }

        try {
            Object networkManager = this.getNetworkManager(player);

            if (encryptKeyMethod != null) {
                encryptKeyMethod.invoke(networkManager, loginKey);
            } else {
                // Create ciphers from login key
                Object decryptionCipher = cipherMethod.invoke(null, Cipher.DECRYPT_MODE, loginKey);
                Object encryptionCipher = cipherMethod.invoke(null, Cipher.ENCRYPT_MODE, loginKey);

                encryptMethod.invoke(networkManager, decryptionCipher, encryptionCipher);
            }
        } catch (Exception ex) {
            Bukkit.getConsoleSender().sendMessage(ex.getMessage());
            return false;
        }

        return true;
    }

    private Object getNetworkManager(Player player) throws ClassNotFoundException {
        NettyChannelInjector injectorContainer = (NettyChannelInjector) Accessors.getMethodAccessorOrNull(
                TemporaryPlayerFactory.class, "getInjectorFromPlayer", Player.class
        ).invoke(null, player);

        FieldAccessor accessor = Accessors.getFieldAccessorOrNull(
                NettyChannelInjector.class, "networkManager", Object.class
        );
        return accessor.get(injectorContainer);
    }
}
