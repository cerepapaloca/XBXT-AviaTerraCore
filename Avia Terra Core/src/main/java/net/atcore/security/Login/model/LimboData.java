package net.atcore.security.Login.model;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.atcore.AviaTerraCore;
import net.atcore.data.DataSection;
import net.atcore.data.yml.CacheLimboFile;
import net.atcore.messages.CategoryMessages;
import net.atcore.messages.Message;
import net.atcore.messages.MessagesManager;
import net.atcore.messages.TypeMessages;
import net.atcore.utils.GlobalUtils;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashSet;
import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
public class LimboData {

    private final GameMode gameMode;
    private final ItemStack[] items;
    private final Location location;
    private final boolean op;
    private final int level;
    private final double health;
    private final int foodLevel;
    private final float exhaustion;
    private final float saturation;
    private final int fireTicks;
    private final List<PotionEffect> effects;

    private BukkitTask task;
    private HashSet<PacketContainer> packets;

    public void restorePlayer(Player player) {
        player.setGameMode(gameMode);
        player.setOp(op);
        player.setLevel(level);
        player.teleport(location, PlayerTeleportEvent.TeleportCause.PLUGIN);
        player.getInventory().setContents(items);
        player.setHealth(health);
        player.setFoodLevel(foodLevel);
        player.setExhaustion(exhaustion);
        player.setSaturation(saturation);
        player.addPotionEffects(effects);
        player.setFireTicks(fireTicks);
        player.saveData();// Se guarda los datos del usuario en el servidor por si el servidor peta
        if (gameMode == GameMode.SURVIVAL) player.setAllowFlight(false);
        CacheLimboFile cacheLimbo = (CacheLimboFile) DataSection.getCacheLimboFlies().getConfigFile(GlobalUtils.getRealUUID(player).toString(), false);
        AviaTerraCore.enqueueTaskAsynchronously(() -> cacheLimbo.setRestored(true));
        cacheLimbo.removeLimbo();
        if (packets != null) {
            for (PacketContainer packet : packets.stream().toList()) {
                ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet, true);
            }
        }
        MessagesManager.logConsole(String.format(Message.LOGIN_LIMBO_EXIT.getMessage(player), player.getName()), TypeMessages.INFO, CategoryMessages.LOGIN);
    }
}
