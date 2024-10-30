package net.atcore.command.Commnads;

import net.atcore.armament.ArmamentUtils;
import net.atcore.command.BaseCommand;
import net.atcore.armament.ArmamentSection;
import net.atcore.messages.TypeMessages;
import net.atcore.utils.GlobalUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.units.qual.A;

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
        ArmamentUtils.baseWeapons.forEach((list, baseWeapon) -> {
            GlobalUtils.addItemPlayer(baseWeapon.getItemArmament(), (Player) sender , true, true);
        });
        ArmamentUtils.baseChargers.forEach((list, dataCharger) -> {
            GlobalUtils.addItemPlayer(dataCharger.getItemArmament(), (Player) sender , true, true);
        });
        ArmamentUtils.baseAmmo.forEach((list, baseAmmo) -> {
            GlobalUtils.addItemPlayer(baseAmmo.getItemArmament(), (Player) sender , true, true);
        });
    }

}
