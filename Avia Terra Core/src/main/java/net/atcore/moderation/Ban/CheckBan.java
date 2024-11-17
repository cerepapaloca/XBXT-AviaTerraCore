package net.atcore.moderation.Ban;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerLoginEvent;
import org.jetbrains.annotations.NotNull;

public class CheckBan {

    public static boolean checkChat(@NotNull Player player) {
        IsBan isBan = BanManager.checkBan(player, ContextBan.CHAT);
        return isBan.equals(IsBan.YES);
    }
}
