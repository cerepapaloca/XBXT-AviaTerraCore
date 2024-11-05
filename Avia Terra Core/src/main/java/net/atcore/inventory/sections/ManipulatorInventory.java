package net.atcore.inventory.sections;

import net.atcore.AviaTerraPlayer;
import net.atcore.inventory.ClickInventory;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class ManipulatorInventory implements ClickInventory {

    @Override
    public void clickInventory(InventoryClickEvent event, AviaTerraPlayer player) {
        if (player.getManipulatedInventoryPlayer() == null) return;
        Player victim = player.getManipulatedInventoryPlayer();
        victim.getInventory().setContents(player.getPlayer().getOpenInventory().getTopInventory().getContents());
    }

    @Override
    public void closeInventory(InventoryCloseEvent event, AviaTerraPlayer player) {
        player.setInventorySectionList(null);
        Player victim = player.getManipulatedInventoryPlayer();
        player.setManipulatedInventoryPlayer(null);
        AviaTerraPlayer atp = AviaTerraPlayer.getPlayer(victim);
        atp.setInventorySectionList(null);
        atp.getManipulatorInventoryPlayer().remove(player.getPlayer());

    }
}
