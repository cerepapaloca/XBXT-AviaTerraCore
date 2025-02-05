package net.atcore.command.commnads;

import net.atcore.AviaTerraCore;
import net.atcore.command.BaseTabCommand;
import net.atcore.command.ArgumentUse;
import net.atcore.data.sql.DataBaseRegister;
import net.atcore.messages.CategoryMessages;
import net.atcore.messages.Message;
import net.atcore.messages.TypeMessages;
import net.atcore.security.EncryptService;
import net.atcore.security.Login.LoginManager;
import net.atcore.security.Login.TwoFactorAuth;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

import static net.atcore.messages.MessagesManager.*;

public class ChangePasswordCommand extends BaseTabCommand {

    public ChangePasswordCommand(){
        super("changePassword",
                new ArgumentUse("changePassword").addNote("Contraseña", "Código").addNote("Nueva Contraseña"),
                "*",
                "cambias tu contraseña",
                false);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length > 1) {
            if (sender instanceof Player player) {
                String password = EncryptService.hashPassword(player.getName(), args[1]);
                if (isUUID(args[0])){
                    if (TwoFactorAuth.checkCode(player, args[0])){
                        onChange(player, password, "código");
                    }
                }else {
                    if (LoginManager.isEqualPassword(player, args[0])){
                        onChange(player, password, "contraseña");
                    }else{
                        sendMessage(sender, Message.COMMAND_CHANGE_PASSWORD_NOT_EQUAL_PASSWORD);
                    }
                }
            }else{
                sendMessage(sender, Message.COMMAND_GENERIC_NO_PLAYER);
            }
        }else{
            sendArgument(sender, this.getAviaTerraUsage(), TypeMessages.ERROR);
        }
    }

    private static void onChange(Player player, String password, String reason) {
        LoginManager.getDataLogin(player).getRegister().setPasswordShaded(password);
        AviaTerraCore.enqueueTaskAsynchronously(() -> {
            if (DataBaseRegister.updatePassword(player.getName(), password)){
                sendMessage(player, Message.COMMAND_CHANGE_PASSWORD_SUCCESSFUL);
            }else {
                sendMessage(player, Message.COMMAND_CHANGE_PASSWORD_ERROR);
            }
        });

        logConsole( String.format(Message.COMMAND_CHANGE_PASSWORD_SUCCESSFUL_LOG.getMessage(player), player.getName(), reason), TypeMessages.INFO, CategoryMessages.LOGIN);
        TwoFactorAuth.CODES.remove(player.getUniqueId());
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
