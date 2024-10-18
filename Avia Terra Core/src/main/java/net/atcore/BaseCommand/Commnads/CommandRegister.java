package net.atcore.BaseCommand.Commnads;

import net.atcore.BaseCommand.BaseCommand;
import net.atcore.Messages.TypeMessages;
import net.atcore.Security.Login.LoginManager;
import org.apache.commons.collections4.BagUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;

import static net.atcore.Messages.MessagesManager.sendMessage;

public class CommandRegister extends BaseCommand {

    public CommandRegister() {
        super("register",
                "/register <contraseña> <contraseña>",
                "aviaterra.command.login",
                false,
                "Te registras"
        );
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof Player player){
            if (args.length >= 2){
                if (Objects.equals(args[0], args[1])){
                    LoginManager.addPassword(player.getName(), args[1]);
                    sendMessage(player, "la contraseña se guardo exitosamente y registraste exitosamente", TypeMessages.ERROR);
                }else{
                    sendMessage(player, "las contra seña no son iguales", TypeMessages.ERROR);
                }
            }else{
                sendMessage(player, "tiene que escribir la contraseña de nuevo", TypeMessages.ERROR);
            }

        }
    }
}
