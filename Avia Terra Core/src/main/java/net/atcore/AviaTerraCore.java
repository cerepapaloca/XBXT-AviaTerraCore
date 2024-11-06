package net.atcore;

import com.github.games647.craftapi.resolver.MojangResolver;
import lombok.Getter;
import lombok.SneakyThrows;
import net.atcore.command.CommandSection;
import net.atcore.data.DataSection;
import net.atcore.messages.TypeMessages;
import net.atcore.listenerManager.ListenerManagerSection;
import net.atcore.security.Login.LoginManager;
import net.atcore.service.ServiceSection;
import net.atcore.utils.GlobalConstantes;
import net.atcore.utils.GlobalUtils;
import net.atcore.utils.RegisterManager;
import net.atcore.moderation.ModerationSection;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.UUID;

import static net.atcore.messages.MessagesManager.COLOR_SUCCESS;
import static net.atcore.messages.MessagesManager.sendMessageConsole;
import static net.atcore.security.Login.LoginManager.getDataLogin;

public final class AviaTerraCore extends JavaPlugin {
    @Getter
    private static AviaTerraCore instance;
    private static HashMap<UUID, AviaTerraPlayer> players = new HashMap<>();
    public static final String TOKEN_BOT = "MTI5MTUzODM1MjY0NjEzMTc3NA.GDwtcq.azwlvX6fWKbusXk8sOyzRMK78Qe9CwbHy_pmWk";
    public static JDA BOT_DISCORD;
    @Getter private static LuckPerms LP;
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
        new GlobalConstantes();
        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) {
            LP = provider.getProvider();
        }
        if (Bukkit.getOnlineMode()){
            throw new IllegalStateException("modo online esta activo");
        }
        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            BOT_DISCORD = JDABuilder.createDefault(TOKEN_BOT).build();
            try {
                BOT_DISCORD.awaitReady();
                sendMessageConsole("discord bot" + COLOR_SUCCESS + " Ok", TypeMessages.INFO, false);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        RegisterManager.register(new CommandSection());
        RegisterManager.register(new ModerationSection());
        RegisterManager.register(new DataSection());
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
        Bukkit.getOnlinePlayers().forEach(player -> {
            if (LoginManager.getDataLogin(player) != null){
                if (!LoginManager.getListPlayerLoginIn().contains(player.getUniqueId())) {
                    player.getInventory().setContents(getDataLogin(player).getLimbo().getItems());
                    player.teleport(getDataLogin(player).getLimbo().getLocation());
                }
            }
            GlobalUtils.kickPlayer(player, "El servidor va a cerrar, volveremos pronto...");
        });
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
