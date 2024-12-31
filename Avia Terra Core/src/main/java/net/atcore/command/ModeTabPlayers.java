package net.atcore.command;

/**
 * Mode como se muestra la lista de jugadores en el TAB
 */

public enum ModeTabPlayers {
    /**
     * No añade ningún argumento al TAB
     */
    NONE,
    /**
     * Solo añade la lista de jugadores conectados. Usando la lista que minecraft vanilla
     */
    NORMAL,
    /**
     * Añade todos los argumentos para seleccionar todos los jugadores
     * o solo seleccionar algúnos
     */
    ADVANCED
}
