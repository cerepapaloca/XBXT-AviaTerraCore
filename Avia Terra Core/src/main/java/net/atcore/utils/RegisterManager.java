package net.atcore.utils;
import lombok.experimental.UtilityClass;
import net.atcore.AviaTerraCore;
import net.atcore.armament.ArmamentUtils;
import net.atcore.armament.BaseArmament;
import net.atcore.command.BaseCommand;
import net.atcore.command.CommandManager;
import net.atcore.command.CommandSection;
import net.atcore.command.CommandUtils;
import net.atcore.data.DataBaseMySql;
import net.atcore.data.DataSection;
import net.atcore.data.File;
import net.atcore.messages.CategoryMessages;
import net.atcore.messages.TypeMessages;
import net.atcore.Section;
import org.bukkit.command.CommandException;
import org.bukkit.command.PluginCommand;
import org.checkerframework.checker.units.qual.A;
import org.jetbrains.annotations.NotNull;

import org.bukkit.event.Listener;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

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

    public void register(@NotNull Section section) {
        try {
            section.enable();
            sections.add(section);
            sendMessageConsole(section.getName() + TypeMessages.SUCCESS.getMainColor() + " Ok", TypeMessages.INFO, CategoryMessages.PRIVATE, false);
        } catch (Exception e) {
            sendMessageConsole("Error al cargar: " + section.getName() + ". Plugin deshabilitado", TypeMessages.ERROR, CategoryMessages.PRIVATE, false);
            throw new RuntimeException(e);
        }
    }

    public static void register(@NotNull BaseCommand... commands) {
        for (BaseCommand command : commands) {
            CommandSection.getCommandHandler().getCommands().add(command);
            PluginCommand pluginCommand = AviaTerraCore.getInstance().getCommand(command.getName());
            if (pluginCommand == null) {
                throw new CommandException(command.getName() + " El comando no existe. tiene que añadirlo en plugin.yml");
            }
            CommandManager.COMMANDS_AVIA_TERRA.put(command.getName().toLowerCase(), command.getPermissions());
            for (String s : pluginCommand.getAliases()){
                CommandManager.COMMANDS_AVIA_TERRA.put(s.toLowerCase(), command.getPermissions());
            }
            if (!command.getPermissions().equals("*") && !command.getPermissions().equals("**")) {
                pluginCommand.setPermission(AviaTerraCore.getInstance().getName().toLowerCase() + ".command." + command.getName());
                //Permission permission = new Permission(AviaTerraCore.getInstance().getName().toLowerCase() + ".command." + command.getName());
                //getServer().getPluginManager().addPermission(permission);
            }
            pluginCommand.setDescription(command.getDescription());
            pluginCommand.setUsage(CommandUtils.useToUseDisplay(command.getUsage().toString()));
            pluginCommand.setExecutor(CommandSection.getCommandHandler());
            pluginCommand.setTabCompleter(CommandSection.getCommandHandler());
        }
    }
    /*
    public void register(@NotNull DataBaseMySql... database) {
        DataSection.DATA_BASE.addAll(Arrays.asList(database));
    }

    public void register(@NotNull File... yaml) {
        DataSection.FILES.addAll(Arrays.asList(yaml));
    }

    /*public void register(@NotNull BaseArmament... armament) {
        ArmamentUtils.ARMAMENTS.addAll(Arrays.asList(armament));
    }*/

}
