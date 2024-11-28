package net.atcore.security.Login;

import lombok.experimental.UtilityClass;
import net.atcore.AviaTerraCore;
import net.atcore.data.ActionInReloadYaml;
import net.atcore.data.DataSection;
import net.atcore.data.FileYaml;
import net.atcore.data.FilesYams;
import net.atcore.messages.MessagesManager;
import net.atcore.messages.TypeMessages;
import net.atcore.utils.GlobalUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;

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
        String uuidString = player.getUniqueId().toString();
        FileYaml file = DataSection.getFliesCacheLimbo().getConfigFile(uuidString, false);
        DataLimbo dataLimbo;
        if (file == null){// Si tiene un archivo eso quiere decir que no pudo aplicar las propiedades al usuario
            dataLimbo = new DataLimbo(player.getGameMode(),// TODO aplicar un sistema para evitar problemas donde el yml no se llegue a borrar
                    player.getInventory().getContents(),
                    player.getLocation(),
                    player.isOp(),
                    player.getLevel());
            dataLogin.setLimbo(dataLimbo);
            // Se guarda los datos cuando sé crear el limboData esto es solo por si hubo problema grave con el servidor
            AviaTerraCore.getInstance().enqueueTaskAsynchronously(() -> DataSection.getFliesCacheLimbo().registerConfigFile(uuidString, ActionInReloadYaml.SAVE));
        }else {
            Bukkit.getLogger().severe("B");// TODO borrar esto cuando termine con las pruebas
            // Carga los datos del usuario
            file.loadData();
            dataLimbo = dataLogin.getLimbo();
        }
        player.getInventory().clear();
        player.teleport(player.getWorld().getSpawnLocation());
        player.setOp(false);
        player.setGameMode(GameMode.SPECTATOR);
        player.setLevel(0);
        player.setAllowFlight(true);

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