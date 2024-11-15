package net.atcore.moderation.Ban;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerLoginEvent;
import org.jetbrains.annotations.NotNull;

public class CheckBan {


    public static void onLogin(@NotNull PlayerLoginEvent event) {//No sé por qué cuando es la primeras vez que entras al servidor se dispara 4 veces seguidas
        Player player = event.getPlayer();
        IsBan isBan = BanManager.checkBan(player, event.getAddress(), ContextBan.GLOBAL);
        if (isBan.equals(IsBan.YES)) {
            event.setKickMessage(BanManager.getReasonBan());
            event.setResult(PlayerLoginEvent.Result.KICK_BANNED);
        }
    }

    public static boolean checkChat(@NotNull Player player) {
        IsBan isBan = BanManager.checkBan(player, ContextBan.CHAT);
        return isBan.equals(IsBan.YES);
    }
}
