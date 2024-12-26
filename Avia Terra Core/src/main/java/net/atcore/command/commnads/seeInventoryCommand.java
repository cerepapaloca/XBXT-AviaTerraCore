package net.atcore.command.commnads;

import net.atcore.aviaterraplayer.AviaTerraPlayer;
import net.atcore.command.BaseCommand;
import net.atcore.command.ModeAutoTab;
import net.atcore.inventory.InventorySection;
import net.atcore.messages.Message;
import net.atcore.messages.TypeMessages;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static net.atcore.messages.MessagesManager.sendMessage;

public class seeInventoryCommand extends BaseCommand {

    public seeInventoryCommand() {
        super("seeInventory",
                "/seeInventory <Jugador>",
                "Puedes ver y modificar el inventario del jugador a tiempo real y de manera remota",
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
                    if (atp2.getModerationPlayer().getManipulatedInventoryPlayer() == null){
                        AviaTerraPlayer atp = AviaTerraPlayer.getPlayer(player);
                        atp.getModerationPlayer().setManipulatedInventoryPlayer(vitim.getUniqueId());
                        player.openInventory(InventorySection.MANIPULATOR.getBaseInventory().createInventory(atp));
                        atp.setInventorySection(InventorySection.MANIPULATOR);

                        atp2.setInventorySection(InventorySection.MANIPULATED);
                        atp2.getModerationPlayer().getManipulatorInventoryPlayer().add(player.getUniqueId());
                    }else {
                        sendMessage(sender, Message.COMMAND_SEE_INVENTORY_ERROR.getMessage(), TypeMessages.ERROR);
                    }
                }else{
                    sendMessage(sender, Message.COMMAND_GENERIC_PLAYER_NOT_FOUND.getMessage(), TypeMessages.ERROR);
                }
            }else {
                sendMessage(sender, Message.COMMAND_SEE_INVENTORY_MISSING_ARGS.getMessage(), TypeMessages.ERROR);
            }
        }else{
            sendMessage(sender, Message.COMMAND_GENERIC_NO_PLAYER.getMessage(), TypeMessages.ERROR);
        }
    }
}
