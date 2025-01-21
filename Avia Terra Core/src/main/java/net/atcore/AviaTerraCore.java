package net.atcore;

import com.github.games647.craftapi.resolver.MojangResolver;
import lombok.Getter;
import net.atcore.armament.ArmamentSection;
import net.atcore.command.CommandManager;
import net.atcore.command.CommandSection;
import net.atcore.data.DataSection;
import net.atcore.messages.*;
import net.atcore.listener.ListenerSection;
import net.atcore.messages.ConsoleDiscord;
import net.atcore.security.SecuritySection;
import net.atcore.utils.AviaRunnable;
import net.atcore.utils.GlobalUtils;
import net.atcore.utils.RegisterManager;
import net.atcore.moderation.ModerationSection;
import net.dv8tion.jda.api.JDA;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static net.atcore.messages.MessagesManager.sendMessageConsole;
import static net.atcore.utils.RegisterManager.register;

public final class AviaTerraCore extends JavaPlugin {

    private static final BlockingQueue<AviaRunnable> TASK_QUEUE = new LinkedBlockingQueue<>();
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
        register(
                new MessageSection(),
                new DataSection(),// Los Datos Primero
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
        if (ConsoleDiscord.stateTasks != null) ConsoleDiscord.stateTasks.cancel();
        workerThread.interrupt();
        Bukkit.getOnlinePlayers().forEach(player -> {
            GlobalUtils.kickPlayer(player, "El servidor va a cerrar, volveremos pronto...");
        });
        //if (jda != null) jda
        TASK_QUEUE.clear();
        sendMessageConsole("XB XT Apagada", MessagesType.SUCCESS, CategoryMessages.PRIVATE, false);
    }

    @Override
    public void reloadConfig(){
        for (Section section : RegisterManager.sections){
            section.reload();
        }
    }

    private void messageOn(long timeCurrent){
        sendMessageConsole("XB XT Iniciado en <|" + (System.currentTimeMillis() - timeCurrent) + "ms" +
                "\n" +
                "<gradient:#571ecf:#3f39ea> ___    ___ ________     </gradient><gradient:#fb8015:#fbb71f> ___    ___ _________   </gradient>\n" +
                "<gradient:#571ecf:#3f39ea>|\\  \\  /  /|\\   __  \\    </gradient><gradient:#fb8015:#fbb71f>|\\  \\  /  /|\\___   ___\\ </gradient>\n" +
                "<gradient:#571ecf:#3f39ea>\\ \\  \\/  / | \\  \\|\\ /_   </gradient><gradient:#fb8015:#fbb71f>\\ \\  \\/  / ||___ \\  \\_| </gradient>\n" +
                "<gradient:#571ecf:#3f39ea> \\ \\    / / \\ \\   __  \\  </gradient><gradient:#fb8015:#fbb71f> \\ \\    / /     \\ \\  \\  </gradient>\n" +
                "<gradient:#571ecf:#3f39ea>  /     \\/   \\ \\  \\|\\  \\ </gradient><gradient:#fb8015:#fbb71f>  /     \\/       \\ \\  \\ </gradient>\n" +
                "<gradient:#571ecf:#3f39ea> /  /\\   \\    \\ \\_______\\ </gradient><gradient:#fb8015:#fbb71f>/  /\\   \\        \\ \\__\\ </gradient>\n" +
                "<gradient:#571ecf:#3f39ea>/__/ /\\ __\\    \\|_______|</gradient><gradient:#fb8015:#fbb71f>/__/ /\\ __\\        \\|__|</gradient>\n" +
                "<gradient:#571ecf:#3f39ea>|__|/ \\|__|              </gradient><gradient:#fb8015:#fbb71f>|__|/ \\|__|             </gradient>",
                MessagesType.SUCCESS, CategoryMessages.PRIVATE, false);
    }

    /**
     * @see #enqueueTaskAsynchronously(boolean, Runnable)
     * @param task La tarea que se va añadir
     */

    public static void enqueueTaskAsynchronously(Runnable task) {
        enqueueTaskAsynchronously(false, task);
    }

    /**
     * Realiza tareas de manera asincrónica y lo añade a una cola para evitar problemas de sincronización y que se haga
     * los proceso de manera consecutiva.
     * <p>
     * Si la tarea tarda mucho en realizarse mucho en realize (<1000 ms) salta una excepción indicando el problema
     * @param task El proceso que va a realizar
     * @param isHeavyProcess indica si la tarea es pesada haciendo una omisión del waring que se
     *                       produce cuando la tarea tarda en completable
     */

    public static void enqueueTaskAsynchronously(boolean isHeavyProcess, Runnable task) {
        if (!TASK_QUEUE.offer(new AviaRunnable(task, isHeavyProcess))){
            MessagesManager.sendMessageConsole("Error al añadir una tarea la cola", MessagesType.ERROR);
        }
        if (TASK_QUEUE.size() > 6){
            MessagesManager.sendMessageConsole(String.format("Hay <|%s|> tareas en cola, Hilo sobre cargador", TASK_QUEUE.size()), MessagesType.WARNING, CategoryMessages.SYSTEM, false);
        }
    }

    private void processQueue() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                // Toma una tarea de la cola y la ejecuta
                AviaRunnable task = TASK_QUEUE.take();
                long startTime = System. nanoTime();
                task.run();
                long elapsedNanos = System. nanoTime() - startTime;
                // 1000 ms
                if (elapsedNanos > 1000000*1000 && !task.isHeavyProcess()){
                    StringBuilder builder = new StringBuilder();
                    for (StackTraceElement element : task.getStackTraceElements()) builder.append(element.toString()).append("\n\t");
                    AviaTerraCore.getInstance().getLogger().warning(String.format("La tarea tardo %s Ms en procesarse", elapsedNanos*0.000001D) + "\n" + builder);
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Si es interrumpido, detenemos el hilo
        }
    }

    private void startBroadcast(){
        Random random = new Random();
        new BukkitRunnable() {
            public void run() {
                if (LIST_BROADCAST.isEmpty()) return;
                int randomInt = random.nextInt(LIST_BROADCAST.size());
                Bukkit.broadcast(MessagesManager.applyFinalProprieties(LIST_BROADCAST.get(randomInt), MessagesType.INFO, CategoryMessages.PRIVATE, true));
            }
        }.runTaskTimer(this, 20*60*15L, 20*60*15L);
    }

    private void startMOTD(){
        Random random = new Random();
        new BukkitRunnable() {
            public void run() {
                if (LIST_MOTD.isEmpty()) return;
                int randomInt = random.nextInt(LIST_MOTD.size());
                Bukkit.motd(AviaTerraCore.getMiniMessage().deserialize(String.format("<#4B2FDE><bold>XB<reset>  <gold>%s \n<#FF8C00><bold>XT", LIST_MOTD.get(randomInt))));
            }
        }.runTaskTimer(this, 20L, 20L);
    }
}
