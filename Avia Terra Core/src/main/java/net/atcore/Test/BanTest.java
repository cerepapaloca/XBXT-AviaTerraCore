package net.atcore.Test;

import net.atcore.AviaTerraCore;
import net.atcore.AviaTerraPlayer;
import net.atcore.messages.MessagesManager;
import net.atcore.messages.TypeMessages;
import net.atcore.moderation.Ban.ContextBan;
import net.atcore.moderation.ModerationSection;
import net.atcore.utils.GlobalUtils;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.TestOnly;

@TestOnly
public class BanTest implements RunTest {

    public BanTest(){
        Bukkit.getLogger().warning("Test");
    }

    boolean isFinished = true;

    @Override
    public void runTest(AviaTerraPlayer player) {
        if (!isFinished) {
            player.sendMessage("Ya hay una prueba corriendo", TypeMessages.ERROR);
            return;
        }
        isFinished = false;
        ModerationSection.getBanManager().banPlayer(
                player.getPlayer(),
                "Es una prueba automatizada seras de desbaneado en unos segundos",
                1000*5,
                ContextBan.GLOBAL,
                "Servidor"
        );

        new BukkitRunnable() {
            public void run() {
                ModerationSection.getBanManager().removeBanPlayer(player.getPlayer().getName(), ContextBan.GLOBAL, "Servidor");
                ModerationSection.getBanManager().banPlayer(
                        "JugadorX",
                        GlobalUtils.getUUIDByName("JugadorX"),
                        player.getPlayer().getAddress().getAddress(),
                        "Es una prueba automatizada seras de desbaneado en unos segundos",
                        1000*60*60,
                        ContextBan.GLOBAL,
                        "Servidor"
                );
                new BukkitRunnable() {
                    public void run() {
                        ModerationSection.getBanManager().removeBanPlayer("JugadorX", ContextBan.GLOBAL, "Servidor");
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "aviaterra:ban jugadorA,JugadorB global 100d Es una prueba automatizada seras de desbaneado");
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "aviaterra:unban jugadorA,JugadorB global");
                        MessagesManager.sendMessageConsole("la prueba ha finalizado", TypeMessages.SUCCESS);
                        isFinished = true;
                    }
                }.runTaskLaterAsynchronously(AviaTerraCore.getInstance(), 60L);
            }
        }.runTaskLaterAsynchronously(AviaTerraCore.getInstance(), 60L);
    }
}
