package net.atcore.aviaterraplayer;

import lombok.Getter;
import lombok.Setter;
import net.atcore.AviaTerraCore;
import net.atcore.command.commnads.TpaCommand;
import net.atcore.data.DataSection;
import net.atcore.data.yml.PlayerDataFile;
import net.atcore.inventory.InventorySection;
import net.atcore.messages.Message;
import net.atcore.messages.MessagesManager;
import net.atcore.messages.TypeMessages;
import net.atcore.utils.GlobalUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Se encarga de guardar variable y funciones donde implique a un jugador específico.
 * Para más orden se usa {@link ArmamentPlayer} y {@link ModerationPlayer}
 */

@Getter
@Setter
public class AviaTerraPlayer {

    public static final HashMap<UUID, BukkitTask> TASKS_UNLOAD = new HashMap<>();

    public AviaTerraPlayer(Player player) {
        this.uuid = player.getUniqueId();
        if (TASKS_UNLOAD.containsKey(uuid)) TASKS_UNLOAD.get(uuid).cancel();
        AviaTerraCore.enqueueTaskAsynchronously(() -> {
            this.playerDataFile = (PlayerDataFile) DataSection.getPlayersDataFiles().getConfigFile(uuid.toString(), true);
            //DataSection.getCacheLimboFlies().getConfigFile(uuid.toString(), true);
            updateView(player);
            playerDataFile.loadData();
        });
    }

    private final static HashMap<UUID, AviaTerraPlayer> AVIA_TERRA_PLAYERS = new HashMap<>();

    @NotNull
    private final ModerationPlayer moderationPlayer = new ModerationPlayer(this);
    @NotNull
    private final ArmamentPlayer armamentPlayer = new ArmamentPlayer(this);
    private final UUID uuid;
    private final List<TpaCommand.TpaRequest> ListTpa = new ArrayList<>();
    private final HashMap<String, Location> homes = new HashMap<>();
    private String nameColor = null;
    private PlayerDataFile playerDataFile;

    private InventorySection inventorySection = null;

    public void sendMessage(Message message) {
        MessagesManager.sendMessage(GlobalUtils.getPlayer(uuid), message);
    }

    public void sendString(String message, TypeMessages type) {
        MessagesManager.sendString(getPlayer(), message, type);
    }

    /**
     * @param uuid La uuid que le da el servidor no la real
     */

    @Contract(pure = true)
    public static AviaTerraPlayer getPlayer(UUID uuid){
        return AVIA_TERRA_PLAYERS.get(uuid);
    }

    @Contract(pure = true)
    public static AviaTerraPlayer getPlayer(Player player){
        return AVIA_TERRA_PLAYERS.get(player.getUniqueId());
    }

    @Contract(pure = true)
    public Player getPlayer(){
        return GlobalUtils.getPlayer(uuid);
    }

    public void unloadPlayer(){
        AVIA_TERRA_PLAYERS.remove(uuid);
    }

    public static void addPlayer(Player player){
        if (!AVIA_TERRA_PLAYERS.containsKey(player.getUniqueId())){
            AVIA_TERRA_PLAYERS.put(player.getUniqueId(), new AviaTerraPlayer(player));
        }

        //AVIA_TERRA_PLAYERS.get(player.getUniqueId()).joinEvent(player);
    }

    public void updateView(Player player) {
        int renderDistance = getMaxPermission(player, "viewdistance");
        player.setSendViewDistance(renderDistance);
        player.setViewDistance(renderDistance);
        player.setSimulationDistance(getMaxPermission(player, "simulationdistance"));
    }

    public int getMaxHome(){
        Player player = getPlayer();
        return getMaxPermission(player, "maxhome");
    }

    private static int getMaxPermission(Player player, String permissionName) {
        if (player.isOp()) return 32;
        int renderDistance = 4;
        int maxChunks = 4;
        for (String permission : player.getEffectivePermissions().stream().map(PermissionAttachmentInfo::getPermission).toList()) {
            if (permission.startsWith(AviaTerraCore.getInstance().getName().toLowerCase() + "." + permissionName + ".")) {
                String chunksStr = permission.replace(AviaTerraCore.getInstance().getName().toLowerCase() + "." + permissionName + ".", "");
                try {
                    int chunks = Integer.parseInt(chunksStr);
                    chunks = Math.max(4, Math.min(32, chunks));

                    if (chunks > maxChunks) {
                        maxChunks = chunks;
                    }
                    renderDistance = maxChunks;
                } catch (NumberFormatException ignored) {
                }
            }
        }
        return renderDistance;
    }
}
