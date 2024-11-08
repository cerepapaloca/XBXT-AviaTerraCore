package net.atcore.inventory;

import lombok.Getter;
import lombok.Setter;
import net.atcore.AviaTerraPlayer;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.Nullable;

@Getter
@Setter
public abstract class BaseInventory {

    public BaseInventory(int size, String name) {
        this.name = name;
        this.size = size;
    }

    protected String name;
    protected int size;
    protected InventorySection section;

    public abstract Inventory createInventory(AviaTerraPlayer player);

    protected Inventory createNewInventory(@Nullable AviaTerraPlayer player) {
        if (player == null) {
            return Bukkit.createInventory(null, size, name);
        }else {
            return Bukkit.createInventory(player.getPlayer(), size, name);
        }

    }
}
