package net.atcore.avia.BaseCommand.Commnads;

import net.atcore.avia.BaseCommand.BaseCommand;
import net.atcore.avia.Messages.MessagesManager;
import net.atcore.avia.Messages.TypeMessages;
import org.bukkit.command.CommandSender;

public class CommandPrueba extends BaseCommand {

    public CommandPrueba() {
        super("prueba",//Aquí va el comando que va ejecutar
                "/prueba",//esto es una información extra de como tiene que ejecutar un comando
                "aviaterra.command.prueba", //el permiso los permisos suele comenzar con el nombre del plugin luego categoría y por último el comando
                true,//indica si el comando esta oculto para evitar que aparezca en una documentación o algo así
                "es un comando de prueba"//La descripción del comando
        );
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        MessagesManager.sendMessage(sender, "Hola Mundo!", TypeMessages.SUCCESS);
    }
}
