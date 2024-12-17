package net.atcore.command.commnads;

import net.atcore.AviaTerraCore;
import net.atcore.command.BaseTabCommand;
import net.atcore.data.sql.DataBaseRegister;
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
                        onChange(player, password, "código");
                    }
                }else {
                    if (LoginManager.isEqualPassword(player.getName(), args[0])){
                        onChange(player, password, "contraseña");
                    }else{
                        sendMessage(sender, "Contraseña incorrecta. Si no se acuerda de su contraseña y tiene un corro o un discord vinculado puede" +
                                "puede enviar un código de verificación usando <|/link <discord | gmail>|>", TypeMessages.ERROR);
                    }
                }
            }else{
                sendMessage(sender, "solo para jugadores", TypeMessages.ERROR);
            }
        }else{
            sendMessage(sender, this.getUsage(), TypeMessages.ERROR);
        }
    }

    private static void onChange(Player player, String password, String reason) {
        LoginManager.getDataLogin(player).getRegister().setPasswordShaded(password);
        AviaTerraCore.getInstance().enqueueTaskAsynchronously(() -> {
            if (DataBaseRegister.updatePassword(player.getName(), password)){
                sendMessage(player, "La contraseña se cambio correctamente", TypeMessages.SUCCESS);
            }else {
                sendMessage(player, "Hubo un error al cambiar la contraseña", TypeMessages.ERROR);
            }
        });

        sendMessageConsole( String.format("el jugador <|%s|> se cambio la contraseña con su <|%s|>", player.getName(), reason), TypeMessages.INFO, CategoryMessages.LOGIN);
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
