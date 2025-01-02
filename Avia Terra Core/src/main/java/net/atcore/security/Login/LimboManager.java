package net.atcore.security.Login;

import lombok.experimental.UtilityClass;
import net.atcore.AviaTerraCore;
import net.atcore.data.DataSection;
import net.atcore.data.FileYaml;
import net.atcore.data.yml.CacheLimboFile;
import net.atcore.messages.Message;
import net.atcore.messages.MessagesManager;
import net.atcore.messages.MessagesType;
import net.atcore.utils.GlobalUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

@UtilityClass
public class LimboManager {

    public static final int LIMBO_TIME = 20*60;

    public void startSynchronizeLimboMode(Player player, ReasonLimbo reasonLimbo) {
        AviaTerraCore.getInstance().enqueueTaskAsynchronously(() -> {
            if (player.isOnline() && !LoginManager.isLimboMode(player)){
                LimboManager.createLimboMode(player, reasonLimbo);
            /*
            if (Bukkit.isPrimaryThread()){
                LimboManager.createLimboMode(player, reasonLimbo);
            }else{
                Bukkit.getScheduler().runTask(AviaTerraCore.getInstance(), () -> LimboManager.createLimboMode(player, reasonLimbo));
            }*/
            }
        });
    }

    public void createLimboMode(Player player, ReasonLimbo reasonLimbo){
        DataLogin dataLogin = LoginManager.getDataLogin(player);
        String uuidString = player.getUniqueId().toString();
        FileYaml file = DataSection.getFliesCacheLimbo().getConfigFile(uuidString, false);
        DataLimbo dataLimbo;
        if (file == null){// Si tiene un archivo eso quiere decir que no pudo aplicar las propiedades al usuario
            dataLimbo = newDataLimbo(player, dataLogin, uuidString);
        }else {
            if (file instanceof CacheLimboFile cacheLimbo){
                if (cacheLimbo.isRestored()){
                    dataLimbo = newDataLimbo(player, dataLogin, uuidString);
                }else {
                    // Carga los datos del usuario
                    file.loadData();
                    dataLimbo = dataLogin.getLimbo();
                }
            }else {
                dataLimbo = newDataLimbo(player, dataLogin, uuidString);
            }
        }

        Bukkit.getScheduler().runTask(AviaTerraCore.getInstance(), () -> {
            player.getInventory().clear();
            player.teleport(player.getWorld().getSpawnLocation());
            player.setOp(false);
            player.setGameMode(GameMode.SPECTATOR);
            player.setLevel(0);
            player.setAllowFlight(true);
            switch (reasonLimbo){
                case NO_SESSION -> {
                    MessagesManager.sendTitle(player, Message.LOGIN_LIMBO_INITIATED_BY_SESSION_TITLE.getMessage(), Message.LOGIN_LIMBO_INITIATED_BY_SESSION_SUBTITLE.getMessage(), 30, LIMBO_TIME, 30, MessagesType.INFO);
                    MessagesManager.sendMessage(player, Message.LOGIN_LIMBO_INITIATED_BY_SESSION_CHAT.getMessage(), MessagesType.INFO);
                    startTimeOut(player, Message.LOGIN_LIMBO_TIME_OUT_SESSION.getMessage(), dataLimbo);
                }
                case NO_REGISTER -> {
                    MessagesManager.sendTitle(player, Message.LOGIN_LIMBO_INITIATED_BY_REGISTER_TITLE.getMessage(), Message.LOGIN_LIMBO_INITIATED_BY_REGISTER_SUBTITLE.getMessage(), 30, LIMBO_TIME, 30, MessagesType.INFO);
                    MessagesManager.sendMessage(player, Message.LOGIN_LIMBO_INITIATED_BY_REGISTER_CHAT.getMessage(), MessagesType.INFO);
                    startTimeOut(player, Message.LOGIN_LIMBO_TIME_OUT_REGISTER.getMessage(), dataLimbo);
                }
            }
        });
    }

    private @NotNull DataLimbo newDataLimbo(Player player, DataLogin dataLogin, String uuidString) {
        DataLimbo dataLimbo;
        dataLimbo = new DataLimbo(player.getGameMode(),
                player.getInventory().getContents(),
                player.getLocation(),
                player.isOp(),
                player.getLevel());
        dataLogin.setLimbo(dataLimbo);
        // Se guarda los datos cuando sé crea el limboData esto es solo por si hubo problema grave con el servidor
        CacheLimboFile limboFile = (CacheLimboFile) DataSection.getFliesCacheLimbo().registerConfigFile(uuidString);
        limboFile.saveData();
        limboFile.setRestored(false);

        return dataLimbo;
    }

    private void startTimeOut(Player player, String reason, DataLimbo dataLimbo){
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