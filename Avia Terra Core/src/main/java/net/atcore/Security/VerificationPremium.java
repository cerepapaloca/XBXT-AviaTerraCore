package net.atcore.Security;

import com.comphenix.protocol.events.PacketContainer;
import com.github.games647.craftapi.model.auth.Verification;
import com.github.games647.craftapi.resolver.MojangResolver;
import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;
import net.atcore.AviaTerraCore;
import net.atcore.Exception.ExceptionPackageConnection;
import net.atcore.Messages.CategoryMessages;
import net.atcore.Messages.TypeMessages;
import net.atcore.Security.Login.DataSession;
import net.atcore.Security.Login.StateLogins;
import net.atcore.Service.ServiceSection;
import net.atcore.Service.SimulateOnlineMode;
import net.atcore.Utils.GlobalUtils;
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

import static net.atcore.Messages.MessagesManager.sendMessageConsole;
import static net.atcore.Service.SimulateOnlineMode.listUUIDPremium;
import static net.atcore.Service.SimulateOnlineMode.verifyTokens;

public class VerificationPremium {

    public static void checkPremium(PacketContainer packet, Player player) {
        byte[] encryptedSharedSecret = packet.getByteArrays().read(0);
        byte[] encryptedToken = packet.getByteArrays().read(1);

        if (player.getAddress() == null) throw new ExceptionPackageConnection("retorno nulo la conexión del paquete");

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

                //se investiga en la base de datos en mojang para saber si esta logueado.
                response = resolver.hasJoined(name, serverId, InetAddress.getByName("localhost"));
                if (response.isPresent()){//Se mira la base de datos
                    Verification verification = response.get();
                    if (checkNameAndUUID(verification, name, uuid)) {//mira si son iguales
                        if (player.getAddress().toString().equals(ip)){//mira si la ip son las misma
                            SimulateOnlineMode.FakeStartPacket(verification.getName(), verification.getId(), player);//se envía un paquete falso al servidor
                            sendMessageConsole("Certificación del premíum valida del jugador <|" +           //para que siga con el protocolo
                                    name + "|>", TypeMessages.SUCCESS, CategoryMessages.LOGIN);
                            //Activa el protocolo de encriptación de minecraft. Más información en https://wiki.vg/Protocol_Encryption
                            if (SimulateOnlineMode.enableEncryption(new SecretKeySpec(sharedSecret, "AES"), player)){
                                String userName = verification.getName();
                                new DataSession(name, GlobalUtils.getUUIDByName(name), verification.getId(), StateLogins.PREMIUM);
                                listUUIDPremium.put(userName, verification);
                            }else{
                                GlobalUtils.kickPlayer(player, "hubo un error. Reinicie su cliente");
                                sendMessageConsole("hubo un error al activar el protocolo de encriptación por el jugador <|"
                                        + player.getName() + "|> y la ip <|" + player.getAddress().toString() +
                                        "|>. Discrepancia detectada", TypeMessages.ERROR, CategoryMessages.LOGIN);
                            }
                        }else{
                            GlobalUtils.kickPlayer(player, "Se detecto una discrepancia. Reinicie su cliente");
                            sendMessageConsole("la ip que se envío el paquete no es la misma que se envío al primer paquete por el jugador <|"
                                    + player.getName() + "|> y la ip <|" + player.getAddress().toString() +
                                    "|>. Discrepancia detectada", TypeMessages.WARNING, CategoryMessages.LOGIN);
                        }
                    }else {
                        GlobalUtils.kickPlayer(player, "Se detecto una discrepancia. Reinicie su cliente");
                        sendMessageConsole("Los datos dados por mojang no concuerda el jugador <|" + player.getName() + "|> " +
                                "y la ip <|" + player.getAddress().toString() + "|>. Discrepancia detectada", TypeMessages.WARNING, CategoryMessages.LOGIN);
                    }
                }else{
                    GlobalUtils.kickPlayer(player, "Se detecto una discrepancia. Reinicie su cliente");
                    sendMessageConsole("No se encontró registrado del jugador en mojang por el jugador <|" + player.getName() + "|> " +
                            "y la ip <|" + player.getAddress().toString() + "|>. Discrepancia detectada", TypeMessages.WARNING, CategoryMessages.LOGIN);
                }
            }else{
                GlobalUtils.kickPlayer(player, "Se detecto una discrepancia. Reinicie su cliente");
                sendMessageConsole("tokens no iguales por el jugador <|" + player.getName() + "|> " +
                        "y la ip <|" + player.getAddress().toString() + "|>. Discrepancia detectada", TypeMessages.WARNING, CategoryMessages.LOGIN);
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
