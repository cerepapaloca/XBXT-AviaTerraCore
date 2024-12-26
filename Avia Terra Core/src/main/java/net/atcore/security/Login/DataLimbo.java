package net.atcore.security.Login;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.atcore.AviaTerraCore;
import net.atcore.data.DataSection;
import net.atcore.data.FileYaml;
import net.atcore.data.yml.CacheLimboFile;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
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
        player.teleport(location, PlayerTeleportEvent.TeleportCause.PLUGIN);
        player.getInventory().setContents(items);
        player.saveData();// Se guarda los datos del usuario en el servidor por si el servidor peta
        if (gameMode == GameMode.SURVIVAL) player.setAllowFlight(false);
        FileYaml file = DataSection.getFliesCacheLimbo().getConfigFile(player.getUniqueId().toString(), false);

        AviaTerraCore.getInstance().enqueueTaskAsynchronously(() -> {
            if (file != null) {
                if (file instanceof CacheLimboFile cacheLimbo) {
                    cacheLimbo.setRestored(true);
                }
            }
        });
        //DataSection.getFliesCacheLimbo().deleteConfigFile(player.getUniqueId().toString());// Borra la caché del jugador por qué ya no hace falta
    }
}
