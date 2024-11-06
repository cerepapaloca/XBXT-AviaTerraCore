package net.atcore.inventory.inventors;

import net.atcore.AviaTerraPlayer;
import net.atcore.inventory.BaseInventors;
import net.atcore.inventory.InventorySection;
import org.bukkit.inventory.Inventory;

public class ManipulatorInventory extends BaseInventors {

    public ManipulatorInventory() {
        super(54,"Inventario de moderación");
    }

    @Override
    public Inventory createInventory(AviaTerraPlayer player) {
        Inventory inv = createNewInventory(player);
        //InventoryUtils.fillItems(inv, InventoryUtils.newItems(Material.STRUCTURE_VOID, "&l&bHotBar", 1, null), 0, 9);
        inv.setContents(player.getManipulatedInventoryPlayer().getInventory().getContents());
        return inv;
    }
}
