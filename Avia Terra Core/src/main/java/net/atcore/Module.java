package net.atcore;

/**
 * Está pensado para que otros módulos del proyecto se activen o
 * se desactive atreves del módulo principal o mejor dicho del Core.
 * Esto es para centralizar todó el proyecto y dividir la carga, por si
 * se requiere unas funciones en un servidor, pero de pronto el otro no las
 * necesite y así evitar cargar cosas innecesarias
 */

public interface Module {
    void enable();
    void disable();
}