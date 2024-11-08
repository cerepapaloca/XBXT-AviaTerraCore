package net.atcore.inventory;

import lombok.Getter;
import lombok.Setter;
import net.atcore.AviaTerraPlayer;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;

@Getter
@Setter
public abstract class BaseInventors {

    public BaseInventors(int size, String name) {
        this.name = name;
        this.size = size;
    }

    protected String name;
    protected int size;
    protected InventorySection section;

    public abstract Inventory createInventory(AviaTerraPlayer player);

    protected Inventory createNewInventory(AviaTerraPlayer player) {
        return Bukkit.createInventory(player.getPlayer(), size, name);
    }
}
