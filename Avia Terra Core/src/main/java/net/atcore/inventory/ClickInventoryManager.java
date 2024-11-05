package net.atcore.inventory;

import net.atcore.AviaTerraCore;
import net.atcore.AviaTerraPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.HashMap;

public class ClickInventoryManager {

    public static final HashMap<InventorySectionList, ClickInventory> inventories = new HashMap<>();

    public void clickEvent(InventoryClickEvent event){
        AviaTerraPlayer atp = AviaTerraPlayer.getPlayer((Player) event.getWhoClicked());
        if (atp.getInventorySectionList() == null) return;
        inventories.get(atp.getInventorySectionList()).clickInventory(event, atp);
    }
}
