package net.atcore.placeholder.holders;

import net.atcore.placeholder.BasePlaceHolder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CurrentTpsHolder extends BasePlaceHolder {
    public CurrentTpsHolder() {
        super("tps");
    }

    @Override
    public @NotNull String onPlaceholderRequest(Player player) {
        return String.valueOf(Math.round(Bukkit.getTPS()[0]));
    }
}
