package net.atcore.security.Login;

import lombok.Setter;
import lombok.experimental.UtilityClass;
import net.atcore.AviaTerraCore;
import net.atcore.data.DataSection;
import net.atcore.data.yml.ConfigFile;
import net.atcore.messages.MessagesManager;
import net.atcore.messages.TypeMessages;
import net.atcore.security.Login.model.CodeAuth;
import net.atcore.utils.GlobalUtils;
import net.dv8tion.jda.api.entities.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.HashMap;
import java.util.Objects;
import java.util.Properties;
import java.util.UUID;

import static net.atcore.messages.Message.*;
import static net.atcore.messages.MessagesManager.sendMessage;

@UtilityClass
public class TwoFactorAuth {

    private final String EMAIL;//contacto.ceres.yt@gmail.com
    private final String PASSWORD;

    static {
        PASSWORD = Objects.requireNonNullElseGet(DataSection.getConfigFile(),  () -> {
            ConfigFile configFile = new ConfigFile();
            DataSection.setConfigFile(configFile);
            return configFile;
        }).getFileYaml().getString("email.password");
        EMAIL = DataSection.getConfigFile().getConfig().getString("email.name");
    }

    public static final HashMap<UUID, CodeAuth> CODES = new HashMap<>();

    @Setter
    private String discord;
    @Setter
    private String gmail;

    public void sendVerificationEmail(String recipientEmail, CodeAuth code, FormatMessage format) {
        if (PASSWORD == null || PASSWORD.isBlank() || EMAIL == null || EMAIL.isBlank()) return;
        if (Bukkit.isPrimaryThread()){
            throw new IllegalThreadStateException("No usar el hilo principal para el envió de emails");
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
            Player player = GlobalUtils.getPlayer(code.getUuidPlayer());
            String name = player.getName();
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(EMAIL));
            message.setRecipients(
                    Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject(LOGIN_TWO_FACTOR_SUBJECT_GMAIL.getMessage(player));
            message.setContent(String.format(gmail,
                    name,
                    format.getTitle().getMessage(player),
                    String.format(format.getSubtitle().getMessage(player), code.getCode().toString())),
                    "text/html; charset=utf-8");

            Transport.send(message);
            MessagesManager.logConsole(String.format("Se envió código de verificación a <|%1$s|> (%2$s)", recipientEmail, name), TypeMessages.INFO);

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    public static void sendVerificationDiscord(String id, Player player, FormatMessage format) {
        if (AviaTerraCore.jda == null) return;
        if (Bukkit.isPrimaryThread()){
            throw new IllegalThreadStateException("No usar el hilo principal para el envió de mensajes de discord");
        }
        CodeAuth code = CODES.get(player.getUniqueId());
        User user = AviaTerraCore.jda.retrieveUserById(id).complete();
        user.openPrivateChannel().flatMap(channel -> channel.sendMessage(
                String.format(
                discord,
                        player.getName(),
                        format.getTitle().getMessage(player),
                        String.format(format.getSubtitle().getMessage(player), code.getCode().toString())
                       )
                )
        ).queue(success -> sendMessage(player, LOGIN_TWO_FACTOR_ARRIVED_MESSAGE_DISCORD));

        MessagesManager.logConsole(String.format("Se envió código de verificación a <|%1$s|> (%2$s)", player.getName(), id), TypeMessages.INFO);
    }

    public boolean checkCode(Player player, String code) {
        if (CODES.containsKey(player.getUniqueId())) {
            CodeAuth codeAuth = CODES.get(player.getUniqueId());
            if (codeAuth.getUuidPlayer().equals(player.getUniqueId())) {
                if (codeAuth.getExpires() > System.currentTimeMillis()) {
                    if (code.equals(codeAuth.getCode().toString())) {
                        return true;
                    }else {
                        sendMessage(player, LOGIN_TWO_FACTOR_CODE_NO_EQUAL);
                    }
                }else {
                    sendMessage(player, LOGIN_TWO_FACTOR_EXPIRE_CODE);
                    CODES.remove(player.getUniqueId());
                }
            }else {
                sendMessage(player, LOGIN_TWO_FACTOR_UUID_NO_EQUAL);
            }
        }else {
            sendMessage(player, LOGIN_TWO_FACTOR_NO_FOUND_CODE);
        }
        return false;
    }

    public enum MediaAuth {
        DISCORD,
        GMAIL
    }
}
