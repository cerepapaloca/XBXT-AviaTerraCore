package net.atcore.BaseCommand.Commnads;

import net.atcore.BaseCommand.BaseCommand;
import net.atcore.Messages.TypeMessages;
import net.atcore.Security.Login.*;
import net.atcore.Utils.GlobalUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static net.atcore.Messages.MessagesManager.COLOR_ESPECIAL;
import static net.atcore.Messages.MessagesManager.sendMessage;
import static net.atcore.Security.Login.LoginManager.startPlaySessionCracked;

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
