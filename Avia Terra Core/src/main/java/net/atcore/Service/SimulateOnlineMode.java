package net.atcore.Service;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.*;
import com.comphenix.protocol.injector.netty.channel.NettyChannelInjector;
import com.comphenix.protocol.injector.temporary.TemporaryPlayerFactory;
import com.comphenix.protocol.reflect.FuzzyReflection;
import com.comphenix.protocol.reflect.accessors.Accessors;
import com.comphenix.protocol.reflect.accessors.FieldAccessor;
import com.comphenix.protocol.utility.MinecraftReflection;
import com.comphenix.protocol.wrappers.*;
import com.github.games647.craftapi.model.auth.Verification;
import com.github.games647.craftapi.model.skin.Textures;
import lombok.Getter;
import net.atcore.AviaTerraCore;
import net.atcore.Messages.TypeMessages;
import net.atcore.Security.Login.LoginManager;
import net.atcore.Security.Login.DataSession;
import net.atcore.Security.VerificationPremium;
import net.atcore.Utils.GlobalUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import javax.crypto.*;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.security.*;
import java.util.*;

import static com.comphenix.protocol.PacketType.Login.Client.START;
import static net.atcore.Messages.MessagesManager.sendMessageConsole;

public class SimulateOnlineMode {

    public SimulateOnlineMode() {
        super();
        try {
            registerEvents();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Method encryptMethod;
    private static Method encryptKeyMethod;


    @Getter
    private static final HashMap<String, InetAddress> ips = new HashMap<>();
    public static final HashMap<String, String> verifyTokens = new HashMap<>();
    public static final HashMap<String, Verification> listUUIDPremium = new HashMap<>();

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

        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(AviaTerraCore.getInstance(), PacketType.Login.Server.SUCCESS) {
            @Override
            public void onPacketSending(PacketEvent event) {
                PacketContainer packet = event.getPacket();
                String name = packet.getGameProfiles().read(0).getName();
            }
        });

        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(AviaTerraCore.getInstance(), PacketType.Login.Client.ENCRYPTION_BEGIN) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                event.setCancelled(true);//se cancela por qué si no el servidor le tira error al cliente por enviar un paquete que no debería

                PacketContainer packet = event.getPacket();
                Player player = event.getPlayer();

                if (player.getAddress() == null){
                    GlobalUtils.kickPlayer(event.getPlayer(), "Error de connexion vuele a intentar");
                    return;
                }

                VerificationPremium.checkPremium(packet, player);
            }
        });

        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(AviaTerraCore.getInstance(),
                PacketType.Login.Client.START){
            @Override
            public void onPacketReceiving(PacketEvent event) {

                Player player = event.getPlayer();

                if (player.getAddress() == null){
                    GlobalUtils.kickPlayer(player, "Error de connexion vuele a intentar");
                    return;
                }

                UUID uuid = event.getPacket().getUUIDs().read(0);
                String name = event.getPacket().getStrings().read(0);

                DataSession session = LoginManager.getListSession().get(name);
                if (session == null || session.getEndTimeLogin() < System.currentTimeMillis()) {
                    switch (LoginManager.getState(player.getAddress().getAddress() ,name)){//revisa entre las sesiones o los registro del los jugadores
                        case PREMIUM -> {
                            Bukkit.getLogger().warning("premium");
                            event.setCancelled(true);//se cancela por que asi el servidor no se da cuenta que a recibido un paquete
                            StartLoginPremium(name, uuid, player);
                        }
                        case CRACKED -> {
                            Bukkit.getLogger().warning("cracked");
                            StartLoginCracked(name, uuid);
                        }
                        case UNKNOWN -> {
                            GlobalUtils.kickPlayer(player, "Error de connexion vuele a intentar");
                            Bukkit.getLogger().warning("unknown");
                        }
                    }

                }
            }
        });
    }

    private void StartLoginCracked(String name, UUID uuid) {

    }

    private void StartLoginPremium(String name, UUID uuid, Player sender) {

        PacketContainer packetEncryption = new PacketContainer(PacketType.Login.Server.ENCRYPTION_BEGIN);
        byte[] token = new byte[4];
        new java.security.SecureRandom().nextBytes(token);

        verifyTokens.put(Arrays.toString(token), name + "|" + sender.getAddress().toString() + "|" + uuid);

        packetEncryption.getByteArrays().write(0, ServiceSection.getEncrypt().getPublicKey().getEncoded());
        packetEncryption.getByteArrays().write(1, token);

        packetEncryption.getBooleans().write(0, true);

        ProtocolLibrary.getProtocolManager().sendServerPacket(sender, packetEncryption);
    }

    public void applySkin(Player player) {
        if (listUUIDPremium.containsKey(player.getName())){
            String skinData = listUUIDPremium.get(player.getName()).getProperties()[0].getValue();
            String signature = listUUIDPremium.get(player.getName()).getProperties()[0].getSignature();
            listUUIDPremium.remove(player.getName());
            WrappedGameProfile gameProfile = WrappedGameProfile.fromPlayer(player);
            WrappedSignedProperty skin = WrappedSignedProperty.fromValues(Textures.KEY, skinData, signature);
            gameProfile.getProperties().put(Textures.KEY, skin);
        }
    }

    /**
     * Esto envía un paquete falso al mismo servidor para que el servidor crea se está unido un
     * jugador por qué el paquete original fue cancelado al recibir el paquete {@code PacketType.Login.Client.START}
     * y para que el servidor siga con el protocolo se envía de nuevo
     */

    public static void FakeStartPacket(String username, UUID uuid, Player player) {
        PacketContainer startPacket = new PacketContainer(START);
        startPacket.getStrings().write(0, username);
        startPacket.getUUIDs().write(0, uuid);

        ProtocolLibrary.getProtocolManager().receiveClientPacket(player, startPacket, false);
    }

    private static final String ENCRYPTION_CLASS_NAME = "MinecraftEncryption";
    private static Method cipherMethod;

    /**
     * Se encarga de que el servidor cambien de modo offline a modo online para algunos usuarios
     * este method lo encontré en por hay ósea no tengo ni idea como funciona solo que llama otro
     * method dentro del servidor por eso es mejor no tocar
     * @param loginKey Este es secreto compartido entre servidor y el cliente se llama asi por solo lo
     *                 sabe esos dos
     * @param player El jugador que le va a afectar el modo online
     * @return da true cuando esta bien false si dio un error
     */

    public static boolean enableEncryption(SecretKey loginKey, Player player) throws IllegalArgumentException {
        sendMessageConsole("Se inicio cifrado", TypeMessages.INFO);
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
}
