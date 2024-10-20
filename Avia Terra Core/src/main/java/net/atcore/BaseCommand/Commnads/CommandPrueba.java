package net.atcore.BaseCommand.Commnads;

import com.google.common.base.Charsets;
import net.atcore.BaseCommand.BaseCommand;
import net.atcore.Messages.TypeMessages;
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
        sendMessage(sender, "es premium: " + isPremium((Player) sender), TypeMessages.SUCCESS);
    }

    public boolean isPremium(Player player) {
        // La UUID de un jugador en modo offline se basa en el nombre del jugador.
        String offlineUUID = UUID.nameUUIDFromBytes(("OfflinePlayer:" + player.getName()).getBytes(Charsets.UTF_8)).toString();

        // Comparar la UUID del jugador actual con la UUID generada para un jugador en modo offline.
        return !player.getUniqueId().toString().equals(offlineUUID);
    }
}
