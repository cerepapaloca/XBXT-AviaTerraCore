package net.atcore.command.commnads;

import net.atcore.AviaTerraCore;
import net.atcore.command.BaseTabCommand;
import net.atcore.data.sql.DataBaseRegister;
import net.atcore.messages.CategoryMessages;
import net.atcore.messages.Message;
import net.atcore.messages.MessagesManager;
import net.atcore.messages.TypeMessages;
import net.atcore.security.Login.LoginManager;
import net.atcore.security.Login.TwoFactorAuth;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

import static net.atcore.messages.MessagesManager.sendMessage;
import static net.atcore.messages.MessagesManager.sendMessageConsole;

public class ChangePasswordCommand extends BaseTabCommand {

    public ChangePasswordCommand(){
        super("changePassword",
                "/changePassword <Contraseña_|_Código> <Nueva_Contraseña>",
                "*",
                "cambias tu contraseña"
        );
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length > 1) {
            if (sender instanceof Player player) {
                String password = LoginManager.hashPassword(player.getName(), args[1]);
                if (isUUID(args[0])){
                    if (TwoFactorAuth.checkCode(player, args[0])){
                        onChange(player, password, "código");
                    }
                }else {
                    if (LoginManager.isEqualPassword(player.getName(), args[0])){
                        onChange(player, password, "contraseña");
                    }else{
                        sendMessage(sender, Message.COMMAND_CHANGE_PASSWORD_NOT_EQUAL_PASSWORD, TypeMessages.ERROR);
                    }
                }
            }else{
                sendMessage(sender, Message.COMMAND_GENERIC_NO_PLAYER, TypeMessages.ERROR);
            }
        }else{
            sendMessage(sender, this.getUsage(), TypeMessages.ERROR);
        }
    }

    private static void onChange(Player player, String password, String reason) {
        LoginManager.getDataLogin(player).getRegister().setPasswordShaded(password);
        AviaTerraCore.getInstance().enqueueTaskAsynchronously(() -> {
            if (DataBaseRegister.updatePassword(player.getName(), password)){
                sendMessage(player, Message.COMMAND_CHANGE_PASSWORD_SUCCESSFUL, TypeMessages.SUCCESS);
            }else {
                sendMessage(player, Message.COMMAND_CHANGE_PASSWORD_ERROR, TypeMessages.ERROR);
            }
        });

        sendMessageConsole( String.format(Message.COMMAND_CHANGE_PASSWORD_SUCCESSFUL_LOG.getMessage(), player.getName(), reason), TypeMessages.INFO, CategoryMessages.LOGIN);
        TwoFactorAuth.getCodes().remove(player.getUniqueId());
    }

    public static boolean isUUID(String str) {
        try {
            UUID.fromString(str);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    @Override
    public List<String> onTab(CommandSender sender, String[] args) {
        switch (args.length) {
            case 1 -> {
                return List.of("TuContraseñaActual", "xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx");
            }
            case 2 -> {
                return List.of("NuevaContraseña");
            }
            default -> {
                return List.of();
            }
        }
    }
}
