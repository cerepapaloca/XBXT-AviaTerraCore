package net.atcore.moderation.Ban;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;

public interface RunActionBan {
    boolean onContext(Player player, Event event);
    void onBan(Player player, DataBan dataBan);
}
