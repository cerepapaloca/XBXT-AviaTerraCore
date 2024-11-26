package net.atcore;

import com.github.games647.craftapi.resolver.MojangResolver;
import lombok.Getter;
import lombok.SneakyThrows;
import net.atcore.command.CommandSection;
import net.atcore.data.DataSection;
import net.atcore.messages.MessageSection;
import net.atcore.messages.TypeMessages;
import net.atcore.listener.ListenerManagerSection;
import net.atcore.security.Login.DataLogin;
import net.atcore.security.Login.LoginManager;
import net.atcore.service.ServiceSection;
import net.atcore.utils.GlobalUtils;
import net.atcore.utils.RegisterManager;
import net.atcore.moderation.ModerationSection;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static net.atcore.messages.MessagesManager.sendMessageConsole;
import static net.atcore.security.Login.LoginManager.getDataLogin;

public final class AviaTerraCore extends JavaPlugin {

    private final BlockingQueue<Runnable> taskQueue = new LinkedBlockingQueue<>();
    private Thread workerThread;

    @Getter
    private static AviaTerraCore instance;
    public static final String TOKEN_BOT = "MTI5MTUzODM1MjY0NjEzMTc3NA.GDwtcq.azwlvX6fWKbusXk8sOyzRMK78Qe9CwbHy_pmWk";
    public static JDA jda;
    @Getter private static LuckPerms lp;
    @Getter private static MojangResolver resolver;
    @Getter private static boolean isStarting;

    @Override
    public void onLoad(){
        instance = this;
        resolver = new MojangResolver();
    }

    @SneakyThrows
    @Override
    public void onEnable() {
        long timeCurrent = System.currentTimeMillis();
        sendMessageConsole("AviaTerra Iniciando...", TypeMessages.INFO, false);
        isStarting = true;
        workerThread = new Thread(this::processQueue);
        workerThread.start();
        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) {
            lp = provider.getProvider();
        }
        if (Bukkit.getOnlineMode()){
            throw new IllegalStateException("modo online esta activo");
        }
        RegisterManager.register(new DataSection());// Los Datos Primero
        RegisterManager.register(new MessageSection());
        RegisterManager.register(new CommandSection());
        RegisterManager.register(new ModerationSection());
        RegisterManager.register(new ListenerManagerSection());
        RegisterManager.register(new ServiceSection());
        //enableModules();
        isStarting = false;
        messageOn(timeCurrent);
    }

    @Override
    public void onDisable() {
        for (Section section : RegisterManager.sections){
            section.disable();
        }
        workerThread.interrupt();
        Bukkit.getOnlinePlayers().forEach(player -> {
            DataLogin dataLogin = LoginManager.getDataLogin(player);
            if (dataLogin != null){
                if (LoginManager.isLimboMode(player)) {
                    dataLogin.getLimbo().restorePlayer(player);
                    dataLogin.setLimbo(null);
                }
            }
            GlobalUtils.kickPlayer(player, "El servidor va a cerrar, volveremos pronto...");
        });
        //jda.shutdown();
        //disableModules();
        sendMessageConsole("AviaTerra Se fue a mimir.", TypeMessages.INFO, false);
    }

    @Override
    public void reloadConfig(){
        for (Section section : RegisterManager.sections){
            section.reloadConfig();
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
                TypeMessages.SUCCESS, false);
    }

    /**
     * Se tiene usar cuando se va a hacer algo relacionado con la base de datos
     * esto para evitar problemás de ejecutar varias peticiones simultáneas
     * a la base de datos
     */

    public void enqueueTaskAsynchronously(Runnable task) {
        taskQueue.offer(task);
    }

    private void processQueue() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                // Toma una tarea de la cola y la ejecuta
                Runnable task = taskQueue.take();
                task.run();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Si es interrumpido, detenemos el hilo
        }
    }


    //no borrar
    /*
    private void enableModules() {
        Reflections reflections = new Reflections("net");
        Set<Class<? extends Module>> annotatedClasses = reflections.getSubTypesOf(Module.class);

        for (Class<? extends Module> clazz : annotatedClasses) {
            try {
                Module module = clazz.getDeclaredConstructor().newInstance();
                module.enable();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
    }
    //no borrar
    private void disableModules() {
        Reflections reflections = new Reflections("net");
        Set<Class<? extends Module>> annotatedClasses = reflections.getSubTypesOf(Module.class);

        for (Class<? extends Module> clazz : annotatedClasses) {
            try {
                Module module = clazz.getDeclaredConstructor().newInstance();
                module.disable();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
    }
    */
}
