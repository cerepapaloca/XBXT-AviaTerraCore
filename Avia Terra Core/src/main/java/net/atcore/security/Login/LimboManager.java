package net.atcore.security.Login;

import lombok.experimental.UtilityClass;
import net.atcore.AviaTerraCore;
import net.atcore.data.DataSection;
import net.atcore.data.FileYaml;
import net.atcore.data.yml.CacheLimboFile;
import net.atcore.messages.CategoryMessages;
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

    public final int LIMBO_TIME = 20*60;

    public void startAsynchronouslyLimboMode(Player player, ReasonLimbo reasonLimbo) {
        try {
            if (player.isOnline() && !LoginManager.isLimboMode(player)){
                if (Bukkit.isPrimaryThread()) {
                    LimboManager.createLimboMode(player, reasonLimbo);
                }else {
                    Bukkit.getScheduler().runTask(AviaTerraCore.getInstance(), () -> LimboManager.createLimboMode(player, reasonLimbo));
                }
            }
        }catch (Exception e){// Esto es un porsi acaso hay un error. Es mejor hacer un kick por seguridad
            MessagesManager.sendWaringException("Error al pasar al limbo mode", e);
            GlobalUtils.synchronizeKickPlayer(player, Message.LOGIN_KICK_ENTRY_LIMBO_ERROR.getMessage());
        }
    }

    public void createLimboMode(Player player, ReasonLimbo reasonLimbo){
        MessagesManager.sendMessageConsole(String.format(Message.LOGIN_LIMBO_INITIATED_LOG.getMessage(), player.getName()), MessagesType.INFO, CategoryMessages.LOGIN);
        LoginData loginData = LoginManager.getDataLogin(player);
        String uuidString = player.getUniqueId().toString();
        FileYaml file = DataSection.getCacheLimboFlies().getConfigFile(uuidString, false);
        sendMessage(player, reasonLimbo, newLimboData(player, loginData));
        if (file != null) {// Si tiene un archivo eso quiere decir que no pudo aplicar las propiedades al usuario
            if (file instanceof CacheLimboFile cacheLimbo){
                AviaTerraCore.getInstance().enqueueTaskAsynchronously(() -> {
                    if (!cacheLimbo.isRestored()){
                        Bukkit.getLogger().warning("Limbo restored");
                        // Carga los datos del usuario
                        // Se realiza de manera asincrónica por qué no se requiere los datos del usuario para crear el LimboData
                        cacheLimbo.loadData();
                    }
                });

            }
        }
        AviaTerraCore.getInstance().enqueueTaskAsynchronously(() -> {
            CacheLimboFile limboFile = (CacheLimboFile) DataSection.getCacheLimboFlies().registerConfigFile(player.getUniqueId().toString());
            limboFile.saveData();
            limboFile.setRestored(false);
        });
        player.getInventory().clear();
        player.teleport(player.getWorld().getSpawnLocation());
        player.setOp(false);
        player.setGameMode(GameMode.SPECTATOR);
        player.setLevel(0);
        player.setAllowFlight(true);
        player.setHealth(20);
        player.setFoodLevel(20);
        player.setExhaustion(0);
        player.setSaturation(0);
        player.setFireTicks(0);
        player.clearActivePotionEffects();
    }

    private void sendMessage(Player player, ReasonLimbo reasonLimbo, LimboData limboData) {
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
    }

    private @NotNull LimboData newLimboData(Player player, LoginData loginData) {
        LimboData limboData;
        limboData = new LimboData(player.getGameMode(),
                player.getInventory().getContents(),
                player.getLocation(),
                player.isOp(),
                player.getLevel(),
                player.getHealth(),
                player.getFoodLevel(),
                player.getExhaustion(),
                player.getSaturation(),
                player.getFireTicks(),
                player.getActivePotionEffects().stream().toList()
        );
        loginData.setLimbo(limboData);
        // Se guarda los datos cuando sé crea el limboData esto es solo por si hubo problema grave con el servidor
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