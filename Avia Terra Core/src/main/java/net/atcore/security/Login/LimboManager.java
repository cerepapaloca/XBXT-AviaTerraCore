package net.atcore.security.Login;

import lombok.experimental.UtilityClass;
import net.atcore.AviaTerraCore;
import net.atcore.messages.MessagesManager;
import net.atcore.messages.TypeMessages;
import net.atcore.utils.GlobalUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

@UtilityClass
public class LimboManager {

    public static final int LIMBO_TIME = 20*60;

    public void startSynchronizeLimboMode(Player player, ReasonLimbo reasonLimbo){
        if (player.isOnline() && !LoginManager.isLimboMode(player)){
            if (Bukkit.isPrimaryThread()){
                LimboManager.createLimboMode(player, reasonLimbo);
            }else{
                Bukkit.getScheduler().runTask(AviaTerraCore.getInstance(), () ->
                        LimboManager.createLimboMode(player, reasonLimbo));
            }
        }
    }

    public void createLimboMode(Player player, ReasonLimbo reasonLimbo){
        DataLogin dataLogin = LoginManager.getDataLogin(player);
        DataLimbo dataLimbo = new DataLimbo(player.getGameMode(),
                player.getInventory().getContents(),
                player.getLocation(),
                player.isOp(),
                player.getLevel());
        player.getInventory().clear();
        player.teleport(player.getWorld().getSpawnLocation());
        player.setOp(false);
        player.setGameMode(GameMode.SPECTATOR);
        player.setLevel(0);
        player.setAllowFlight(true);
        dataLogin.setLimbo(dataLimbo);
        switch (reasonLimbo){
            case NO_SESSION -> {
                MessagesManager.sendTitle(player,
                    "Utiliza Este Comando", "<|/login <Contraseña>|>", 30, LIMBO_TIME, 30, TypeMessages.INFO);
                MessagesManager.sendMessage(player,
                        "Para Loguear utiliza el siguiente comando:\n <|/login <Contraseña>|>", TypeMessages.INFO);
                startTimeOut(player, "Tardaste mucho en iniciar sesión", dataLimbo);
            }
            case NO_REGISTER -> {
                MessagesManager.sendTitle(player,
                    "Utiliza Este Comando", "<|/register <Contraseña> <Contraseña>|>", 30, LIMBO_TIME, 30, TypeMessages.INFO);
                MessagesManager.sendMessage(player,
                        "Para registrarte utiliza el siguiente comando:\n <|/register <Contraseña> <Contraseña>|>.", TypeMessages.INFO);
                startTimeOut(player, "Tardaste mucho en registrarte", dataLimbo);
            }
        }
    }

    public static void startTimeOut(Player player, String reason, DataLimbo dataLimbo){
        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                if (LoginManager.checkLoginIn(player)) return;
                GlobalUtils.kickPlayer(player, reason);
            }
        }.runTaskLater(AviaTerraCore.getInstance(), LIMBO_TIME);
        dataLimbo.setTask(task);
    }
}