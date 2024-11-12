package net.atcore.command.Commnads;

import net.atcore.AviaTerraPlayer;
import net.atcore.command.BaseCommand;
import net.atcore.command.ModeAutoTab;
import net.atcore.inventory.InventorySection;
import net.atcore.messages.TypeMessages;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static net.atcore.messages.MessagesManager.sendMessage;

public class seeInventoryCommand extends BaseCommand {

    public seeInventoryCommand() {
        super("seeInventory",
                "/seeInventory <Jugador>",
                true,
                "puede ver y modificar el inventario del jugador a tiempo real y de manera remota",
                ModeAutoTab.NORMAL
        );
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof Player player) {
            if (args.length > 0) {
                Player vitim = Bukkit.getPlayer(args[0]);
                if (vitim != null) {
                    AviaTerraPlayer atp2 = AviaTerraPlayer.getPlayer(vitim);
                    if (atp2.getManipulatedInventoryPlayer() == null){
                        AviaTerraPlayer atp = AviaTerraPlayer.getPlayer(player);
                        atp.setManipulatedInventoryPlayer(vitim.getUniqueId());
                        player.openInventory(InventorySection.MANIPULATOR.getBaseInventory().createInventory(atp));
                        atp.setInventorySection(InventorySection.MANIPULATOR);

                        atp2.setInventorySection(InventorySection.MANIPULATED);
                        atp2.getManipulatorInventoryPlayer().add(player.getUniqueId());
                    }else {
                        sendMessage(sender, "No puedes ver el inventarió de otro jugador que también este mirando un inventarió", TypeMessages.ERROR);
                    }
                }else{
                    sendMessage(sender, "el jugador no existe o no esta conectado", TypeMessages.ERROR);
                }
            }else {
                sendMessage(sender, "tiene que tener el nombre del jugador", TypeMessages.ERROR);
            }
        }else{
            sendMessage(sender, "solo se puede ejecutar por jugador", TypeMessages.ERROR);
        }
    }
}
