package net.atcore.inventory.inventors;

import net.atcore.AviaTerraPlayer;
import net.atcore.inventory.BaseInventory;
import net.atcore.inventory.InventoryUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ManipulatorInventory extends BaseInventory {

    public ManipulatorInventory() {
        super(54,"Inventario de moderación");
    }

    @Override
    public Inventory createInventory(AviaTerraPlayer player) {
        Inventory inv = transformInventory(player.getManipulatedInventoryPlayer(), createNewInventory(null));
        ItemStack item = InventoryUtils.newItems(Material.GRAY_STAINED_GLASS_PANE, " ", 1, null);
        InventoryUtils.fillItems(inv, item, 36, 44);
        InventoryUtils.setItems(inv, item, 49, 51, 53);
        return inv;
    }

    private Inventory transformInventory(Player victim, Inventory inv) {
        for (int i = 0; i < 54; i++){
            ItemStack item = victim.getInventory().getItem(i);
            if (item != null){
                if (i >= 36 && i <= 39){
                    inv.setItem(i + 9,item);
                }else if (i == 40){
                    inv.setItem(i + 10,item);
                }else if (i > 8){
                    inv.setItem(i - 9,item);
                }else{
                    inv.setItem(i + 27,item);
                }
            }
        }
        inv.setItem(52, victim.getItemOnCursor());
        return inv;
    }
}
