package net.atcore.command;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public record DataTemporalPlayer(@NotNull String name, Player player) {
}
