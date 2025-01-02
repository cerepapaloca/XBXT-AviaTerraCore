package net.atcore.utils.debug;

import net.atcore.AviaTerraCore;
import net.atcore.aviaterraplayer.AviaTerraPlayer;
import net.atcore.messages.Message;
import net.atcore.messages.MessagesManager;
import net.atcore.messages.MessagesType;
import net.atcore.moderation.ban.ContextBan;
import net.atcore.moderation.ModerationSection;
import net.atcore.utils.GlobalUtils;
import org.bukkit.Bukkit;

import java.net.InetAddress;
import java.util.Objects;


public final class BanDebug implements RunTest {

    @Override
    public void runTest(AviaTerraPlayer player) {
        String name = player.getPlayer().getName();
        InetAddress ip = Objects.requireNonNull(player.getPlayer().getAddress()).getAddress();
        ModerationSection.getBanManager().banPlayer(
                player.getPlayer(),
                "Es una prueba automatizada seras de desbaneado en unos segundos",
                1000*5,
                ContextBan.GLOBAL,
                "Servidor"
        );
        ModerationSection.getBanManager().removeBanPlayer(name, ContextBan.GLOBAL, "Servidor");
        ModerationSection.getBanManager().banPlayer(
                "JugadorX",
                GlobalUtils.getUUIDByName("JugadorX"),
                ip,
                "Es una prueba automatizada seras de desbaneado en unos segundos",
                1000*60*60,
                ContextBan.GLOBAL,
                "Servidor"
        );
        ModerationSection.getBanManager().removeBanPlayer("JugadorX", ContextBan.GLOBAL, "Servidor");
        Bukkit.getScheduler().runTask(AviaTerraCore.getInstance(), () -> {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "aviaterracore:ban jugadorA,JugadorB global 100d Es una prueba automatizada seras de desbaneado");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "aviaterracore:unban jugadorA,JugadorB global");
            MessagesManager.sendMessageConsole(Message.TEST_FINISHED.getMessage(), MessagesType.SUCCESS);
        });
    }
}
