package net.atcore.BaseCommand.Commnads;

import net.atcore.AviaTerraCore;
import net.atcore.BaseCommand.BaseCommand;
import net.atcore.Messages.MessagesManager;
import net.atcore.Messages.TypeMessages;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.UUID;

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
        MessagesManager.sendMessage(sender, "Hola Mundo!", TypeMessages.SUCCESS);
        ItemStack item = new ItemStack(Material.NAME_TAG);
        ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().set(new NamespacedKey(AviaTerraCore.getInstance(), "uuid"), PersistentDataType.STRING, UUID.randomUUID().toString());
        item.setItemMeta(meta);
        if (sender instanceof Player player) {
            player.getInventory().addItem(item);
        }
    }
}
