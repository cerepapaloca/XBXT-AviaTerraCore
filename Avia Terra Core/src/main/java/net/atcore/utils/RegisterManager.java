package net.atcore.utils;

import lombok.experimental.UtilityClass;
import net.atcore.AviaTerraCore;
import net.atcore.Section;
import net.atcore.command.BaseCommand;
import net.atcore.command.CommandHandler;
import net.atcore.command.CommandSection;
import net.atcore.messages.CategoryMessages;
import net.atcore.messages.MessagesManager;
import net.atcore.messages.TypeMessages;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;

import static net.atcore.messages.MessagesManager.logConsole;
import static org.bukkit.Bukkit.getServer;

/**
 * Esta es una clase que se dedica a registrar e inicializar los listener o comando u otras cosas
 */
@UtilityClass
@SuppressWarnings("removal")
public class RegisterManager {

    public HashSet<Section> sections = new HashSet<>();

    public void register(Listener @NotNull ... listeners) {
        for (Listener listener : listeners) {
            getServer().getPluginManager().registerEvents(listener, AviaTerraCore.getInstance());
        }
    }

    public void register(@NotNull Section... sections) {
        for (Section section : sections) {
            try {
                section.enable();
                RegisterManager.sections.add(section);
                logConsole(section.getName() + TypeMessages.SUCCESS.getMainColor() + " Ok", TypeMessages.INFO, CategoryMessages.PRIVATE, false);
            } catch (Exception e) {
                Bukkit.shutdown();
                MessagesManager.sendErrorException("Error al cargar: " + section.getName() + ". Deteniendo el servidor...", e);
            }
        }
    }

    public static void register(@NotNull BaseCommand... commands) {
        for (BaseCommand baseCommand : commands) {
            CommandHandler.AVIA_TERRA_COMMANDS.add(baseCommand);
            try {
                // Se crea una instancia manualmente de PluginCommand
                Constructor<PluginCommand> constructor = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
                constructor.setAccessible(true);
                PluginCommand pluginCommand = constructor.newInstance(baseCommand.getName().toLowerCase(), AviaTerraCore.getInstance());

                // Aplicar las propiedades de BaseCommand a PluginCommand
                pluginCommand.setAliases(baseCommand.getAliases());
                pluginCommand.setDescription(baseCommand.getDescription());
                pluginCommand.setUsage(baseCommand.getUsage());
                pluginCommand.setPermission(baseCommand.getPermission());

                // La instancia de PluginCommand la registra dentro de bukkit
                CommandMap commandMap = Bukkit.getCommandMap();
                commandMap.register(AviaTerraCore.getInstance().getName(), pluginCommand);

                // Se añade el gestor de la ejecución tab del comando
                pluginCommand.setExecutor(CommandSection.getCommandHandler());
                pluginCommand.setTabCompleter(CommandSection.getCommandHandler());
            } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
