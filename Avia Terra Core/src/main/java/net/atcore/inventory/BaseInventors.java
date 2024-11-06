package net.atcore.inventory;

import net.atcore.AviaTerraPlayer;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;

public abstract class BaseInventors {

    public BaseInventors(int size, String name) {
        this.name = name;
        this.size = size;
    }

    protected String name;
    protected int size;

    public abstract Inventory createInventory(AviaTerraPlayer player);

    protected Inventory createNewInventory(AviaTerraPlayer player) {
        return Bukkit.createInventory(player.getPlayer(), size, name);
    }
}
