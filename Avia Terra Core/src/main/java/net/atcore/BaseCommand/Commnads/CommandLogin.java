package net.atcore.BaseCommand.Commnads;

import net.atcore.BaseCommand.BaseCommand;
import net.atcore.Messages.TypeMessages;
import net.atcore.Security.Login.DataSession;
import net.atcore.Security.Login.LoginManager;
import net.atcore.Security.Login.StateLogins;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import static net.atcore.Messages.MessagesManager.sendMessage;

public class CommandLogin extends BaseCommand {

    public CommandLogin() {
        super("login",
                "/login <contraseña>",
                "aviaterra.command.login",
                false,
                "Te logueas"
        );
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof Player player) {
            try {
                if (LoginManager.isEqualPassword(player.getName(), args[0])){
                    if (LoginManager.isLoginIn(player, false)){
                        DataSession session = new DataSession(player.getName(), player.getUniqueId(), StateLogins.CRACKED);
                        session.setEndTimeLogin(System.currentTimeMillis() + 1000*60);
                        session.setIp(player.getAddress().getAddress());
                        sendMessage(player, "Has iniciado session exitosamente", TypeMessages.SUCCESS);
                    }else {
                        sendMessage(player, "Ya estas logueado", TypeMessages.ERROR);
                    }
                }else{
                    sendMessage(player, "las contraseña no es igual", TypeMessages.ERROR);
                }
            } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
