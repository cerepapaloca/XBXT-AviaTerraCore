package net.atcore.security.Login;

import java.lang.reflect.Method;
import java.net.InetAddress;
import java.security.Key;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;

import lombok.RequiredArgsConstructor;
import net.atcore.security.Login.model.LoginData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType;
import static com.comphenix.protocol.PacketType.Login.Client.START;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.injector.netty.channel.NettyChannelInjector;
import com.comphenix.protocol.injector.temporary.TemporaryPlayerFactory;
import com.comphenix.protocol.reflect.FuzzyReflection;
import com.comphenix.protocol.reflect.accessors.Accessors;
import com.comphenix.protocol.reflect.accessors.FieldAccessor;
import com.comphenix.protocol.utility.MinecraftReflection;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedSignedProperty;
import com.github.games647.craftapi.model.auth.Verification;
import com.github.games647.craftapi.model.skin.Textures;

import lombok.Getter;
import net.atcore.Config;
import net.atcore.messages.CategoryMessages;
import net.atcore.messages.Message;
import static net.atcore.messages.MessagesManager.sendMessageConsole;
import net.atcore.messages.MessagesType;
import net.atcore.security.AntiBot;
import net.atcore.security.SecuritySection;
import net.atcore.security.VerificationPremium;
import net.atcore.utils.GlobalUtils;

public class SimulateOnlineMode {


    @Getter
    private static final HashMap<String, InetAddress> IPS = new HashMap<>();
    public static final HashMap<String, BasicDataPlayer> MAP_TOKENS = new HashMap<>();
    public static final HashMap<String, Verification> LIST_UUID_PREMIUM = new HashMap<>();

    public void startEncryption(Player player, PacketContainer packet){

        if (player.getAddress() == null){
            GlobalUtils.kickPlayer(player, Message.LOGIN_KICK_GENERIC.getMessage());
            return;
        }

        VerificationPremium.checkPremium(packet, player);
    }

    public boolean preStartLogin(Player player, PacketContainer packet){

        if (player.getAddress() == null){
            GlobalUtils.kickPlayer(player, Message.LOGIN_KICK_GENERIC.getMessage());
            return false;
        }
        InetAddress inetAddress = player.getAddress().getAddress();

        UUID uuid = packet.getUUIDs().read(0);
        String name = packet.getStrings().read(0);
        if (AntiBot.checkBot(inetAddress, name)){
            return true;
        }

        LoginData loginData = LoginManager.getDataLogin(name);
        // En caso que no tenga una sesión se le expiro se hace una nueva
        if ((loginData == null || loginData.getSession() == null) || loginData.getSession().getEndTimeLogin() < System.currentTimeMillis()) {
            // Lo registra si no esta registrado
            StateLogins state = LoginManager.getStateAndRegister(inetAddress ,name);
            switch (Config.getServerMode()){
                case OFFLINE_MODE -> state = StateLogins.CRACKED;
                case ONLINE_MODE -> state = StateLogins.PREMIUM;
            }
            switch (state){
                case PREMIUM -> startLoginPremium(name, uuid, player);
                case UNKNOWN -> GlobalUtils.kickPlayer(player, Message.LOGIN_KICK_GENERIC.getMessage());
            }
            sendMessageConsole(String.format(Message.LOGIN_START_PROTOCOL_LOG.getMessage(), state.name().toLowerCase(), name, inetAddress.getHostName(), inetAddress.getHostAddress()), MessagesType.INFO, CategoryMessages.LOGIN);
            return state == StateLogins.PREMIUM; //se cancela por que asi el servidor no se da cuenta de que a recibido un paquete
        }
        return false;
    }

    private void startLoginPremium(String name, UUID uuid, Player sender) {
        PacketContainer packetEncryption = new PacketContainer(PacketType.Login.Server.ENCRYPTION_BEGIN);
        byte[] token = new byte[4];
        new java.security.SecureRandom().nextBytes(token);

        MAP_TOKENS.put(Arrays.toString(token), new BasicDataPlayer(sender.getAddress().getAddress(), name, uuid));

        packetEncryption.getByteArrays().write(0, SecuritySection.getEncryptService().getPublicKey().getEncoded());
        packetEncryption.getByteArrays().write(1, token);

        packetEncryption.getBooleans().write(0, true);
        // se envía el paquete de inicio de cifrado esto es para que el cliente se prepara para comenzar el cifrado y seguir con el protocolo
        ProtocolLibrary.getProtocolManager().sendServerPacket(sender, packetEncryption);
    }

    public void applySkin(Player player) {
        if (LIST_UUID_PREMIUM.containsKey(player.getName())){
            String skinData = LIST_UUID_PREMIUM.get(player.getName()).getProperties()[0].getValue();
            String signature = LIST_UUID_PREMIUM.get(player.getName()).getProperties()[0].getSignature();
            LIST_UUID_PREMIUM.remove(player.getName());
            WrappedGameProfile gameProfile = WrappedGameProfile.fromPlayer(player);
            WrappedSignedProperty skin = WrappedSignedProperty.fromValues(Textures.KEY, skinData, signature);
            gameProfile.getProperties().put(Textures.KEY, skin);
        }
    }

    /**
     * Esto envía un paquete falso al mismo servidor para que el servidor crea se está unido un
     * jugador por qué el paquete original fue cancelado al recibir el paquete {@code PacketType.Login.Client.START}
     * y para que el servidor siga con el protocolo
     */

    public static void FakeStartPacket(String username, UUID uuid, Player player) {
        PacketContainer startPacket = new PacketContainer(START);
        startPacket.getStrings().write(0, username);
        startPacket.getUUIDs().write(0, uuid);
        ProtocolLibrary.getProtocolManager().receiveClientPacket(player, startPacket, false);
    }

    private static final String ENCRYPTION_CLASS_NAME = "MinecraftEncryption";
    private static Method cipherMethod;
    private static Method encryptMethod;
    private static Method encryptKeyMethod;

    /**
     * Se encarga de que el servidor cambien de modo offline a modo online para algunos usuarios
     * este method lo encontré en por hay ósea no tengo ni idea como funciona solo que llama otro
     * method dentro del servidor por eso es mejor no tocar. Ni sé de donde saco esto la verdad
     * @param loginKey Este es secreto compartido entre servidor y el cliente
     * @param player El jugador que le va a afectar el modo online
     * @return da true cuando esta bien false si dio un error
     */

    public static boolean enableEncryption(SecretKey loginKey, Player player) throws IllegalArgumentException {
        sendMessageConsole("Se inicio cifrado", MessagesType.INFO);
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
            Object networkManager = getNetworkManager(player);

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

    private static Object getNetworkManager(Player player) {
        NettyChannelInjector injectorContainer = (NettyChannelInjector) Accessors.getMethodAccessorOrNull(
                TemporaryPlayerFactory.class, "getInjectorFromPlayer", Player.class
        ).invoke(null, player);

        FieldAccessor accessor = Accessors.getFieldAccessorOrNull(
                NettyChannelInjector.class, "networkManager", Object.class
        );
        return accessor.get(injectorContainer);
    }

    @Getter
    @RequiredArgsConstructor
    public static class BasicDataPlayer {

        private final InetAddress IP;
        private final String name;
        private final UUID uuid;
    }
}
