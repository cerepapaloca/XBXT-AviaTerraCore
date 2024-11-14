package net.atcore.command.Commnads;

import net.atcore.command.BaseCommand;
import net.atcore.command.ModeAutoTab;
import net.atcore.messages.MessagesManager;
import net.atcore.messages.TypeMessages;
import net.atcore.security.Login.*;
import net.atcore.utils.GlobalUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static net.atcore.messages.MessagesManager.COLOR_ESPECIAL;
import static net.atcore.messages.MessagesManager.sendMessage;
import static net.atcore.security.Login.LoginManager.startPlaySessionCracked;

public class LoginCommand extends BaseCommand {

    public LoginCommand() {
        super("login",
                "/login <contraseña>",
                "*",
                "Te logueas",
                ModeAutoTab.NONE
        );
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof Player player) {
            DataLogin dataLogin = LoginManager.getDataLogin(player);
            if (dataLogin.getRegister().getPasswordShaded() != null) {
                if (LoginManager.isEqualPassword(player.getName(), args[0])){
                    if (LoginManager.checkLoginIn(player, true)) {
                        sendMessage(player, "Ya estas logueado", TypeMessages.ERROR);
                        return;
                    }
                    startPlaySessionCracked(player);
                    LoginManager.updateLoginDataBase(player.getName(), player.getAddress().getAddress());
                    MessagesManager.sendTitle(player,"Bienvenido de vuelta", "<|&o" + player.getDisplayName() + "|>", 20, 20*3, 40, TypeMessages.INFO);
                    sendMessage(player, "Has iniciado session exitosamente", TypeMessages.SUCCESS);
                }else{
                    GlobalUtils.kickPlayer(player, "contraseña incorrecta, vuele a intentarlo. " +
                            "Si no se acuerda su contraseña y tiene un corro o un discord vinculado puede" +
                            "puede enviar un código de verificación usando <|/link <discord | gmail>|>");
                }
            }else {
                sendMessage(player, "No estas registrado usa el /register", TypeMessages.ERROR);
            }
        }
    }
}
