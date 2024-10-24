package net.atcore;

import com.github.games647.craftapi.resolver.MojangResolver;
import lombok.Getter;
import lombok.SneakyThrows;
import net.atcore.BaseCommand.CommandSection;
import net.atcore.Data.DataSection;
import net.atcore.Messages.TypeMessages;
import net.atcore.ListenerManager.ListenerManagerSection;
import net.atcore.Security.Login.LoginManager;
import net.atcore.Service.ServiceSection;
import net.atcore.Utils.GlobalUtils;
import net.atcore.Utils.RegisterManager;
import net.atcore.Moderation.ModerationSection;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import static net.atcore.Messages.MessagesManager.COLOR_SUCCESS;
import static net.atcore.Messages.MessagesManager.sendMessageConsole;
import static net.atcore.Security.Login.LoginManager.getDataLogin;

public final class AviaTerraCore extends JavaPlugin {
    @Getter
    private static AviaTerraCore instance;
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
        sendMessageConsole("AviaTerra Iniciado en <|" + (System.currentTimeMillis() - timeCurrent) + "ms", TypeMessages.SUCCESS, false);
    }

    @Override
    public void onDisable() {
        for (Section section : RegisterManager.sections){
            section.disable();
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!LoginManager.getListPlayerLoginIn().contains(player.getUniqueId())) {
                player.getInventory().setContents(getDataLogin(player).getLimbo().getItems());
            }
            GlobalUtils.kickPlayer(player, "El servidor va a cerrar, volveremos pronto...");
        }
        //disableModules();
        sendMessageConsole("AviaTerra Se fue a mimir.", TypeMessages.INFO, false);
    }

    @Override
    public void reloadConfig(){
        for (Section section : RegisterManager.sections){
            section.reloadConfig();
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
