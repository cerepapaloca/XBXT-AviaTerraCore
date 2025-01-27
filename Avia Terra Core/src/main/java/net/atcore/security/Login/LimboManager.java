package net.atcore.security.Login;

import com.google.common.collect.Sets;
import lombok.experimental.UtilityClass;
import net.atcore.AviaTerraCore;
import net.atcore.data.DataSection;
import net.atcore.data.FileYaml;
import net.atcore.data.yml.CacheLimboFile;
import net.atcore.messages.CategoryMessages;
import net.atcore.messages.Message;
import net.atcore.messages.MessagesManager;
import net.atcore.messages.TypeMessages;
import net.atcore.security.Login.model.LimboData;
import net.atcore.security.Login.model.LoginData;
import net.atcore.utils.GlobalUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.geysermc.floodgate.api.FloodgateApi;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

@UtilityClass
public class LimboManager {

    public final int LIMBO_TIME = 20*60;
    public final Set<UUID> LIST_IN_PROCESS = Sets.newHashSet();
    private final PotionEffect BLINDNESS_EFFECT = new PotionEffect(PotionEffectType.BLINDNESS, PotionEffect.INFINITE_DURATION,1, true, false);

    public void startAsynchronouslyLimboMode(Player player, ReasonLimbo reasonLimbo) {
        try {
            if (player.isOnline() && !LoginManager.isLimboMode(player) && !LIST_IN_PROCESS.contains(player.getUniqueId())) {
                LIST_IN_PROCESS.add(player.getUniqueId());
                if (Bukkit.isPrimaryThread()) {
                    switch (reasonLimbo) {
                        case NO_SESSION -> {
                            LoginData loginData = LoginManager.getDataLogin(player);
                            boolean isOk = loginData.isBedrockPlayer() == FloodgateApi.getInstance().isFloodgatePlayer(player.getUniqueId());
                            if (isOk) {
                                if (loginData.isBedrockPlayer()) {
                                    Bukkit.getScheduler().runTask(AviaTerraCore.getInstance(), () -> LimboManager.createLimboMode(player, reasonLimbo));
                                } else {
                                    LimboManager.createLimboMode(player, reasonLimbo);
                                }
                            }else {
                                MessagesManager.logConsole(String.format("Los registros de %s y el floodGate dieron datos inconsistentes", player.getName()), TypeMessages.WARNING, CategoryMessages.LOGIN);
                                GlobalUtils.synchronizeKickPlayer(player, Message.LOGIN_LIMBO_BEDROCK_ERROR);
                            }
                        }
                        case NO_REGISTER -> Bukkit.getScheduler().runTask(AviaTerraCore.getInstance(), () -> LimboManager.createLimboMode(player, reasonLimbo));
                    }
                }else {
                    Bukkit.getScheduler().runTask(AviaTerraCore.getInstance(), () -> LimboManager.createLimboMode(player, reasonLimbo));
                }
            }
        }catch (Exception e){// Esto es un porsi acaso hay un error. Es mejor hacer un kick por seguridad
            LIST_IN_PROCESS.remove(player.getUniqueId());
            MessagesManager.sendWaringException("Error al pasar al limbo mode", e);
            GlobalUtils.synchronizeKickPlayer(player, Message.LOGIN_KICK_ENTRY_LIMBO_ERROR);
        }
    }

    public void createLimboMode(Player player, ReasonLimbo reasonLimbo){
        MessagesManager.logConsole(String.format(Message.LOGIN_LIMBO_INITIATED_LOG.getMessage(player), player.getName()), TypeMessages.INFO, CategoryMessages.LOGIN);
        LoginData loginData = LoginManager.getDataLogin(player);
        String uuidString = GlobalUtils.getRealUUID(player).toString();
        FileYaml file = DataSection.getCacheLimboFlies().getConfigFile(uuidString, false);
        AtomicBoolean aBoolean = new AtomicBoolean(true);
        addLimboData(player, loginData);
        if (loginData.isBedrockPlayer()) player.addPotionEffect(BLINDNESS_EFFECT);
        if (file != null) {// Si tiene un archivo eso quiere decir que no pudo aplicar las propiedades al usuario
            if (file instanceof CacheLimboFile cacheLimbo){
                if (!cacheLimbo.isRestored()) {
                    aBoolean.set(false);
                    AviaTerraCore.enqueueTaskAsynchronously(() -> {
                        // Carga los datos del usuario
                        // Se realiza de manera asincrónica por qué no se requiere los datos del usuario para crear el LimboData
                        cacheLimbo.loadData();
                        MessagesManager.logConsole(String.format("Se restauro el usuario %s usando el .yaml", player.getName()), TypeMessages.INFO, CategoryMessages.LOGIN);
                    });
                }
            }
        }
        // Lo ejecuta con un Tick de delay apara que el addLimbodata pueda con tiempo crear el limboData para los de bedrock
        AviaTerraCore.enqueueTaskAsynchronously(() -> {
            if (aBoolean.get()) {// TODO: los de bedrock no funciona bien aveces lo pilla y aveces no
                CacheLimboFile limboFile = (CacheLimboFile) DataSection.getCacheLimboFlies()
                        .registerConfigFile(GlobalUtils.getRealUUID(player).toString());
                limboFile.saveData();
                limboFile.setRestored(false);
            }
            sendMessage(player, reasonLimbo);
        });
    }

    private final Location LIMBO_LOCATION = new Location(Bukkit.getWorlds().getFirst(), 0, 100, 0);

    private static void clearPlayer(Player player) {
        player.getInventory().clear();
        player.teleport(LIMBO_LOCATION);
        player.setOp(false);
        player.setGameMode(GameMode.SPECTATOR);
        player.setLevel(0);
        player.setAllowFlight(true);
        player.setFlying(true);
        player.setHealth(20);
        player.setFoodLevel(20);
        player.setExhaustion(0);
        player.setSaturation(0);
        player.setFireTicks(0);
        player.setInvisible(true);
        player.setInvulnerable(true);
        player.clearActivePotionEffects();
    }

    private void sendMessage(Player player, ReasonLimbo reasonLimbo) {
        switch (reasonLimbo){
            case NO_SESSION -> {
                MessagesManager.sendTitle(player, Message.LOGIN_LIMBO_INITIATED_BY_SESSION_TITLE.getMessage(player), Message.LOGIN_LIMBO_INITIATED_BY_SESSION_SUBTITLE.getMessage(player), 30, LIMBO_TIME, 30, TypeMessages.INFO);
                MessagesManager.sendMessage(player, Message.LOGIN_LIMBO_INITIATED_BY_SESSION_CHAT.getMessage(player), TypeMessages.INFO);
                startTimeOut(player, Message.LOGIN_LIMBO_TIME_OUT_SESSION.getMessage(player));
            }
            case NO_REGISTER -> {
                MessagesManager.sendTitle(player, Message.LOGIN_LIMBO_INITIATED_BY_REGISTER_TITLE.getMessage(player), Message.LOGIN_LIMBO_INITIATED_BY_REGISTER_SUBTITLE.getMessage(player), 30, LIMBO_TIME, 30, TypeMessages.INFO);
                MessagesManager.sendMessage(player, Message.LOGIN_LIMBO_INITIATED_BY_REGISTER_CHAT.getMessage(player), TypeMessages.INFO);
                startTimeOut(player, Message.LOGIN_LIMBO_TIME_OUT_REGISTER.getMessage(player));
            }
        }
    }

    private void addLimboData(Player player, LoginData loginData) {
        LimboData limboData = newLimboData(player);
        loginData.setLimbo(limboData);
        clearPlayer(player);
        LIST_IN_PROCESS.remove(player.getUniqueId());
    }

    private static @NotNull LimboData newLimboData(Player player) {
        return new LimboData(player.getGameMode(),
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
    }

    private void startTimeOut(Player player, String reason){
        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                if (LoginManager.checkLoginIn(player)) return;
                GlobalUtils.kickPlayer(player, reason);
            }
        }.runTaskLater(AviaTerraCore.getInstance(), LIMBO_TIME);
        LoginManager.getDataLogin(player).getLimbo().setTask(task);
    }
}