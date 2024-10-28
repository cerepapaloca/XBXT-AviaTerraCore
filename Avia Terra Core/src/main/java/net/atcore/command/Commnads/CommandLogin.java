package net.atcore.command.Commnads;

import net.atcore.command.BaseCommand;
import net.atcore.messages.TypeMessages;
import net.atcore.security.Login.*;
import net.atcore.utils.GlobalUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static net.atcore.messages.MessagesManager.COLOR_ESPECIAL;
import static net.atcore.messages.MessagesManager.sendMessage;
import static net.atcore.security.Login.LoginManager.startPlaySessionCracked;

public class CommandLogin extends BaseCommand {

    public CommandLogin() {
        super("login",
                "/login <contraseña>",
                false,
                "Te logueas"
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
                    player.sendTitle(ChatColor.translateAlternateColorCodes('&',COLOR_ESPECIAL + "Te haz logueado!"), "", 20, 20*3, 40);
                    sendMessage(player, "Has iniciado session exitosamente", TypeMessages.SUCCESS);
                }else{
                    GlobalUtils.kickPlayer(player, "contraseña incorrecta, vuele a intentarlo");
                }
            }else {
                sendMessage(player, "No estas registrado usa el /register", TypeMessages.ERROR);
            }
        }
    }
}
