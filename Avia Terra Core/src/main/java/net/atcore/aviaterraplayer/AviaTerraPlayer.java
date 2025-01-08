package net.atcore.aviaterraplayer;

import lombok.Getter;
import lombok.Setter;
import net.atcore.AviaTerraCore;
import net.atcore.command.commnads.TpaCommand;
import net.atcore.data.DataSection;
import net.atcore.data.yml.PlayerDataFile;
import net.atcore.inventory.InventorySection;
import net.atcore.messages.MessagesManager;
import net.atcore.messages.MessagesType;
import net.atcore.utils.GlobalUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;

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
    public AviaTerraPlayer(Player player) {
        this.uuid = player.getUniqueId();
        AviaTerraCore.getInstance().enqueueTaskAsynchronously(() -> {
            this.playerDataFile = (PlayerDataFile) DataSection.getPlayersData().getConfigFile(uuid.toString(), true);
            joinEvent(player);
        });
    }

    private final static HashMap<UUID, AviaTerraPlayer> AVIA_TERRA_PLAYERS = new HashMap<>();

    private final ModerationPlayer moderationPlayer = new ModerationPlayer(this);
    private final ArmamentPlayer armamentPlayer = new ArmamentPlayer(this);
    private final UUID uuid;
    private final List<TpaCommand.TpaRequest> ListTpa = new ArrayList<>();
    private final HashMap<String, Location> homes = new HashMap<>();
    private String nameColor = null;
    private PlayerDataFile playerDataFile;

    private InventorySection inventorySection = null;

    public void sendMessage(String message, MessagesType type) {
        MessagesManager.sendMessage(GlobalUtils.getPlayer(uuid), message, type);
    }


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

    public static void addPlayer(Player player){
        if (!AVIA_TERRA_PLAYERS.containsKey(player.getUniqueId())){
            AVIA_TERRA_PLAYERS.put(player.getUniqueId(), new AviaTerraPlayer(player));
        }
        AVIA_TERRA_PLAYERS.get(player.getUniqueId()).joinEvent(player);
    }

    public void joinEvent(Player player) {
        AviaTerraCore.getInstance().enqueueTaskAsynchronously(() -> {
            boolean b1 = true;
            boolean b2 = true;
            for (int i = 32; i > 4; i--) {
                if (!b1 && !b2) break;
                if (b1){
                    if (player.hasPermission(AviaTerraCore.getInstance().getName().toLowerCase() + ".simulationdistance." + i)) {
                        player.setSimulationDistance(i);
                        b1 = false;
                    }
                }

                if (b2){
                    if (player.hasPermission(AviaTerraCore.getInstance().getName().toLowerCase() + ".viewdistance." + i)) {
                        player.setViewDistance(i);
                        b2 = false;
                    }
                }
            }

            playerDataFile.loadData();
            Bukkit.getScheduler().runTask(AviaTerraCore.getInstance(), () -> {
                if (nameColor != null) player.displayName(GlobalUtils.chatColorLegacyToComponent(nameColor));
            });
        });
    }

}
