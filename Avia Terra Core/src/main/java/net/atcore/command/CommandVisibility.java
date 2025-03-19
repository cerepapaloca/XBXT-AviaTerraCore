package net.atcore.command;

/**
 * Define la visibilidad del comando al ser ejecutadó
 */

public enum CommandVisibility {
    /**
     * El comando puede ser ejecutado por todos aunque no esté logueado
     */
    ALL,
    /**
     * El comando puede solo puede ser ejecutado por jugadores logueados
     */
    PUBLIC,
    /**
     * El comando solo puede ser ejecutado con los permisos adecuados, pero es visible para todos
     */
    SEMI_PUBLIC,
    /**
     * El comando solo puedes ser ejecutado con los permisos adecuados y no es visible por nadie
     */
    PRIVATE,
    /**
     * El commando solo puede ser ejecutado por la consola
     */
    ONLY_CONSOLE,
}
