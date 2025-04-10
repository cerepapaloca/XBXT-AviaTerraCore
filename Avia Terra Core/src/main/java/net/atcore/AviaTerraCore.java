package net.atcore;

import com.github.games647.craftapi.resolver.MojangResolver;
import lombok.Getter;
import lombok.Setter;
import net.atcore.achievement.AchievementSection;
import net.atcore.armament.ArmamentSection;
import net.atcore.command.CommandSection;
import net.atcore.data.DataSection;
import net.atcore.data.sql.DataBaseRegister;
import net.atcore.data.yml.ConfigFile;
import net.atcore.listener.ListenerSection;
import net.atcore.messages.CategoryMessages;
import net.atcore.messages.DiscordBot;
import net.atcore.messages.MessagesManager;
import net.atcore.messages.TypeMessages;
import net.atcore.moderation.ModerationSection;
import net.atcore.placeholder.PlaceHolderSection;
import net.atcore.security.SecuritySection;
import net.atcore.security.login.LoginManager;
import net.atcore.security.login.model.LoginData;
import net.atcore.utils.AviaRunnable;
import net.atcore.utils.GlobalUtils;
import net.atcore.utils.RegisterManager;
import net.atcore.webapi.ApiSection;
import net.dv8tion.jda.api.JDA;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.PluginCommand;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static net.atcore.utils.RegisterManager.register;
import static org.bukkit.Bukkit.getOnlinePlayers;


public class AviaTerraCore extends JavaPlugin {

    public static String tokenBot;
    public static final List<String> LIST_MOTD = new ArrayList<>();
    public static final List<String> LIST_BROADCAST = new ArrayList<>();
    @Nullable
    public static JDA jda;
    private static long startTime;
    @Getter private static LuckPerms lp;
    @Getter private static MojangResolver resolver;
    @Getter private static boolean isStarting = true;
    @Getter private static boolean isStopping = false;
    @Getter private static AviaTerraCore instance;
    @Getter private static MiniMessage miniMessage;
    @Setter private static long activeTime;

    @Override
    public void onLoad(){
        instance = this;
        resolver = new MojangResolver();
        miniMessage = MiniMessage.miniMessage();
        tokenBot = Objects.requireNonNullElseGet(DataSection.getConfigFile(),  () -> {
            ConfigFile configFile = new ConfigFile();
            DataSection.setConfigFile(configFile);
            return configFile;
        }).getFileYaml().getString("token-bot");
    }

    @Override
    public void onEnable() {
        MessagesManager.logConsole("AviaTerra Iniciando...", TypeMessages.SUCCESS, CategoryMessages.PRIVATE, false);
        startTime = System.currentTimeMillis();
        isStarting = true;
        workerThread = new Thread(this::processQueue);
        workerThread.setName("AviaTerraCore WorkerThread");
        workerThread.start();
        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) {
            lp = provider.getProvider();
        }else throw new RuntimeException("LuckPerms not found");
        if (Bukkit.getOnlineMode()) throw new IllegalStateException("modo online esta activo");
        DiscordBot.startDiscordBot();
        register(
                new AchievementSection(),
                new DataSection(),
                new CommandSection(),
                new ModerationSection(),
                new ListenerSection(),
                new SecuritySection(),
                new ArmamentSection(),
                new PlaceHolderSection(),
                new ApiSection()
        );
        registerPermission();
        startMOTD();
        startBroadcast();
        startAutoSaveTime();
        startAutoRestart();
        isStarting = false;
        messageOn(startTime);
    }

    private void registerPermission(){
        PluginManager pm = Bukkit.getPluginManager();
        for (int i = 4; i < 32; i++){
            pm.addPermission(new Permission(this.getName().toLowerCase() + ".viewdistance." + i, "Asigna la distancia de renderizado"));
            pm.addPermission(new Permission(this.getName().toLowerCase() + ".simulationdistance." + i, "Asigna la distancia de simulación"));
            pm.addPermission(new Permission(this.getName().toLowerCase() + ".maxhome." + i, "La cantidad maxima de home que puede tener"));
        }
        for (Command command : Bukkit.getCommandMap().getKnownCommands().values()){
            if (command.getPermission() == null){
                PluginCommand pluginCommand = Bukkit.getPluginCommand(command.getName());
                if (pluginCommand != null){
                    try {
                        pm.addPermission(new Permission(this.getName().toLowerCase() + ".command." + pluginCommand.getName() + "." + command.getName()));
                    }catch (Exception ignored){
                        // En caso de que el comando tenga alise saltara una excepción
                    }
                }
            }
        }
    }

    public static long getActiveTime(){
        return activeTime + (System.currentTimeMillis() - startTime);
    }

    @Override
    public void onDisable() {
        isStopping = true;
        // Borra a los jugadores cracked y semi cracked que no pudieron registrarse para evitar tener jugadores fantasmas
        DataSection.getConfigFile().saveActiveTime();
        for (Section section : RegisterManager.sections){
            section.disable();
        }
        LIST_BROADCAST.clear();
        LIST_MOTD.clear();
        for (LoginData data : LoginManager.getDataLogin()){
            // Cuidado con esta condición por qué puede borrar todas la cuentas
            if (data.getRegister().isTemporary()){
                DataBaseRegister.removeRegister(data.getRegister().getUsername(), "Servidor (EL registro es temporal)");
                LoginManager.removeDataLogin(data.getRegister().getUsername());
                /*if (!Service.removeStatsPlayer(player.getUniqueId())){
                    MessagesManager.logConsole(String.format("Error al borrar las stats del jugador <|%s|>", player.getName()), TypeMessages.WARNING);
                }
                if (!AviaTerraPlayer.getPlayer(player).getPlayerDataFile().getFile().delete()){
                    MessagesManager.logConsole(String.format("Error al borrar ATPF del jugador <|%s|>", player.getName()), TypeMessages.WARNING);
                }
                if (!Service.removePlayerData(player.getUniqueId())){
                    MessagesManager.logConsole(String.format("Error al borrar PlayerData del jugador <|%s|>", player.getName()), TypeMessages.WARNING);
                }*/
            }
        }
        if (DiscordBot.stateTasks != null) DiscordBot.stateTasks.cancel();
        getOnlinePlayers().forEach(player -> {
            DataBaseRegister.checkRegister(player, GlobalUtils.getRealUUID(player));
            GlobalUtils.kickPlayer(player, "El servidor va a cerrar, volveremos pronto...");
        });
        taskQueue.clear();
        DiscordBot.handler.shutdown();
        workerThread.interrupt();
        isStopping = false;
        MessagesManager.logConsole("AviaTerra Se fue a dormir cómodamente", TypeMessages.SUCCESS, CategoryMessages.SYSTEM, false);
    }

    @Override
    public void reloadConfig(){
        DiscordBot.handler.shutdown();
        DiscordBot.startDiscordBot();
        for (Section section : RegisterManager.sections){
            section.reload();
        }
    }

    private void messageOn(long timeCurrent){
        MessagesManager.logConsole("AviaTerra Iniciado en <|" + (System.currentTimeMillis() - timeCurrent) + "ms" +
                "\n" +
                "<gradient:#571ecf:#3f39ea> ___    ___ ________     </gradient><gradient:#fb8015:#fbb71f> ___    ___ _________   </gradient>\n" +
                "<gradient:#571ecf:#3f39ea>|\\  \\  /  /|\\   __  \\    </gradient><gradient:#fb8015:#fbb71f>|\\  \\  /  /|\\___   ___\\ </gradient>\n" +
                "<gradient:#571ecf:#3f39ea>\\ \\  \\/  / | \\  \\|\\ /_   </gradient><gradient:#fb8015:#fbb71f>\\ \\  \\/  / ||___ \\  \\_| </gradient>\n" +
                "<gradient:#571ecf:#3f39ea> \\ \\    / / \\ \\   __  \\  </gradient><gradient:#fb8015:#fbb71f> \\ \\    / /     \\ \\  \\  </gradient>\n" +
                "<gradient:#571ecf:#3f39ea>  /     \\/   \\ \\  \\|\\  \\ </gradient><gradient:#fb8015:#fbb71f>  /     \\/       \\ \\  \\ </gradient>\n" +
                "<gradient:#571ecf:#3f39ea> /  /\\   \\    \\ \\_______\\ </gradient><gradient:#fb8015:#fbb71f>/  /\\   \\        \\ \\__\\ </gradient>\n" +
                "<gradient:#571ecf:#3f39ea>/__/ /\\ __\\    \\|_______|</gradient><gradient:#fb8015:#fbb71f>/__/ /\\ __\\        \\|__|</gradient>\n" +
                "<gradient:#571ecf:#3f39ea>|__|/ \\|__|              </gradient><gradient:#fb8015:#fbb71f>|__|/ \\|__|             </gradient>",
                TypeMessages.SUCCESS, CategoryMessages.PRIVATE, false);
    }
    /*De pronto en un futuro se use
    public static void runTask(Runnable task){
        Bukkit.getScheduler().runTask(AviaTerraCore.getInstance(), task);
    }

    public static void runTaskLater(long delay, Runnable task){
        Bukkit.getScheduler().runTaskLater(AviaTerraCore.getInstance(), task, delay);
    }

    public static void runTaskLaterAsynchronously(long delay, Runnable task){
        Bukkit.getScheduler().runTaskLaterAsynchronously(AviaTerraCore.getInstance(), task, delay);
    }

    public static void runTaskTimer(long delay, long period,Runnable task){
        Bukkit.getScheduler().runTaskTimer(AviaTerraCore.getInstance(), task, delay, period);
    }

    public static void runTaskTimerAsynchronously(long delay, long period, Runnable task){
        Bukkit.getScheduler().runTaskTimerAsynchronously(AviaTerraCore.getInstance(), task, delay, period);
    }*/

    /**
     * @see #enqueueTaskAsynchronously(boolean, Runnable)
     * @param task La tarea que se va añadir
     */

    public static void enqueueTaskAsynchronously(Runnable task) {
        enqueueTaskAsynchronously(false, task);
    }

    public static void taskSynchronously(Runnable task) {
        Bukkit.getScheduler().runTask(AviaTerraCore.getInstance(), task);
    }

    private static final ExecutorService EXECUTOR = Executors.newCachedThreadPool();
    private Thread workerThread;
    public static final BlockingQueue<AviaRunnable> taskQueue = new LinkedBlockingQueue<>();
    public static volatile LinkedList<telemetryTask> telemetryTasks = new LinkedList<>();
    public static volatile ArrayList<Integer> amountTask = new ArrayList<>();
    public static volatile AtomicInteger currentAoumt = new AtomicInteger(0);

    /**
     * Realiza tareas de manera asincrónica y lo añade a una cola para evitar problemas de sincronización y que se haga
     * los proceso de manera consecutiva.
     * <p>
     * Si la tarea tarda mucho en realizarse mucho en realize (<1000 ms) salta una excepción indicando el problema
     * </p>
     * @param task El proceso que va a realizar
     * @param isHeavyProcess indica si la tarea es pesada haciendo una omisión del waring que se
     *                       produce cuando la tarea tarda en completable
     */

    public static void enqueueTaskAsynchronously(boolean isHeavyProcess, Runnable task) {
        if (!taskQueue.offer(new AviaRunnable(task, isHeavyProcess))){
            MessagesManager.logConsole("Error al añadir una tarea la cola", TypeMessages.ERROR);
        }
        if (taskQueue.size() >= 20){
            MessagesManager.logConsole(String.format("Hay <|%s|> tareas en cola, Hilo sobre cargador \n" +
                    Arrays.stream(Thread.currentThread().getStackTrace()).toList().get(3), taskQueue.size()), TypeMessages.WARNING);
        }
    }


    private void processQueue() {
        new BukkitRunnable(){
            @Override
            public void run() {
                if (amountTask.size() >= 5) {
                    amountTask.removeFirst(); // Elimina el más antiguo
                }
                amountTask.add(currentAoumt.get());
                currentAoumt.set(0);
            }
        }.runTaskTimerAsynchronously(this, 0, 20*60*2);
        try {
            while (!Thread.currentThread().isInterrupted()) {
                AviaRunnable task = taskQueue.take();
                long startTime = System.nanoTime();

                Future<?> future = EXECUTOR.submit(task);
                try {
                    future.get(1000*45, TimeUnit.MILLISECONDS);
                    long elapsedNanos = System.nanoTime() - startTime;
                    /*if (elapsedNanos > 1_000_000_000L && !task.isHeavyProcess()) { // 1s en nanosegundos
                        StringBuilder builder = getStackTrace(new Exception().getStackTrace());
                        AviaTerraCore.getInstance().getLogger().warning(
                                String.format("La tarea tardó %s ms en procesarse", elapsedNanos * 0.000001D) + "\n" + builder
                        );
                    }*/
                    if (telemetryTasks.size() >= 200) {
                        telemetryTasks.removeFirst(); // Elimina el más antiguo
                    }
                    telemetryTasks.add(new telemetryTask(System.currentTimeMillis(), elapsedNanos * 0.000001D,  taskQueue.size()));
                    currentAoumt.set(currentAoumt.get() + 1);
                } catch (TimeoutException e) {
                    StringBuilder builder = getStackTrace(e.getCause().getStackTrace());
                    future.cancel(true); // Cancelamos la tarea si tarda demasiado
                    AviaTerraCore.getInstance().getLogger().severe("La tarea fue cancelada por que tardo mucho en procesarse" + "\n" + builder);
                } catch (ExecutionException e) {
                    StringBuilder builder = getStackTrace(e.getCause().getStackTrace());
                    AviaTerraCore.getInstance().getLogger().severe("Hubo un error al iniciar la tarea [" + e.getMessage() + "]" + "\n" + builder);
                }

            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            if (!Bukkit.isStopping()) MessagesManager.sendErrorException("Hilo del AviaTerra hubo una excepción de interrupción", e);
        }
    }

    private static @NotNull StringBuilder getStackTrace(StackTraceElement[] traceElements) {
        StringBuilder builder = new StringBuilder();
        for (StackTraceElement element : traceElements) {
            builder.append(element.toString()).append("\n\t");
        }
        return builder;
    }

    public record telemetryTask(long currentTime, double elapsedProcess, int queue) {}

    /*
    public static void initModules() {
        for (String className : LIST_MODULE) {
            try {
                Class<?> clazz = Class.forName(className);
                if (Module.class.isAssignableFrom(clazz)) {
                    Module module = (Module) clazz.getDeclaredConstructor().newInstance();
                    module.init();
                    System.out.println("Módulo cargado: " + className);
                } else {
                    System.out.println("La clase " + className + " no implementa Module.");
                }
            } catch (Exception e) {
                MessagesManager.sendWaringException("Error al cargar el modulo de: " + className, e);
            }
        }
    }*/


    private void startBroadcast(){
        Random random = new Random();
        new BukkitRunnable() {
            public void run() {
                if (getOnlinePlayers().isEmpty()) return;
                if (LIST_BROADCAST.isEmpty()) return;
                int randomInt = random.nextInt(LIST_BROADCAST.size());
                Bukkit.broadcast(MessagesManager.applyFinalProprieties(LIST_BROADCAST.get(randomInt), TypeMessages.INFO, CategoryMessages.PRIVATE, true));
            }
        }.runTaskTimer(this, 20*60*15L, 20*60*15L);
    }

    private void startAutoSaveTime(){
        new BukkitRunnable() {
            @Override
            public void run() {
                DataSection.getConfigFile().saveActiveTime();
            }
        }.runTaskTimerAsynchronously(this, 20*60*15L, 20*60*15L);
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

    private void startAutoRestart(){
        new BukkitRunnable() {
            public void run() {
                MessagesManager.logConsole("Reinició automático iniciado", TypeMessages.INFO, CategoryMessages.SYSTEM);
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "passiverestart");
            }
        }.runTaskLater(this, 20*60*60*24L*2);
    }


}
