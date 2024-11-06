package net.atcore.command.Commnads;

import net.atcore.AviaTerraPlayer;
import net.atcore.Section;
import net.atcore.command.BaseCommand;
import net.atcore.inventory.InventorySection;
import net.atcore.inventory.inventors.ManipulatorInventory;
import net.atcore.messages.TypeMessages;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.PlayerInventory;

import static net.atcore.messages.MessagesManager.sendMessage;

public class seeInventoryCommand extends BaseCommand {

    public seeInventoryCommand() {
        super("seeInventory",
                "/seeInventory <Jugador>",
                true,
                "puede ver y modificar el inventario del jugador a tiempo real y de manera remota"
        );
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof Player player) {
            if (args.length <= 1) {
                Player vitim = Bukkit.getPlayer(args[0]);
                if (vitim != null) {
                    //PlayerInventory inv = vitim.getInventory();
                    //Inventory inventory = Bukkit.createInventory(player, 54, "seeInventory");
                    //inventory.setContents(inv.getContents());

                    AviaTerraPlayer atp = AviaTerraPlayer.getPlayer(player);
                    atp.setManipulatedInventoryPlayer(vitim);
                    player.openInventory(InventorySection.MANIPULATOR.getBaseInventors().createInventory(atp));
                    atp.setInventorySection(InventorySection.MANIPULATOR);
                    AviaTerraPlayer atp2 = AviaTerraPlayer.getPlayer(vitim);
                    atp2.setInventorySection(InventorySection.MANIPULATED);
                    atp2.getManipulatorInventoryPlayer().add(player);
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
