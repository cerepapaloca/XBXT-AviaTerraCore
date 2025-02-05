package net.atcore.command;

import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;


/**
 * El commando que implemente esta interfaz podrá asignar alias especiales donde no solo cambien el nombre del comando,
 * sino que cambia los argumentos a usar. Para añadir un alias se tiene crear una lista en {@link #getCommandsAliases()} luego
 * crear la lógica en {@link #getCommandsAliases()} y {@link #getTabAliase()} y dependiendo el index del alias se usará
 * una condición u otra. Si el alias de {@link #getCommandsAliases()} pertenece al index 2 se usará también el index 2 de
 * {@link #getCommandsAliases()} y {@link #getTabAliase()}.
 */

public interface CommandAliase {

    List<String> getCommandsAliases();

    List<BiConsumer<CommandSender, String[]>> getExecuteAliase();

    List<BiFunction<CommandSender, String[], List<String>>> getTabAliase();

}
