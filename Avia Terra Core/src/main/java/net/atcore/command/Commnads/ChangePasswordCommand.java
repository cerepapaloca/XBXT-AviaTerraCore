package net.atcore.command.Commnads;

import net.atcore.AviaTerraCore;
import net.atcore.command.BaseCommand;
import net.atcore.command.ModeAutoTab;
import net.atcore.data.DataBaseRegister;
import net.atcore.messages.CategoryMessages;
import net.atcore.messages.TypeMessages;
import net.atcore.security.Login.LoginManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static net.atcore.messages.MessagesManager.sendMessage;
import static net.atcore.messages.MessagesManager.sendMessageConsole;

public class ChangePasswordCommand extends BaseCommand {

    public ChangePasswordCommand(){
        super("changePassword",
                "/changePassword <Tu Contraseña> <Nueva Contraseña>",
                "*",
                false,
                "cambias tu contras ",
                ModeAutoTab.NONE
        );
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length > 1) {
            if (sender instanceof Player player) {
                if (LoginManager.isEqualPassword(player.getName(), args[0])){
                    String password = LoginManager.hashPassword(player.getName(), args[1]);
                    Bukkit.getScheduler().runTaskAsynchronously(AviaTerraCore.getInstance(), () ->
                            DataBaseRegister.updatePassword(player.getName(), password));
                    LoginManager.getDataLogin(player).getRegister().setPasswordShaded(password);
                    sendMessage(sender, "La contraseña se cambio correctamente", TypeMessages.SUCCESS);
                    sendMessageConsole("el jugador " + player.getName() + " se cambio la contraseña", TypeMessages.INFO, CategoryMessages.LOGIN);
                }else{
                    sendMessage(sender, "Contraseña incorrecta", TypeMessages.ERROR);
                }
            }else{
                sendMessage(sender, "solo para jugadores", TypeMessages.ERROR);
            }
        }else{
            sendMessage(sender, this.getUsage(), TypeMessages.ERROR);
        }
    }
}
