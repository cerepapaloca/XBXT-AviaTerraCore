package net.atcore;

import com.github.games647.craftapi.resolver.MojangResolver;
import lombok.Getter;
import net.atcore.armament.ArmamentSection;
import net.atcore.command.CommandSection;
import net.atcore.data.DataSection;
import net.atcore.messages.CategoryMessages;
import net.atcore.messages.MessageSection;
import net.atcore.messages.MessagesManager;
import net.atcore.messages.MessagesType;
import net.atcore.listener.ListenerSection;
import net.atcore.security.Login.model.LoginData;
import net.atcore.security.Login.LoginManager;
import net.atcore.security.SecuritySection;
import net.atcore.utils.GlobalUtils;
import net.atcore.utils.Gradient;
import net.atcore.utils.RegisterManager;
import net.atcore.moderation.ModerationSection;
import net.dv8tion.jda.api.JDA;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static net.atcore.messages.MessagesManager.addTextComponent;
import static net.atcore.messages.MessagesManager.sendMessageConsole;
import static net.atcore.utils.RegisterManager.register;

public final class AviaTerraCore extends JavaPlugin {

    private static final BlockingQueue<Runnable> TASK_QUEUE = new LinkedBlockingQueue<>();
    private Thread workerThread;

    public static final String TOKEN_BOT = "MTI5MTUzODM1MjY0NjEzMTc3NA.GDwtcq.azwlvX6fWKbusXk8sOyzRMK78Qe9CwbHy_pmWk";
    public static final List<String> LIST_MOTD = new ArrayList<>();
    public static final List<String> LIST_BROADCAST = new ArrayList<>();
    public static JDA jda;
    @Getter private static LuckPerms lp;
    @Getter private static MojangResolver resolver;
    @Getter private static boolean isStarting;
    @Getter private static AviaTerraCore instance;
    @Getter private static MiniMessage miniMessage;


    @Override
    public void onLoad(){
        instance = this;
        resolver = new MojangResolver();
        miniMessage = MiniMessage.miniMessage();
    }

    @Override
    public void onEnable() {
        long timeCurrent = System.currentTimeMillis();
        sendMessageConsole("AviaTerra Iniciando...", MessagesType.SUCCESS, CategoryMessages.PRIVATE, false);
        isStarting = true;
        workerThread = new Thread(this::processQueue);
        workerThread.setName("AviaTerraCore WorkerThread");
        workerThread.start();
        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) {
            lp = provider.getProvider();
        }
        if (Bukkit.getOnlineMode()){
            throw new IllegalStateException("modo online esta activo");
        }
        register(new DataSection(),// Los Datos Primero
                new MessageSection(),
                new CommandSection(),
                new ModerationSection(),
                new ListenerSection(),
                new SecuritySection(),
                new ArmamentSection()
        );
        startMOTD();
        startBroadcast();
        isStarting = false;
        messageOn(timeCurrent);
    }

    @Override
    public void onDisable() {
        for (Section section : RegisterManager.sections){
            section.disable();
        }
        LIST_BROADCAST.clear();

        workerThread.interrupt();
        Bukkit.getOnlinePlayers().forEach(player -> {
            LoginData loginData = LoginManager.getDataLogin(player);
            if (loginData != null){
                if (LoginManager.isLimboMode(player)) {
                    loginData.getLimbo().restorePlayer(player);
                }
            }
            GlobalUtils.kickPlayer(player, "El servidor va a cerrar, volveremos pronto...");
        });
        //if (jda != null) jda.shutdown();
        sendMessageConsole("AviaTerra Apagada", MessagesType.SUCCESS, CategoryMessages.PRIVATE, false);
    }

    @Override
    public void reloadConfig(){
        for (Section section : RegisterManager.sections){
            section.reload();
        }
    }

    private void messageOn(long timeCurrent){
        sendMessageConsole("AviaTerra Iniciado en <|" + (System.currentTimeMillis() - timeCurrent) + "ms" +
                "\n" +
                " ________  ___      ___ ___  ________          _________  _______   ________  ________  ________     \n" +
                "|\\   __  \\|\\  \\    /  /|\\  \\|\\   __  \\        |\\___   ___\\\\  ___ \\ |\\   __  \\|\\   __  \\|\\   __  \\    \n" +
                "\\ \\  \\|\\  \\ \\  \\  /  / | \\  \\ \\  \\|\\  \\       \\|___ \\  \\_\\ \\   __/|\\ \\  \\|\\  \\ \\  \\|\\  \\ \\  \\|\\  \\   \n" +
                " \\ \\   __  \\ \\  \\/  / / \\ \\  \\ \\   __  \\           \\ \\  \\ \\ \\  \\_|/_\\ \\   _  _\\ \\   _  _\\ \\   __  \\  \n" +
                "  \\ \\  \\ \\  \\ \\    / /   \\ \\  \\ \\  \\ \\  \\           \\ \\  \\ \\ \\  \\_|\\ \\ \\  \\\\  \\\\ \\  \\\\  \\\\ \\  \\ \\  \\ \n" +
                "   \\ \\__\\ \\__\\ \\__/ /     \\ \\__\\ \\__\\ \\__\\           \\ \\__\\ \\ \\_______\\ \\__\\\\ _\\\\ \\__\\\\ _\\\\ \\__\\ \\__\\\n" +
                "    \\|__|\\|__|\\|__|/       \\|__|\\|__|\\|__|            \\|__|  \\|_______|\\|__|\\|__|\\|__|\\|__|\\|__|\\|__|\n",
                MessagesType.SUCCESS, CategoryMessages.PRIVATE, false);
    }

    /**
     * Realiza tareas de manera asincrónica y lo añade a una cola
     * para evitar problemas de sincronización
     */

    public void enqueueTaskAsynchronously(Runnable task) {
        if (!TASK_QUEUE.offer(task)){
            MessagesManager.sendMessageConsole("Error al añadir una tarea la cola", MessagesType.ERROR);
        }
    }

    private void processQueue() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                // Toma una tarea de la cola y la ejecuta
                Runnable task = TASK_QUEUE.take();
                task.run();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Si es interrumpido, detenemos el hilo
        }
    }

    private void startBroadcast(){
        Random random = new Random();
        new BukkitRunnable() {
            public void run() {
                int randomInt = random.nextInt(LIST_BROADCAST.size());
                Bukkit.broadcast(MessagesManager.applyFinalProprieties(LIST_BROADCAST.get(randomInt), MessagesType.INFO, CategoryMessages.PRIVATE, true));
            }
        }.runTaskTimer(this, 20*60*5L, 20*60*5L);
    }

    private void startMOTD(){
        Random random = new Random();
        new BukkitRunnable() {
            public void run() {
                if (LIST_MOTD.isEmpty()) return;
                int randomInt = random.nextInt(LIST_MOTD.size());
                Gradient xb = new Gradient("XB", 'l').addGradient(new Color(75, 47, 222), 1);
                Gradient xt = new Gradient("XT", 'l').addGradient(new Color(255, 140, 0), 1);
                Bukkit.motd(addTextComponent(String.format("§6§l%s§r  §6%s \n§1%s", xb, LIST_MOTD.get(randomInt), xt)));
            }
        }.runTaskTimer(this, 20L, 20L);
    }
}
