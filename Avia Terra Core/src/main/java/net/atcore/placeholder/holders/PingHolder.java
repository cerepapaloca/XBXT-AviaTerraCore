package net.atcore.placeholder.holders;

import net.atcore.placeholder.BasePlaceHolder;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PingHolder extends BasePlaceHolder {
    public PingHolder() {
        super("ping");
    }

    @Override
    public @NotNull String onPlaceholderRequest(Player player) {
        if (player == null) {
            return "-1";
        }else {
            return String.valueOf(player.getPing());
        }
    }
}
