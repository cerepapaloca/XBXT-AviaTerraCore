package net.atcore;

/**
 * En esta interfaz esta los métodos de inicialización para cada parte del plugin
 */
public interface Section extends Reloadable {
    void enable();
    void disable();
    String getName();
}
