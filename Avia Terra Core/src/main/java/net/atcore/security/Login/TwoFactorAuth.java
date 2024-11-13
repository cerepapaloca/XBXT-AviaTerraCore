package net.atcore.security.Login;

import lombok.Getter;
import lombok.experimental.UtilityClass;
import net.atcore.AviaTerraCore;
import net.atcore.exception.ConnedDataBaseMainThread;
import net.atcore.messages.MessagesManager;
import net.atcore.messages.TypeMessages;
import net.atcore.utils.GlobalUtils;
import net.dv8tion.jda.api.entities.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.*;

import static net.atcore.messages.MessagesManager.sendMessage;

@UtilityClass
public class TwoFactorAuth {

    private final String EMAIL = "cagutierrezpayares@gmail.com"; //no me doxeén porfi
    private final String PASSWORD = "mpqw kpbl pary qfsw"; //esto si lo puede doxear pro que una contraseña que puede cambiar un click
    @Getter
    private static final HashMap<UUID, CodeAuth> codes = new HashMap<>();

    public void sendVerificationEmail(String recipientEmail, CodeAuth code, FormatMessage formatMessage) {
        if (Bukkit.isPrimaryThread()){
            throw new ConnedDataBaseMainThread("No usar el hilo principal para el envió de emails");
        }
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(EMAIL, PASSWORD);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(EMAIL));
            message.setRecipients(
                    Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject("Código de verificación en dos pasos de AviaTerra");
            message.setContent(String.format(formatMessage.gmail, GlobalUtils.getPlayer(code.getUuidPlayer()).getName(), code.getCode()), "text/html; charset=utf-8");

            Transport.send(message);
            MessagesManager.sendMessageConsole("código de verificación a " + recipientEmail, TypeMessages.INFO);

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    public static void sendVerificationDiscord(String id, Player player, FormatMessage formatMessage) {
        if (Bukkit.isPrimaryThread()){
            throw new ConnedDataBaseMainThread("No usar el hilo principal para el envió de mensajes de discord");
        }
        User user = AviaTerraCore.BOT_DISCORD.retrieveUserById(id).complete();
        user.openPrivateChannel()
                .flatMap(channel -> channel.sendMessage(String.format(formatMessage.discord,
                        player.getName(), TwoFactorAuth.getCodes().get(player.getUniqueId()).getCode())))
                .queue(success -> sendMessage(player, "Revise su discord ya tuvo que haber llegado el mensaje", TypeMessages.SUCCESS));
        MessagesManager.sendMessageConsole("código de verificación a " + id, TypeMessages.INFO);
    }

    public boolean checkCode(Player player, String code) {
        if (TwoFactorAuth.getCodes().containsKey(player.getUniqueId())) {
            CodeAuth codeAuth = TwoFactorAuth.getCodes().get(player.getUniqueId());
            if (codeAuth.getUuidPlayer().equals(player.getUniqueId())) {
                if (codeAuth.getExpires() > System.currentTimeMillis()) {
                    if (code.equals(codeAuth.getCode().toString())) {
                        return true;
                    }else {
                        sendMessage(player, "El código no son iguales", TypeMessages.ERROR);
                    }
                }else {
                    sendMessage(player, "El código ya expiro", TypeMessages.ERROR);
                    TwoFactorAuth.getCodes().remove(player.getUniqueId());
                }
            }else {
                sendMessage(player, "hubo una discrepancia vuelve a enviar un nuevo código", TypeMessages.ERROR);
            }
        }else {
            sendMessage(player, "no tienes un código usa el <|/link|> para tener uno", TypeMessages.ERROR);
        }
        return false;
    }
}
