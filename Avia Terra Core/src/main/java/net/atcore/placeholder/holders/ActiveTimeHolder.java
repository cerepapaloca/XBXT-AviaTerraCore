package net.atcore.placeholder.holders;

import net.atcore.AviaTerraCore;
import net.atcore.placeholder.BasePlaceHolder;
import net.atcore.utils.GlobalUtils;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ActiveTimeHolder extends BasePlaceHolder {

    public ActiveTimeHolder() {
        super("active-time");
    }

    @Override
    public @NotNull String onPlaceholderRequest(Player player) {
        return GlobalUtils.timeToString(AviaTerraCore.getActiveTime(), 1);
    }
}
