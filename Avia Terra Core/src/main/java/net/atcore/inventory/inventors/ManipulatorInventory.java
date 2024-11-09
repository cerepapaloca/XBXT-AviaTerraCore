package net.atcore.inventory.inventors;

import net.atcore.AviaTerraPlayer;
import net.atcore.inventory.BaseInventory;
import net.atcore.inventory.InventoryUtils;
import net.atcore.utils.GlobalUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;
public class ManipulatorInventory extends BaseInventory {

    public ManipulatorInventory() {
        super(54,"Inventario de moderación");
    }

    public static final HashMap<UUID, Inventory> inventories = new HashMap<>();

    @Override
    public Inventory createInventory(AviaTerraPlayer player) {
        return transformInventory(GlobalUtils.getPlayer(player.getManipulatedInventoryPlayer()));
    }

    private static final ItemStack AIR = new ItemStack(Material.AIR);

    public Inventory transformInventory(Player victim) {
        Inventory inv = inventories.get(victim.getUniqueId());
        if (inv == null) {
            inv = createNewInventory(AviaTerraPlayer.getPlayer(victim));
            inventories.put(victim.getUniqueId(), inv);
        }
        inv.clear();
        inv.setItem(52, victim.getItemOnCursor());
        for (int i = 0; i < 54; i++){
            ItemStack item = Objects.requireNonNull(GlobalUtils.getPlayer(victim.getName())).getInventory().getItem(i);
            if (item != null) {
                if (i >= 36 && i <= 39){
                    inv.setItem(i + 9,item);
                }else if (i == 40){
                    inv.setItem(i + 10,item);
                }else if (i > 8){
                    inv.setItem(i - 9,item);
                }else {
                    inv.setItem(i + 27,item);
                }
            }
        }
        ItemStack item = InventoryUtils.newItems(Material.GRAY_STAINED_GLASS_PANE, " ", 1, null);
        InventoryUtils.fillItems(inv, item, 36, 44);
        InventoryUtils.setItems(inv, item, 49, 51, 53);
        return inv;
    }
}
