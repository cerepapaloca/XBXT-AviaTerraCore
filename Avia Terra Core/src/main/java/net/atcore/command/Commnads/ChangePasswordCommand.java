package net.atcore.command.Commnads;

import net.atcore.AviaTerraCore;
import net.atcore.command.BaseTabCommand;
import net.atcore.data.DataBaseRegister;
import net.atcore.messages.CategoryMessages;
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
                "/changePassword <Tu Contraseña | Código> <Nueva Contraseña>",
                "*",
                false,
                "cambias tu contras "
        );
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length > 1) {
            if (sender instanceof Player player) {
                String password = LoginManager.hashPassword(player.getName(), args[1]);
                if (isUUID(args[0])){
                    if (TwoFactorAuth.checkCode(player, args[0])){
                        LoginManager.getDataLogin(player).getRegister().setPasswordShaded(password);
                        AviaTerraCore.getInstance().enqueueTaskAsynchronously(() ->
                                DataBaseRegister.updatePassword(player.getName(), password));
                        sendMessage(sender, "La contraseña se cambio correctamente", TypeMessages.SUCCESS);
                        sendMessageConsole("el jugador <|" + player.getName() + "|> se cambio la contraseña con su <|código|>", TypeMessages.INFO, CategoryMessages.LOGIN);
                        TwoFactorAuth.getCodes().remove(player.getUniqueId());
                    }
                }else {
                    if (LoginManager.isEqualPassword(player.getName(), args[0])){
                        LoginManager.getDataLogin(player).getRegister().setPasswordShaded(password);
                        sendMessage(sender, "La contraseña se cambio correctamente", TypeMessages.SUCCESS);
                        sendMessageConsole("el jugador <|" + player.getName() + "|> se cambio la contraseña con su <|contraseña|>", TypeMessages.INFO, CategoryMessages.LOGIN);
                        TwoFactorAuth.getCodes().remove(player.getUniqueId());
                    }else{
                        sendMessage(sender, "Contraseña incorrecta. Si no se acuerda de su contraseña y tiene un corro o un discord vinculado puede" +
                                "puede enviar un código de verificación usando <|/link <discord | gmail>|>", TypeMessages.ERROR);
                    }
                    AviaTerraCore.getInstance().enqueueTaskAsynchronously(() ->
                            DataBaseRegister.updatePassword(player.getName(), password));
                }
            }else{
                sendMessage(sender, "solo para jugadores", TypeMessages.ERROR);
            }
        }else{
            sendMessage(sender, this.getUsage(), TypeMessages.ERROR);
        }
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
                return List.of("Tu Contraseña actual", "xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx");
            }
            case 2 -> {
                return List.of("Nueva Contraseña");
            }
            default -> {
                return List.of();
            }
        }
    }
}
