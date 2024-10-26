package net.atcore.BaseCommand.Commnads;

import com.google.common.base.Charsets;
import net.atcore.BaseCommand.BaseCommand;
import net.atcore.Messages.TypeMessages;
import net.atcore.Security.Login.LoginManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

import static net.atcore.Messages.MessagesManager.sendMessage;

public class CommandPrueba extends BaseCommand {

    public CommandPrueba() {
        super("prueba",//Aquí va el comando que va ejecutar
                "/prueba",//esto es una información extra de como tiene que ejecutar un comando
                "aviaterra.command.prueba", //el permiso o los permisos suele comenzar con el nombre del plugin luego categoría y por último el comando
                true,//indica si el comando esta oculto para evitar que aparezca en una documentación o algo así
                "es un comando de prueba"//La descripción del comando
        );
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        sendMessage(sender, "Hola Mundo!", TypeMessages.SUCCESS);
        LoginManager.getDataLogin().forEach(login -> {
            sendMessage(sender,"name: <|" + login.getSession().getPlayer().getName() + "|> " + "state: <|" + login.getSession().getState().name() + "|>", TypeMessages.INFO);
        });
    }

}
