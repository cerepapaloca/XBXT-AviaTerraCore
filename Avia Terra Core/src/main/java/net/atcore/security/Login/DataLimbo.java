package net.atcore.security.Login;

import lombok.Getter;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@Getter
public class DataLimbo {

    DataLimbo(Player p) {
        this.items = p.getInventory().getContents();
        this.location = p.getLocation();
        this.gameMode = p.getGameMode();
    }

    private final GameMode gameMode;
    private final ItemStack[] items;
    private final Location location;

}
