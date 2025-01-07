package net.atcore.command.commnads;

import net.atcore.AviaTerraCore;
import net.atcore.command.BaseCommand;
import net.atcore.command.ArgumentUse;
import net.atcore.messages.Message;
import net.atcore.messages.MessagesManager;
import net.atcore.messages.MessagesType;
import net.atcore.moderation.ban.ContextBan;
import net.atcore.moderation.ModerationSection;
import net.atcore.security.Login.*;
import net.atcore.security.Login.model.LoginData;
import net.atcore.utils.GlobalUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

import static net.atcore.messages.MessagesManager.sendMessage;
import static net.atcore.security.Login.LoginManager.startPlaySessionCracked;

public class LoginCommand extends BaseCommand {

    public LoginCommand() {
        super("login",
                new ArgumentUse("/login")
                        .addNote("contraseña"),
                "**",
                "Te logueas"
        );
    }

    private final HashMap<UUID, Integer> attempts = new HashMap<>();

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof Player player) {
            if (args.length > 0) {
                LoginData loginData = LoginManager.getDataLogin(player);
                if (loginData.getRegister().getStateLogins() == StateLogins.PREMIUM){
                    MessagesManager.sendMessage(player, Message.COMMAND_REGISTER_IS_PREMIUM, MessagesType.ERROR);
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
                        if (LoginManager.isEqualPassword(player.getName(), args[0])){
                            preStartPlay(player, loginData);
                        }else{
                            fail(player);
                        }
                    }
                }else {
                    sendMessage(player, Message.COMMAND_LOGIN_NO_REGISTER, MessagesType.ERROR);
                }
            }else {
                sendMessage(player, Message.COMMAND_LOGIN_MISSING_ARGS, MessagesType.ERROR);
            }
        }
    }

    private void preStartPlay(Player player, LoginData loginData) {
        if (LoginManager.checkLoginIn(player)){
            sendMessage(player, Message.COMMAND_LOGIN_ALREADY, MessagesType.ERROR);
            return;
        }
        try {
            if (!loginData.isLimboMode()) {
                throw new RuntimeException("El jugador no esta modo limbo");
            }else {
                startPlay(player);
            }
        }catch (Exception e) {
            GlobalUtils.kickPlayer(player, Message.LOGIN_KICK_GENERIC.getMessage());
            MessagesManager.sendWaringException("Error al iniciar el modo play", e);
        }

    }

    private void startPlay(Player player) {
        startPlaySessionCracked(player);
        attempts.remove(player.getUniqueId());
        LoginManager.updateLoginDataBase(player.getName(), Objects.requireNonNull(player.getAddress()).getAddress());
        MessagesManager.sendTitle(player, Message.COMMAND_LOGIN_SUCCESSFUL_TITLE.getMessage(),
                String.format(Message.COMMAND_LOGIN_SUCCESSFUL_SUBTITLE.getMessage(), player.getDisplayName()),
                20, 20*3, 40, MessagesType.INFO);
        sendMessage(player, Message.COMMAND_LOGIN_SUCCESSFUL_CHAT, MessagesType.SUCCESS);
    }

    private void fail(Player player) {
        int i = attempts.getOrDefault(player.getUniqueId(), 0);
        attempts.put(player.getUniqueId(), ++i);
        if (i >= 5) AviaTerraCore.getInstance().enqueueTaskAsynchronously(() -> ModerationSection.getBanManager().banPlayer(player,
                Message.COMMAND_LOGIN_BANNED.getMessage(),
                1000*60*5,
                ContextBan.GLOBAL,
                Message.BAN_AUTHOR_AUTO_BAN.getMessage()));
        GlobalUtils.kickPlayer(player, Message.COMMAND_LOGIN_NO_EQUAL_PASSWORD.getMessage());
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
