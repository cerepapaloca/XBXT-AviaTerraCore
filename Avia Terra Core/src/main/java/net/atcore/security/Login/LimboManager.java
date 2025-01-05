package net.atcore.security.Login;

import lombok.experimental.UtilityClass;
import net.atcore.AviaTerraCore;
import net.atcore.data.DataSection;
import net.atcore.data.FileYaml;
import net.atcore.data.yml.CacheLimboFile;
import net.atcore.messages.Message;
import net.atcore.messages.MessagesManager;
import net.atcore.messages.MessagesType;
import net.atcore.security.Login.model.LimboData;
import net.atcore.security.Login.model.LoginData;
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
            }
        });
    }

    public void createLimboMode(Player player, ReasonLimbo reasonLimbo){
        LoginData loginData = LoginManager.getDataLogin(player);
        String uuidString = player.getUniqueId().toString();
        FileYaml file = DataSection.getCacheLimboFlies().getConfigFile(uuidString, false);
        LimboData limboData;
        if (file == null){// Si tiene un archivo eso quiere decir que no pudo aplicar las propiedades al usuario
            limboData = newLimboData(player, loginData, uuidString);
        }else {
            if (file instanceof CacheLimboFile cacheLimbo){
                if (cacheLimbo.isRestored()){
                    limboData = newLimboData(player, loginData, uuidString);
                }else {
                    // Carga los datos del usuario
                    file.loadData();
                    limboData = loginData.getLimbo();
                }
            }else {
                limboData = newLimboData(player, loginData, uuidString);
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
                    startTimeOut(player, Message.LOGIN_LIMBO_TIME_OUT_SESSION.getMessage(), limboData);
                }
                case NO_REGISTER -> {
                    MessagesManager.sendTitle(player, Message.LOGIN_LIMBO_INITIATED_BY_REGISTER_TITLE.getMessage(), Message.LOGIN_LIMBO_INITIATED_BY_REGISTER_SUBTITLE.getMessage(), 30, LIMBO_TIME, 30, MessagesType.INFO);
                    MessagesManager.sendMessage(player, Message.LOGIN_LIMBO_INITIATED_BY_REGISTER_CHAT.getMessage(), MessagesType.INFO);
                    startTimeOut(player, Message.LOGIN_LIMBO_TIME_OUT_REGISTER.getMessage(), limboData);
                }
            }
        });
    }

    private @NotNull LimboData newLimboData(Player player, LoginData loginData, String uuidString) {
        LimboData limboData;
        limboData = new LimboData(player.getGameMode(),
                player.getInventory().getContents(),
                player.getLocation(),
                player.isOp(),
                player.getLevel());
        loginData.setLimbo(limboData);
        // Se guarda los datos cuando sé crea el limboData esto es solo por si hubo problema grave con el servidor
        CacheLimboFile limboFile = (CacheLimboFile) DataSection.getCacheLimboFlies().registerConfigFile(uuidString);
        limboFile.saveData();
        limboFile.setRestored(false);

        return limboData;
    }

    private void startTimeOut(Player player, String reason, LimboData limboData){
        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                if (LoginManager.checkLoginIn(player)) return;
                GlobalUtils.kickPlayer(player, reason);
            }
        }.runTaskLater(AviaTerraCore.getInstance(), LIMBO_TIME);
        limboData.setTask(task);
    }
}