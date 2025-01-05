package net.atcore.security.Login.model;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.atcore.AviaTerraCore;
import net.atcore.data.DataSection;
import net.atcore.data.FileYaml;
import net.atcore.data.yml.CacheLimboFile;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashSet;

@Getter
@Setter
@RequiredArgsConstructor
public class LimboData {

    private final GameMode gameMode;
    private final ItemStack[] items;
    private final Location location;
    private final boolean op;
    private final int level;
    private BukkitTask task;
    private HashSet<PacketContainer> packets;

    public void restorePlayer(Player player) {
        player.setGameMode(gameMode);
        player.setOp(op);
        player.setLevel(level);
        player.teleport(location, PlayerTeleportEvent.TeleportCause.PLUGIN);
        player.getInventory().setContents(items);
        player.saveData();// Se guarda los datos del usuario en el servidor por si el servidor peta
        if (gameMode == GameMode.SURVIVAL) player.setAllowFlight(false);
        FileYaml file = DataSection.getCacheLimboFlies().getConfigFile(player.getUniqueId().toString(), false);

        AviaTerraCore.getInstance().enqueueTaskAsynchronously(() -> {
            if (packets != null) {
                for (PacketContainer packet : packets.stream().toList()) {
                    ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet, true);
                }
            }
            if (file != null) {
                if (file instanceof CacheLimboFile cacheLimbo) {
                    cacheLimbo.setRestored(true);
                    cacheLimbo.removeLimbo();
                }
            }
        });
    }
}
