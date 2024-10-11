package net.atcore.Utils;
import net.atcore.BaseCommand.BaseCommand;
import net.atcore.BaseCommand.CommandSection;
import net.atcore.Data.DataBaseMySql;
import net.atcore.Data.DataSection;
import net.atcore.Messages.TypeMessages;
import net.atcore.Section;
import org.bukkit.command.PluginCommand;
import org.jetbrains.annotations.NotNull;

import org.bukkit.event.Listener;

import java.util.HashSet;

import static net.atcore.AviaTerraCore.PLUGIN;
import static net.atcore.Messages.MessagesManager.*;
import static org.bukkit.Bukkit.getServer;

/**
 * Esta es una clase que se dedica a registrar e inicializar Los listener o comando u otras cosas
 */

public class RegisterManager {

    public static HashSet<Section> sections = new HashSet<>();

    public static void register(Listener @NotNull ... listeners) {
        for (Listener listener : listeners) {
            getServer().getPluginManager().registerEvents(listener , PLUGIN);
        }
    }

    public static void register(@NotNull Section section) {
        try {
            section.enable();
            sections.add(section);
            sendMessageConsole(section.getName() + colorSuccess + " Ok", TypeMessages.INFO, false);
        } catch (Exception e) {
            sendMessageConsole("Error al cargar: " + section.getName() + ". Plugin deshabilitado", TypeMessages.ERROR);
            throw new RuntimeException(e);
        }
    }

    public static void register(@NotNull BaseCommand command) {
        CommandSection.getCommandHandler().getCommands().add(command);
        PluginCommand pluginCommand = PLUGIN.getCommand(command.getName());
        pluginCommand.setExecutor(CommandSection.getCommandHandler());
        pluginCommand.setTabCompleter(CommandSection.getCommandHandler());
    }

    public static void register(@NotNull DataBaseMySql database) {
        DataSection.getDataBases().add(database);
    }

}
