package net.atcore.command.commnads;

import net.atcore.AviaTerraCore;
import net.atcore.command.BaseTabCommand;
import net.atcore.command.CommandUtils;
import net.atcore.command.ArgumentUse;
import net.atcore.data.sql.DataBaseRegister;
import net.atcore.messages.Message;
import net.atcore.messages.MessagesType;
import net.atcore.security.Login.*;
import net.atcore.security.Login.model.CodeAuth;
import net.atcore.security.Login.model.LoginData;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

import static net.atcore.messages.MessagesManager.sendMessage;

public class LinkCommand extends BaseTabCommand {
    public LinkCommand() {
        super("link",
                new ArgumentUse("link")
                        .addArg("gmail", "discord")
                        .addNote("Cuenta"),
                "**",
                "Vinculas una cuenta para que mayor seguridad en el servidor y tener la capacidad de iniciar session con la autenticación de 2 pasos",
                false);
    }

    private static final long EXPIRATION_TIME = 1000*60*2;

    @Override
    public void execute(CommandSender sender, String[] args) throws Exception {
        if (sender instanceof Player player) {
            UUID uuid = player.getUniqueId();
            LoginData loginData = LoginManager.getDataLogin(player);
            if (args.length == 0) {
                sendMessage(sender, Message.COMMAND_LINK_MISSING_ARGS.getMessage(player), MessagesType.ERROR);
                return;
            }
            switch (args[0].toLowerCase()) {
                case "gmail" -> {
                    if (args.length >= 2) {
                        if (LoginManager.checkLoginIn(player)){
                            if (args[1].contains("@")){
                                CodeAuth codeAuth = new CodeAuth(UUID.randomUUID(),
                                        System.currentTimeMillis()+(EXPIRATION_TIME),
                                        uuid,
                                        args[1].toLowerCase(),
                                        TwoFactorAuth.MediaAuth.GMAIL
                                );
                                TwoFactorAuth.CODES.put(uuid, codeAuth);

                                sendMessage(sender,String.format(Message.COMMAND_LINK_SEND_GMAIL_1.getMessage(player), args[1]), MessagesType.INFO);
                                if (loginData.getRegister().getGmail() != null){
                                    sendMessage(sender, Message.COMMAND_LINK_ALREADY_GMAIL, MessagesType.WARNING);
                                }
                                AviaTerraCore.enqueueTaskAsynchronously(() -> {
                                    TwoFactorAuth.sendVerificationEmail(args[1], codeAuth, FormatMessage.LINK);
                                    sendMessage(sender, Message.COMMAND_LINK_ARRIVED_MESSAGE_GMAIL, MessagesType.SUCCESS);
                                });
                            }else {
                                sendMessage(sender, Message.COMMAND_LINK_MISSING_ARGS_GMAIL, MessagesType.ERROR);
                            }
                        }else {
                            sendMessage(sender, Message.COMMAND_LINK_GMAIL_NO_LOGIN, MessagesType.ERROR);
                        }
                    }else {
                        if (loginData.getRegister().getGmail() != null){
                            CodeAuth codeAuth = new CodeAuth(UUID.randomUUID(),
                                    System.currentTimeMillis()+(EXPIRATION_TIME),
                                    uuid,
                                    loginData.getRegister().getGmail(),
                                    TwoFactorAuth.MediaAuth.GMAIL
                            );
                            TwoFactorAuth.CODES.put(uuid, codeAuth);
                            sendMessage(sender, Message.COMMAND_LINK_SEND_GMAIL_2, MessagesType.INFO);
                            AviaTerraCore.enqueueTaskAsynchronously(() -> {
                                TwoFactorAuth.sendVerificationEmail(loginData.getRegister().getGmail(), codeAuth, FormatMessage.CODE);
                                sendMessage(sender, Message.COMMAND_LINK_ARRIVED_MESSAGE_GMAIL, MessagesType.SUCCESS);
                            });
                        }else {
                            sendMessage(sender, Message.COMMAND_LINK_NOT_FOUND_GMAIL, MessagesType.ERROR);
                        }
                    }
                }
                case "discord" -> {
                    if (args.length >= 2) {
                        if (LoginManager.checkLoginIn(player)){
                            if (args[1].length() == 18){

                                CodeAuth codeAuth = new CodeAuth(UUID.randomUUID(),
                                        System.currentTimeMillis()+(EXPIRATION_TIME),
                                        uuid,
                                        args[1].toLowerCase(),
                                        TwoFactorAuth.MediaAuth.DISCORD
                                );
                                TwoFactorAuth.CODES.put(uuid, codeAuth);

                                sendMessage(sender, Message.COMMAND_LINK_SEND_DISCORD_1, MessagesType.INFO);
                                if (loginData.getRegister().getDiscord() != null){
                                    sendMessage(sender, Message.COMMAND_LINK_ALREADY_DISCORD, MessagesType.WARNING);
                                }
                                AviaTerraCore.getInstance().enqueueTaskAsynchronously(() -> TwoFactorAuth.sendVerificationDiscord(args[1], player, FormatMessage.LINK));
                            }else {
                                sendMessage(player, Message.COMMAND_LINK_MISSING_ARGS_DISCORD, MessagesType.ERROR);
                            }
                        }else {
                            sendMessage(sender, Message.COMMAND_LINK_DISCORD_NO_LOGIN, MessagesType.ERROR);
                        }
                    }else {
                        if (loginData.getRegister().getDiscord() != null){

                            CodeAuth codeAuth = new CodeAuth(UUID.randomUUID(),
                                    System.currentTimeMillis()+(EXPIRATION_TIME),
                                    uuid,
                                    loginData.getRegister().getDiscord(),
                                    TwoFactorAuth.MediaAuth.DISCORD
                            );
                            TwoFactorAuth.CODES.put(uuid, codeAuth);

                            sendMessage(sender, Message.COMMAND_LINK_SEND_DISCORD_2, MessagesType.INFO);
                            AviaTerraCore.getInstance().enqueueTaskAsynchronously(() ->
                                    TwoFactorAuth.sendVerificationDiscord(loginData.getRegister().getDiscord(), player, FormatMessage.CODE));
                        }else {
                            sendMessage(sender, Message.COMMAND_LINK_NOT_FOUNT_DISCORD, MessagesType.ERROR);
                        }
                    }
                }
                default -> {
                    CodeAuth codeAuth = TwoFactorAuth.CODES.get(uuid);
                    if (codeAuth != null && TwoFactorAuth.checkCode(player, args[0])){
                        switch (codeAuth.getMediaAuth()){
                            case DISCORD -> AviaTerraCore.getInstance().enqueueTaskAsynchronously(() -> {
                                if (DataBaseRegister.updateDiscord(player.getName(), TwoFactorAuth.CODES.get(uuid).getMedia())){
                                    LoginData login = LoginManager.getDataLogin(player);
                                    login.getRegister().setDiscord(TwoFactorAuth.CODES.get(uuid).getMedia());
                                    sendMessage(player, Message.COMMAND_LINK_SUCCESSFUL, MessagesType.SUCCESS);
                                }else {
                                    sendMessage(player, Message.COMMAND_LINK_ERROR, MessagesType.ERROR);
                                }
                                TwoFactorAuth.CODES.remove(uuid);
                            });
                            case GMAIL -> AviaTerraCore.getInstance().enqueueTaskAsynchronously(() -> {
                                if (DataBaseRegister.updateGmail(player.getName(), TwoFactorAuth.CODES.get(uuid).getMedia())){
                                    LoginData login = LoginManager.getDataLogin(player);
                                    login.getRegister().setGmail(TwoFactorAuth.CODES.get(uuid).getMedia());
                                    sendMessage(player, Message.COMMAND_LINK_SUCCESSFUL, MessagesType.SUCCESS);
                                }else {
                                    sendMessage(player, Message.COMMAND_LINK_ERROR, MessagesType.ERROR);
                                }
                                TwoFactorAuth.CODES.remove(uuid);
                            });
                        }
                    }else {
                        sendMessage(sender, Message.LOGIN_TWO_FACTOR_NO_FOUND_CODE, MessagesType.ERROR);
                    }
                }
            }
        }else {
            sendMessage(sender, Message.COMMAND_GENERIC_NO_PLAYER, MessagesType.ERROR);
        }
    }

    @Override
    public List<String> onTab(CommandSender sender, String[] args) {
        switch (args.length){
            case 1 -> {
                return CommandUtils.listTab(args[0], List.of("gmail", "discord", "xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx"));
            }
            case 2 -> {
                switch (args[0].toLowerCase()){
                    case "discord" -> {
                        return List.of("##################");
                    }
                    case "gmail" -> {
                        return List.of("TuCorreo@gmail.com");
                    }
                }
            }
        }
        return null;
    }
}