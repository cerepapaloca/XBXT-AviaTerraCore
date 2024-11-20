package net.atcore.security.Login;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@Getter
public class DataLimbo {

    DataLimbo(Player player) {
        this.items = player.getInventory().getContents();
        this.location = player.getLocation();
        this.gameMode = player.getGameMode();
        this.op = player.isOp();
        this.level = player.getLevel();
        player.getInventory().clear();
        player.teleport(player.getWorld().getSpawnLocation());
        player.setOp(false);
        player.setGameMode(GameMode.SPECTATOR);
        player.setLevel(0);
        player.setAllowFlight(true);
    }

    private final GameMode gameMode;
    private final ItemStack[] items;
    private final Location location;
    private final boolean op;
    private final int level;

    public void restorePlayer(Player player) {
        player.setGameMode(gameMode);
        player.setOp(op);
        player.teleport(location);
        player.getInventory().setContents(items);
        if (gameMode == GameMode.SURVIVAL) player.setAllowFlight(false);
    }
}
