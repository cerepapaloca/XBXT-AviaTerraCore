package net.atcore.inventory.inventors;

import net.atcore.AviaTerraPlayer;
import net.atcore.inventory.BaseInventors;
import net.atcore.inventory.InventoryUtils;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;

import static net.atcore.inventory.Action.ManipulatorAction.updateInventory;

public class ManipulatorInventory extends BaseInventors {

    public ManipulatorInventory() {
        super(54,"Inventario de moderación");
    }

    @Override
    public Inventory createInventory(AviaTerraPlayer player) {
        Inventory inv = createNewInventory(player);
        updateInventory(player);
        InventoryUtils.fillItems(inv, InventoryUtils.newItems(Material.GRAY_STAINED_GLASS_PANE, " ", 1, null), 36, 45);
        return inv;
    }
}
