package net.atcore.aviaterraplayer;

import lombok.Getter;
import lombok.Setter;
import net.atcore.AviaTerraCore;
import net.atcore.inventory.InventorySection;
import net.atcore.messages.MessagesManager;
import net.atcore.messages.TypeMessages;
import net.atcore.utils.GlobalUtils;
import net.atcore.utils.RangeType;
import net.luckperms.api.model.user.User;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;

import java.util.HashMap;
import java.util.UUID;

@Getter
@Setter
public class AviaTerraPlayer {

    public AviaTerraPlayer(Player player) {
        this.uuid = player.getUniqueId();
        User user = AviaTerraCore.getLp().getUserManager().getUser(player.getUniqueId());
        assert user != null;
        this.rangeType = RangeType.valueOf(user.getPrimaryGroup().toUpperCase());

    }

    private final static HashMap<UUID, AviaTerraPlayer> AVIA_TERRA_PLAYERS = new HashMap<>();

    private final ModerationPlayer moderationPlayer = new ModerationPlayer(this);
    private final ArmamentPlayer armamentPlayer = new ArmamentPlayer(this);
    private final UUID uuid;

    private InventorySection inventorySection = null;
    private RangeType rangeType;

    public void sendMessage(String message, TypeMessages type) {
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
    }

}
