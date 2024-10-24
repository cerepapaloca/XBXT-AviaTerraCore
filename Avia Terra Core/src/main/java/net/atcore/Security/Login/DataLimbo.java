package net.atcore.Security.Login;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@Getter
public class DataLimbo {

    DataLimbo(Player p) {
        this.items = p.getInventory().getContents();
        this.location = p.getLocation();
    }

    private final ItemStack[] items;
    private final Location location;
}
