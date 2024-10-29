package net.atcore.command.Commnads;

import net.atcore.command.BaseCommand;
import net.atcore.guns.GunsSection;
import net.atcore.messages.TypeMessages;
import net.atcore.utils.GlobalUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static net.atcore.messages.MessagesManager.sendMessage;

public class PruebaCommand extends BaseCommand {

    public PruebaCommand() {
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
        GunsSection.baseWeapons.forEach((list, baseWeapon) -> {
            GlobalUtils.addItemPlayer(baseWeapon.getItemWeapon(), (Player) sender , true, true);
        });
        GunsSection.baseChargers.forEach((list, dataCharger) -> {
            GlobalUtils.addItemPlayer(dataCharger.getItemCharger(), (Player) sender , true, true);
            GlobalUtils.addItemPlayer(dataCharger.getItemCharger(), (Player) sender , true, true);
        });
    }

}
