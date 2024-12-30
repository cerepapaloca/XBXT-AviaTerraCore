package net.atcore.command.commnads;

import net.atcore.AviaTerraCore;
import net.atcore.command.BaseCommand;
import net.atcore.command.ModeTabPlayers;
import net.atcore.command.UseArgs;
import net.atcore.messages.Message;
import net.atcore.messages.MessagesManager;
import net.atcore.messages.TypeMessages;
import net.atcore.moderation.ban.ContextBan;
import net.atcore.moderation.ModerationSection;
import net.atcore.security.Login.*;
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
                new UseArgs("/login")
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
                DataLogin dataLogin = LoginManager.getDataLogin(player);
                if (dataLogin.getRegister().getPasswordShaded() != null) {
                    if (isUUID(args[0])){
                        if (TwoFactorAuth.checkCode(player, args[0])) {
                            if (LoginManager.checkLoginIn(player)) {
                                sendMessage(player, Message.COMMAND_LOGIN_ALREADY, TypeMessages.ERROR);
                                return;
                            }
                            startPlay(player);
                        }else {
                            fail(player);
                        }
                    }else {
                        if (LoginManager.isEqualPassword(player.getName(), args[0])){
                            if (LoginManager.checkLoginIn(player)) {
                                sendMessage(player, Message.COMMAND_LOGIN_ALREADY, TypeMessages.ERROR);
                                return;
                            }
                            startPlay(player);
                        }else{
                            fail(player);
                        }
                    }
                }else {
                    sendMessage(player, Message.COMMAND_LOGIN_NO_REGISTER, TypeMessages.ERROR);
                }
            }else {
                sendMessage(player, Message.COMMAND_LOGIN_MISSING_ARGS, TypeMessages.ERROR);
            }
        }
    }

    private void startPlay(Player player) {
        startPlaySessionCracked(player);
        attempts.remove(player.getUniqueId());
        LoginManager.updateLoginDataBase(player.getName(), Objects.requireNonNull(player.getAddress()).getAddress());
        player.updateCommands();
        MessagesManager.sendTitle(player,Message.COMMAND_LOGIN_SUCCESSFUL_TITLE.getMessage(),
                String.format(Message.COMMAND_LOGIN_SUCCESSFUL_SUBTITLE.getMessage(), player.getDisplayName()),
                20, 20*3, 40, TypeMessages.INFO);
        sendMessage(player, Message.COMMAND_LOGIN_SUCCESSFUL_CHAT, TypeMessages.SUCCESS);
    }

    private void fail(Player player) {
        int i = attempts.getOrDefault(player.getUniqueId(), 0);
        attempts.put(player.getUniqueId(), ++i);
        if (i >= 5) AviaTerraCore.getInstance().enqueueTaskAsynchronously(() -> ModerationSection.getBanManager().banPlayer(player,
                Message.COMMAND_LOGIN_BANNED.getMessage(),
                1000*60*5,
                ContextBan.GLOBAL,
                "Servidor"));
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
