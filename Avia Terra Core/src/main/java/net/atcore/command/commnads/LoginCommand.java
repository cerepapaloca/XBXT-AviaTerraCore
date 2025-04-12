package net.atcore.command.commnads;

import net.atcore.AviaTerraCore;
import net.atcore.command.ArgumentUse;
import net.atcore.command.BaseCommand;
import net.atcore.command.CommandVisibility;
import net.atcore.messages.Message;
import net.atcore.messages.MessagesManager;
import net.atcore.messages.TypeMessages;
import net.atcore.moderation.ModerationSection;
import net.atcore.moderation.ban.ContextBan;
import net.atcore.security.login.*;
import net.atcore.security.login.model.LoginData;
import net.atcore.utils.AviaTerraScheduler;
import net.atcore.utils.GlobalUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

import static net.atcore.messages.MessagesManager.sendMessage;
import static net.atcore.security.login.LoginManager.startPlaySessionCracked;

public class LoginCommand extends BaseCommand {

    public LoginCommand() {
        super("login",
                new ArgumentUse("/login")
                        .addNote("contraseña"),
                CommandVisibility.ALL,
                "Inicias session con tu contraseña"
        );
        addAlias("log");
    }

    private final HashMap<UUID, Integer> attempts = new HashMap<>();

    @Override
    public void execute(CommandSender sender, String... args) {
        if (sender instanceof Player player) {
            if (args.length > 0 && !args[0].isEmpty()) {
                LoginData loginData = LoginManager.getDataLogin(player);
                if (loginData.getRegister().getStateLogins() == StateLogins.PREMIUM){
                    MessagesManager.sendMessage(player, Message.COMMAND_REGISTER_IS_PREMIUM);
                    return;
                }
                if (LoginManager.checkLogin(player)){
                    sendMessage(player, Message.COMMAND_LOGIN_ALREADY);
                    return;
                }
                if (loginData.getRegister().getPasswordShaded() != null) {
                    if (isUUID(args[0])){
                        if (TwoFactorAuth.checkCode(player, args[0])) {
                            preStartPlay(player, loginData);
                        }else {
                            fail(player);
                        }
                    }else {
                        if (LoginManager.isEqualPassword(player, args[0])){
                            preStartPlay(player, loginData);
                        }else{
                            fail(player);
                        }
                    }
                }else {
                    sendMessage(player, Message.COMMAND_LOGIN_NO_REGISTER);
                }
            }else {
                LimboManager.sendForm(player, ReasonLimbo.NO_SESSION, Message.COMMAND_LOGIN_MISSING_ARGS);
                sendMessage(player, Message.COMMAND_LOGIN_MISSING_ARGS);
            }
        }
    }

    private void preStartPlay(Player player, LoginData loginData) {
        TwoFactorAuth.CODES.remove(player.getUniqueId());
        try {
            if (!loginData.isLimboMode()) {
                throw new RuntimeException("El jugador no esta modo limbo");
            }else {
                startPlay(player);
            }
        }catch (Exception e) {
            GlobalUtils.kickPlayer(player, Message.LOGIN_KICK_GENERIC.getMessage(player));
            MessagesManager.sendWaringException("Error al iniciar el modo play", e);
        }

    }

    private void startPlay(Player player) {
        long codeSession = LoginManager.codeSession(player);
        AviaTerraScheduler.enqueueTaskAsynchronously(() -> {
            startPlaySessionCracked(player, codeSession);
            attempts.remove(player.getUniqueId());
            LoginManager.updateLoginDataBase(GlobalUtils.getRealName(player), Objects.requireNonNull(player.getAddress()).getAddress());
            MessagesManager.sendTitle(player, Message.COMMAND_LOGIN_SUCCESSFUL_TITLE.getMessage(player),
                    String.format(Message.COMMAND_LOGIN_SUCCESSFUL_SUBTITLE.getMessage(player), player.getName()),
                    20, 20*3, 40, TypeMessages.INFO);
            sendMessage(player, Message.COMMAND_LOGIN_SUCCESSFUL_CHAT);
        });
    }

    private void fail(Player player) {
        int i = attempts.getOrDefault(player.getUniqueId(), 0);
        attempts.put(player.getUniqueId(), ++i);
        if (i >= 5) AviaTerraScheduler.enqueueTaskAsynchronously(() -> ModerationSection.getBanManager().banPlayer(player,
                Message.COMMAND_LOGIN_BANNED.getMessage(player),
                1000*60*5,
                ContextBan.GLOBAL,
                Message.BAN_AUTHOR_AUTO_BAN.getMessage(player)));
        GlobalUtils.kickPlayer(player, Message.COMMAND_LOGIN_NO_EQUAL_PASSWORD.getMessage(player));
    }

    public static boolean isUUID(String str) {
        try {
            UUID.fromString(str);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
