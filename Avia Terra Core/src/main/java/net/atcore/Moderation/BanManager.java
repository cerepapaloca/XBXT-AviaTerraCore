package net.atcore.Moderation;

import net.atcore.Data.BanDataBase;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Objects;

import static net.atcore.AviaTerraCore.plugin;

public class BanManager extends BanDataBase {

    //todo falta cositas para tener el sistema de baneo

    public static void BanPlayer(Player player, String reason, long time) {
        BanPlayer(player, reason, time, ContextBan.GLOBAL);
    }

    public static void BanPlayer(Player player, String reason, long time, ContextBan context) {
        addBanPlayer(player.getUniqueId().toString(),
                player.getName(),
                Objects.requireNonNull(player.getAddress()).getHostName(),
                reason,
                (time + System.currentTimeMillis()),
                System.currentTimeMillis(),
                context.name());
        Bukkit.getScheduler().runTask(plugin, () -> Objects.requireNonNull(player).kickPlayer("reasonFinal"));
    }
}
