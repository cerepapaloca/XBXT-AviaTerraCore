package net.atcore;

import lombok.SneakyThrows;
import net.atcore.BaseCommand.CommandSection;
import net.atcore.Data.DataSection;
import net.atcore.Messages.TypeMessages;
import net.atcore.Security.SecuritySection;
import net.atcore.Utils.RegisterManager;
import net.atcore.Moderation.ModerationSection;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.bukkit.plugin.java.JavaPlugin;

import static net.atcore.Messages.MessagesManager.sendMessageConsole;

public final class AviaTerraCore extends JavaPlugin {

    public static AviaTerraCore PLUGIN;
    public static long timeCurrent;
    public static final String TOKEN_BOT = "MTI5MTUzODM1MjY0NjEzMTc3NA.GDwtcq.azwlvX6fWKbusXk8sOyzRMK78Qe9CwbHy_pmWk";

    @Override
    public void onLoad(){
        PLUGIN = this;
    }

    @SneakyThrows
    @Override
    public void onEnable() {
        timeCurrent = System.currentTimeMillis();
        sendMessageConsole("AviaTerra Iniciando...", TypeMessages.INFO, false);
        RegisterManager.register(new CommandSection());
        RegisterManager.register(new ModerationSection());
        RegisterManager.register(new SecuritySection());
        RegisterManager.register(new DataSection());
        JDA jda = JDABuilder.createDefault(TOKEN_BOT).build();
        jda.awaitReady();
        //enableModules();
        sendMessageConsole("AviaTerra Iniciado. <|" + (System.currentTimeMillis() - timeCurrent) + "ms", TypeMessages.SUCCESS, false);
    }

    @Override
    public void onDisable() {
        for (Section section : RegisterManager.sections){
            section.disable();
        }
        //disableModules();
        sendMessageConsole("AviaTerra Se fue a mimir.", TypeMessages.INFO, false);
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
