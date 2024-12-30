package net.atcore.command;

/**
 * Cambian él la manera como se va a comportar el TAB en de
 * manera pre terminada
 */

public enum ModeTabPlayers {
    /**
     * No añade ningún argumento al TAB
     */
    NONE,
    /**
     * Solo añade la lista de jugadores conectados
     */
    NORMAL,
    /**
     * Añade todos los argumentos para seleccionar todos los jugadores
     * o solo seleccionar algúnos
     */
    ADVANCED
}
