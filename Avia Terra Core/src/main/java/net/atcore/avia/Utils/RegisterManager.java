package net.atcore.avia.Utils;
import net.atcore.avia.AviaTerraCore;
import net.atcore.avia.BaseCommand.BaseCommand;
import net.atcore.avia.BaseCommand.CommandSection;
import net.atcore.avia.Messages.TypeMessages;
import net.atcore.avia.Section;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import org.bukkit.event.Listener;

import static net.atcore.avia.AviaTerraCore.plugin;
import static net.atcore.avia.Messages.MessagesManager.*;
import static org.bukkit.Bukkit.getServer;

/**
 * Esta es una clase que se dedica a registrar e inicializar Los listener o comando u otras cosas
 */

public class RegisterManager {


    public static void register(Listener @NotNull ... listeners) {
        for (Listener listener : listeners) {
            getServer().getPluginManager().registerEvents(listener , plugin);
        }
    }

    public static void register(@NotNull Section section) {
        try {
            section.enable();
            sendMessageConsole(section.getName() + colorSuccess + " Ok", TypeMessages.INFO, false);
        } catch (Exception e) {
            sendMessageConsole("Error al cargar: " + section.getName() + ". Plugin deshabilitado", TypeMessages.ERROR);
            throw new RuntimeException(e);
        }
    }

    public static void register(@NotNull BaseCommand command) {
        CommandSection.getCommandHandler().getCommands().add(command);
        PluginCommand pluginCommand = plugin.getCommand(command.getName());
        pluginCommand.setExecutor(CommandSection.getCommandHandler());
        pluginCommand.setTabCompleter(CommandSection.getCommandHandler());
    }

}
