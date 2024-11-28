package net.atcore.security.Login;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.atcore.data.DataSection;
import net.atcore.messages.MessagesManager;
import net.atcore.messages.TypeMessages;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

@Getter
@Setter
@RequiredArgsConstructor
public class DataLimbo {

    private final GameMode gameMode;
    private final ItemStack[] items;
    private final Location location;
    private final boolean op;
    private final int level;
    private BukkitTask task;

    public void restorePlayer(Player player) {
        player.setGameMode(gameMode);
        player.setOp(op);
        player.teleport(location);
        player.getInventory().setContents(items);
        player.saveData();// Se guarda los datos del usuario en el servidor por si el servidor peta
        if (gameMode == GameMode.SURVIVAL) player.setAllowFlight(false);
        DataSection.getFliesCacheLimbo().deleteConfigFile(player.getUniqueId().toString());// Borra la caché del jugador por qué ya no hace falta
    }
}
