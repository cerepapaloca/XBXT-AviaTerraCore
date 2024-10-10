package net.atcore;

import net.atcore.BaseCommand.CommandSection;
import net.atcore.Messages.TypeMessages;
import net.atcore.Security.SecuritySection;
import net.atcore.Utils.RegisterManager;
import net.atcore.Moderation.ModerationSection;
import org.bukkit.plugin.java.JavaPlugin;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.util.Set;

import static net.atcore.Messages.MessagesManager.sendMessageConsole;

public final class AviaTerraCore extends JavaPlugin {

    public static AviaTerraCore plugin;
    public static long timeCurrent;

    @Override
    public void onLoad(){
        plugin = this;
    }

    @Override
    public void onEnable() {
        timeCurrent = System.currentTimeMillis();
        sendMessageConsole("Avia Terra Iniciando...", TypeMessages.INFO, false);
        RegisterManager.register(new CommandSection());
        RegisterManager.register(new ModerationSection());
        RegisterManager.register(new SecuritySection());
        //RegisterManager.register(new DataSection());
        enableModules();
        sendMessageConsole("Avia Terra Iniciado. " + "&6" + (System.currentTimeMillis() - timeCurrent) + "ms", TypeMessages.SUCCESS, false);
    }

    @Override
    public void onDisable() {
        for (Section section : RegisterManager.sections){
            section.disable();
        }
        disableModules();
        sendMessageConsole("Avia Terra Se fue a mimir.", TypeMessages.INFO, false);
    }

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
}
