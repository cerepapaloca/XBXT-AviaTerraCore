package net.atcore.avia;

/**
 * En esta interfaz esta los métodos de inicialización para cada parte del plugin
 */
public interface Section {
    void enable();
    void disable();
    void reloadConfig();
    String getName();
}
