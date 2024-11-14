package net.atcore.command;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public record TemporalPlayerData(@NotNull String name, Player player) {
}
