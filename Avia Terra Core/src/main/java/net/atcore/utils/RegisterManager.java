package net.atcore.utils;
import lombok.experimental.UtilityClass;
import net.atcore.AviaTerraCore;
import net.atcore.command.BaseCommand;
import net.atcore.command.CommandManager;
import net.atcore.command.CommandSection;
import net.atcore.command.CommandUtils;
import net.atcore.messages.CategoryMessages;
import net.atcore.messages.MessagesType;
import net.atcore.Section;
import org.bukkit.command.CommandException;
import org.bukkit.command.PluginCommand;
import org.jetbrains.annotations.NotNull;

import org.bukkit.event.Listener;

import java.util.HashSet;

import static net.atcore.messages.MessagesManager.*;
import static org.bukkit.Bukkit.getServer;

/**
 * Esta es una clase que se dedica a registrar e inicializar los listener o comando u otras cosas
 */
@UtilityClass
public class RegisterManager {

    public HashSet<Section> sections = new HashSet<>();

    public void register(Listener @NotNull ... listeners) {
        for (Listener listener : listeners) {
            getServer().getPluginManager().registerEvents(listener , AviaTerraCore.getInstance());
        }
    }

    public void register(@NotNull Section... sections) {
        for (Section section : sections) {
            try {
                section.enable();
                RegisterManager.sections.add(section);
                sendMessageConsole(section.getName() + MessagesType.SUCCESS.getMainColor() + " Ok", MessagesType.INFO, CategoryMessages.PRIVATE, false);
            } catch (Exception e) {
                sendMessageConsole("Error al cargar: " + section.getName() + ". Plugin deshabilitado", MessagesType.ERROR, CategoryMessages.PRIVATE, false);
                throw new RuntimeException(e);
            }
        }
    }

    public static void register(@NotNull BaseCommand... commands) {
        for (BaseCommand command : commands) {
            CommandSection.getCommandHandler().getCommands().add(command);
            PluginCommand pluginCommand = AviaTerraCore.getInstance().getCommand(command.getName());
            if (pluginCommand == null) {
                throw new CommandException(command.getName() + " El comando no existe. tiene que añadirlo en plugin.yml");
            }
            CommandManager.COMMANDS_AVIA_TERRA.put("/" + command.getName().toLowerCase(), command.getPermissions());
            for (String s : pluginCommand.getAliases()) {
                CommandManager.COMMANDS_AVIA_TERRA.put(s.toLowerCase(), command.getPermissions());
            }
            if (!command.getPermissions().equals("*") && !command.getPermissions().equals("**")) {
                pluginCommand.setPermission(AviaTerraCore.getInstance().getName().toLowerCase() + ".command." + command.getName());
            }
            pluginCommand.setDescription(command.getDescription());
            pluginCommand.setUsage(CommandUtils.useToUseDisplay(command.getUsage().toString()));
            pluginCommand.setExecutor(CommandSection.getCommandHandler());
            pluginCommand.setTabCompleter(CommandSection.getCommandHandler());
        }
    }
}
