package net.atcore.security;

import com.comphenix.protocol.events.PacketContainer;
import com.github.games647.craftapi.model.auth.Verification;
import com.github.games647.craftapi.resolver.MojangResolver;
import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;
import net.atcore.AviaTerraCore;
import net.atcore.Config;
import net.atcore.messages.CategoryMessages;
import net.atcore.messages.TypeMessages;
import net.atcore.security.Login.DataLogin;
import net.atcore.security.Login.DataSession;
import net.atcore.security.Login.LoginManager;
import net.atcore.security.Login.StateLogins;
import net.atcore.service.ServiceSection;
import net.atcore.service.SimulateOnlineMode;
import net.atcore.utils.GlobalUtils;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

import static net.atcore.messages.MessagesManager.sendMessageConsole;
import static net.atcore.service.SimulateOnlineMode.listUUIDPremium;
import static net.atcore.service.SimulateOnlineMode.verifyTokens;

public class VerificationPremium {

    public static void checkPremium(PacketContainer packet, Player player) {
        byte[] encryptedSharedSecret = packet.getByteArrays().read(0);
        byte[] encryptedToken = packet.getByteArrays().read(1);

        if (player.getAddress() == null) throw new IllegalThreadStateException("retorno nulo la conexión del paquete");

        ////* Aquí comienza la valides del usuario *////
        try {
            // Descifrar usando la clave privada del servidor
            byte[] sharedSecret = ServiceSection.getEncrypt().decryptData(encryptedSharedSecret);
            byte[] token =  ServiceSection.getEncrypt().decryptData(encryptedToken);

            // verificar el token sean iguales
            if (verifyTokens.containsKey(Arrays.toString(token))) {
                String key = verifyTokens.get(Arrays.toString(token));
                verifyTokens.remove(Arrays.toString(token));

                //se obtiene los datos
                String name = key.split("\\|")[0];
                String ip = key.split("\\|")[1];
                String uuid = key.split("\\|")[2];
                ////////////////////////////////////////

                //se crea el serverID a partir de la llave secreta y la llave publica
                String serverId = generateServerId(ServiceSection.getEncrypt().getPublicKey(), sharedSecret);
                MojangResolver resolver = AviaTerraCore.getResolver();
                Optional<Verification> response;
                //Activa el protocolo de encriptación de minecraft. Más información en https://wiki.vg/Protocol_Encryption
                if (SimulateOnlineMode.enableEncryption(new SecretKeySpec(sharedSecret, "AES"), player)){
                    //se investiga en la base de datos en mojang para saber si esta logueado.
                    response = resolver.hasJoined(name, serverId, player.getAddress().getAddress());
                    if (response.isPresent()){//Se mira la base de datos
                        Verification verification = response.get();
                        if (checkNameAndUUID(verification, name, uuid)) {//mira si son iguales
                            if (ip.equals(player.getAddress().getHostName())){//mira si la ip son las misma
                                SimulateOnlineMode.FakeStartPacket(verification.getName(), verification.getId(), player);//se envía un paquete falso al servidor
                                sendMessageConsole("Certificación del premíum valida del jugador <|" +           //para que siga con el protocolo
                                        name + "|>", TypeMessages.SUCCESS, CategoryMessages.LOGIN);
                                String userName = verification.getName();
                                listUUIDPremium.put(userName, verification);
                                DataLogin dataLogin = LoginManager.getDataLogin(userName);
                                DataSession session = new DataSession(player, StateLogins.PREMIUM, player.getAddress().getAddress());
                                session.setSharedSecret(sharedSecret);
                                session.setEndTimeLogin(-1);
                                dataLogin.setSession(session);
                                //LoginManager.checkLoginIn(player, false);//esto para que siga el protocolo
                            }else{
                                GlobalUtils.kickPlayer(player, "Se detecto una discrepancia. Reinicie su cliente");
                                sendMessageConsole("la ip que se envío el paquete no es la misma que se envío al primer paquete por el jugador <|"
                                        + name + "|> y la ip <|" + player.getAddress().getHostName() +
                                        "|>. Discrepancia detectada", TypeMessages.WARNING, CategoryMessages.LOGIN);
                            }
                        }else {
                            GlobalUtils.kickPlayer(player, "Se detecto una discrepancia. Reinicie su cliente");
                            sendMessageConsole("Los datos dados por mojang no concuerda con el jugador <|" + name + "|> " +
                                    "y la ip <|" + player.getAddress().getHostName() + "|>. Discrepancia detectada", TypeMessages.WARNING, CategoryMessages.LOGIN);
                        }
                    }else{
                        GlobalUtils.kickPlayer(player, "Se detecto una discrepancia. Reinicie su cliente");
                        sendMessageConsole("No se encontró registros en mojang del jugador <|" + name + "|> " +
                                "y la ip <|" + player.getAddress().getHostName() + "|>. Discrepancia detectada", TypeMessages.WARNING, CategoryMessages.LOGIN);
                    }
                }else {
                    GlobalUtils.kickPlayer(player, "hubo un error. Reinicie su cliente");
                    sendMessageConsole("hubo un error al activar el protocolo de encriptación por el jugador <|"
                            + name + "|> y la ip <|" + player.getAddress().getHostName() +
                            "|>. Discrepancia detectada", TypeMessages.ERROR, CategoryMessages.LOGIN);
                }
            }else{
                GlobalUtils.kickPlayer(player, "Se detecto una discrepancia. Reinicie su cliente");
                sendMessageConsole("tokens no iguales del el jugador <|" + "Desconocido" + "|> " +
                        "y la ip <|" + player.getAddress().getHostName() + "|>. Discrepancia detectada", TypeMessages.WARNING, CategoryMessages.LOGIN);
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
