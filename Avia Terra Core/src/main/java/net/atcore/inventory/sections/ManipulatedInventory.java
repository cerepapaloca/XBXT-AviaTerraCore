package net.atcore.inventory.sections;

import net.atcore.AviaTerraPlayer;
import net.atcore.inventory.ClickInventory;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class ManipulatedInventory implements ClickInventory {

    @Override
    public void clickInventory(InventoryClickEvent event, AviaTerraPlayer player) {
        for (Player p : player.getManipulatorInventoryPlayer()){
            p.getOpenInventory().getTopInventory().setContents(player.getPlayer().getInventory().getContents());
        }
    }

    @Override
    public void closeInventory(InventoryCloseEvent event, AviaTerraPlayer player) {

    }
}
