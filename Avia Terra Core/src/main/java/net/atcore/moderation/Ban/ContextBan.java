package net.atcore.moderation.Ban;

import net.atcore.moderation.Ban.action.Chat;
import net.atcore.moderation.Ban.action.Global;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

public enum ContextBan {
    CHAT(new Chat()),
    GLOBAL(new Global()),;

    private final RunActionBan runActionBan;

    ContextBan(RunActionBan runActionBan) {
        this.runActionBan = runActionBan;
    }
    public boolean onContext(Player player, Event event) {
        return runActionBan.onContext(player, event);
    }

    public void onBan(Player player, DataBan dataBan) {
        runActionBan.onBan(player, dataBan);
    }
}
