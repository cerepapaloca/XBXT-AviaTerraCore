package net.atcore.baseCommand.Commnads;

import net.atcore.baseCommand.BaseCommand;
import net.atcore.messages.TypeMessages;
import net.atcore.security.Login.LoginManager;
import org.bukkit.command.CommandSender;

import static net.atcore.messages.MessagesManager.sendMessage;

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
