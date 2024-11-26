package net.atcore.utils;
import net.atcore.AviaTerraCore;
import net.atcore.command.BaseCommand;
import net.atcore.command.CommandManager;
import net.atcore.command.CommandSection;
import net.atcore.data.DataBaseMySql;
import net.atcore.data.DataSection;
import net.atcore.data.FileYaml;
import net.atcore.messages.TypeMessages;
import net.atcore.Section;
import org.bukkit.command.CommandException;
import org.bukkit.command.PluginCommand;
import org.jetbrains.annotations.NotNull;

import org.bukkit.event.Listener;

import java.util.HashSet;

import static net.atcore.messages.MessagesManager.*;
import static org.bukkit.Bukkit.getServer;

/**
 * Esta es una clase que se dedica a registrar e inicializar Los listener o comando u otras cosas
 */

public class RegisterManager {

    public static HashSet<Section> sections = new HashSet<>();

    public static void register(Listener @NotNull ... listeners) {
        for (Listener listener : listeners) {
            getServer().getPluginManager().registerEvents(listener , AviaTerraCore.getInstance());
        }
    }

    public static void register(@NotNull Section section) {
        try {
            section.enable();
            sections.add(section);
            sendMessageConsole(section.getName() + COLOR_SUCCESS + " Ok", TypeMessages.INFO, false);
        } catch (Exception e) {
            sendMessageConsole("Error al cargar: " + section.getName() + ". Plugin deshabilitado", TypeMessages.ERROR);
            throw new RuntimeException(e);
        }
    }

    public static void register(@NotNull BaseCommand command) {
        CommandSection.getCommandHandler().getCommands().add(command);
        PluginCommand pluginCommand = AviaTerraCore.getInstance().getCommand(command.getName());
        if (pluginCommand == null) {
            throw new CommandException(command.getName() + " El comando no existe. tiene que añadirlo en plugin.yml");
        }
        CommandManager.COMMANDS_AVIA_TERRA.put(command.getName().toLowerCase(), command.getPermissions());
        for (String s : pluginCommand.getAliases()){
            CommandManager.COMMANDS_AVIA_TERRA.put(s.toLowerCase(), command.getPermissions());
        }
        if (!command.getPermissions().equals("*") && !command.getPermissions().equals("**") && !command.getPermissions().contains("!")){
            pluginCommand.setPermission(AviaTerraCore.getInstance().getName().toLowerCase() + ".command." + command.getName());
            //Permission permission = new Permission(AviaTerraCore.getInstance().getName().toLowerCase() + ".command." + command.getName());
            //getServer().getPluginManager().addPermission(permission);
        }

        pluginCommand.setDescription(command.getDescription());
        pluginCommand.setExecutor(CommandSection.getCommandHandler());
        pluginCommand.setTabCompleter(CommandSection.getCommandHandler());
    }

    public static void register(@NotNull DataBaseMySql database) {
        DataSection.getDataBases().add(database);
    }

    public static void register(@NotNull FileYaml yaml) {
        DataSection.getFileYaml().add(yaml);
    }

}
