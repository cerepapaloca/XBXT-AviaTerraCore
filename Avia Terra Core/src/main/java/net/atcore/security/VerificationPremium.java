package net.atcore.security;

import com.comphenix.protocol.events.PacketContainer;
import com.github.games647.craftapi.model.auth.Verification;
import com.github.games647.craftapi.resolver.MojangResolver;
import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;
import net.atcore.AviaTerraCore;
import net.atcore.messages.CategoryMessages;
import net.atcore.messages.Message;
import net.atcore.messages.MessagesType;
import net.atcore.security.Login.model.LoginData;
import net.atcore.security.Login.model.SessionData;
import net.atcore.security.Login.LoginManager;
import net.atcore.security.Login.StateLogins;
import net.atcore.security.Login.SimulateOnlineMode;
import net.atcore.utils.GlobalUtils;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

import static net.atcore.messages.MessagesManager.sendMessageConsole;
import static net.atcore.security.Login.SimulateOnlineMode.LIST_UUID_PREMIUM;
import static net.atcore.security.Login.SimulateOnlineMode.MAP_TOKENS;

public class VerificationPremium {

    public static void checkPremium(PacketContainer packet, Player player) {
        byte[] encryptedSharedSecret = packet.getByteArrays().read(0);
        byte[] encryptedToken = packet.getByteArrays().read(1);

        if (player.getAddress() == null) throw new IllegalThreadStateException("retorno nulo la conexión del paquete");
        InetAddress inetAddress = player.getAddress().getAddress();
        ////* Aquí comienza la valides del usuario *////
        try {
            // Descifrar usando la clave privada del servidor
            byte[] sharedSecret = SecuritySection.getEncryptService().decryptData(encryptedSharedSecret);
            byte[] token =  SecuritySection.getEncryptService().decryptData(encryptedToken);

            // verificar el token sean iguales
            if (MAP_TOKENS.containsKey(Arrays.toString(token))) {
                SimulateOnlineMode.BasicDataPlayer key = MAP_TOKENS.get(Arrays.toString(token));
                MAP_TOKENS.remove(Arrays.toString(token));

                //se obtiene los datos
                String name = key.getName();
                String ip = key.getIP().getHostAddress();
                String uuid = key.getUuid().toString();
                ////////////////////////////////////////

                // Se crea el serverID a partir de la llave secreta y la llave publicá
                String serverId = generateServerId(SecuritySection.getEncryptService().getPublicKey(), sharedSecret);
                MojangResolver resolver = AviaTerraCore.getResolver();
                Optional<Verification> response;
                // Activa el protocolo de encriptación de minecraft. Más información en https://wiki.vg/Protocol_Encryption
                if (SimulateOnlineMode.enableEncryption(new SecretKeySpec(sharedSecret, "AES"), player)) {
                    // Se investiga en la base de datos en mojang para saber si está logueado.
                    response = resolver.hasJoined(name, serverId, inetAddress);
                    if (response.isPresent()){//Se mira la base de datos
                        Verification verification = response.get();
                        if (checkNameAndUUID(verification, name, uuid)) {// Mira si son iguales las uuid premium
                            if (ip.equals(inetAddress.getHostAddress())){// Mira si la ip son la misma ip
                                // Se envía un paquete falso al servidor para que siga con el protocolo
                                SimulateOnlineMode.FakeStartPacket(verification.getName(), verification.getId(), player);
                                sendMessageConsole(String.format(Message.LOGIN_PREMIUM_VALIDATION_OK.getMessage(), name), MessagesType.SUCCESS, CategoryMessages.LOGIN);
                                String userName = verification.getName();
                                LIST_UUID_PREMIUM.put(userName, verification);
                                LoginData loginData = LoginManager.getDataLogin(userName);
                                SessionData session = new SessionData(player, StateLogins.PREMIUM, inetAddress);
                                session.setSharedSecret(sharedSecret);
                                session.setEndTimeLogin(-1);// Por seguridad los jugadores premium no puede tener sesiones
                                loginData.setSession(session);
                                loginData.setLimbo(null);
                                LoginManager.updateLoginDataBase(name, inetAddress);
                                //LoginManager.checkLoginIn(player, false);//esto para que siga el protocolo
                            }else{
                                GlobalUtils.kickPlayer(player, Message.LOGIN_KICK_PREMIUM_VALIDATION.getMessage());
                                sendMessageConsole(String.format(Message.LOGIN_PREMIUM_VALIDATION_FAILED_LOG_0.getMessage(), name, inetAddress), MessagesType.WARNING, CategoryMessages.LOGIN);
                            }
                        }else {
                            GlobalUtils.kickPlayer(player, Message.LOGIN_KICK_PREMIUM_VALIDATION.getMessage());
                            sendMessageConsole(String.format(Message.LOGIN_PREMIUM_VALIDATION_FAILED_LOG_1.getMessage(), name, inetAddress), MessagesType.WARNING, CategoryMessages.LOGIN);
                        }
                    }else{
                        GlobalUtils.kickPlayer(player, Message.LOGIN_KICK_PREMIUM_VALIDATION.getMessage());
                        sendMessageConsole(String.format(Message.LOGIN_PREMIUM_VALIDATION_FAILED_LOG_2.getMessage(), name, inetAddress), MessagesType.WARNING, CategoryMessages.LOGIN);
                    }
                }else {
                    GlobalUtils.kickPlayer(player, Message.LOGIN_KICK_PREMIUM_VALIDATION.getMessage());
                    sendMessageConsole(String.format(Message.LOGIN_PREMIUM_VALIDATION_FAILED_LOG_3.getMessage(), name, inetAddress), MessagesType.WARNING, CategoryMessages.LOGIN);
                }
            }else{
                GlobalUtils.kickPlayer(player, Message.LOGIN_KICK_PREMIUM_VALIDATION.getMessage());
                sendMessageConsole(String.format(Message.LOGIN_PREMIUM_VALIDATION_FAILED_LOG_4.getMessage(), inetAddress.toString()), MessagesType.WARNING, CategoryMessages.LOGIN);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings({"deprecation", "UnstableApiUsage"})
    private static @NotNull String generateServerId(@NotNull PublicKey publicKey, byte[] sharedSecret) {
        Hasher hasher = Hashing.sha1().newHasher();
        hasher.putBytes("".getBytes(StandardCharsets.ISO_8859_1));
        hasher.putBytes(sharedSecret);
        hasher.putBytes(publicKey.getEncoded());

        byte[] serverHash = hasher.hash().asBytes();
        return (new BigInteger(serverHash)).toString(16);
    }

    public static boolean checkNameAndUUID(@NotNull Verification verification, String name, String uuid) {
        String realUsername = verification.getName();
        String realUUID = verification.getId().toString();
        return realUsername != null && Objects.equals(name, realUsername) && Objects.equals(realUUID, uuid);
    }

}
