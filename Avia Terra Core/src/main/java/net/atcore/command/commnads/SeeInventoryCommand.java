package net.atcore.command.commnads;

import net.atcore.aviaterraplayer.AviaTerraPlayer;
import net.atcore.command.BaseCommand;
import net.atcore.command.CommandVisibility;
import net.atcore.command.ModeTabPlayers;
import net.atcore.command.ArgumentUse;
import net.atcore.inventory.InventorySection;
import net.atcore.messages.Message;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static net.atcore.messages.MessagesManager.sendMessage;

public class SeeInventoryCommand extends BaseCommand {

    public SeeInventoryCommand() {
        super("seeInventory",
                new ArgumentUse("/seeInventory").addArgPlayer(ModeTabPlayers.NORMAL),
                CommandVisibility.PRIVATE,
                "Puedes ver y modificar el inventario del jugador a tiempo real y de manera remota"
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

                        //atp2.setInventorySection(InventorySection.MANIPULATED);
                        atp2.getModerationPlayer().getManipulatorInventoryPlayer().add(player.getUniqueId());
                    }else {
                        sendMessage(sender, Message.COMMAND_SEE_INVENTORY_ERROR);
                    }
                }else{
                    sendMessage(sender, Message.COMMAND_GENERIC_PLAYER_NOT_FOUND);
                }
            }else {
                sendMessage(sender, Message.COMMAND_SEE_INVENTORY_MISSING_ARGS);
            }
        }else{
            sendMessage(sender, Message.COMMAND_GENERIC_NO_PLAYER);
        }
    }
}
