package net.atcore;

import lombok.Getter;
import lombok.Setter;

public class Config {

    /**
     * Comprueba si tiene items ilegales en su inventario cuando
     * no es Op
     */

    @Getter @Setter private static boolean checkAntiIllegalItems = false;

    /**
     * Al momento de ejecutar un comando se revisa si es op
     * si esí se lo quita (esto no aplica en local host)
     */

    @Getter @Setter private static boolean checkAntiOp = false;

    /**
     * Habilitará el protocolo de encriptación de minecraft
     * para los usuarios premium. Mientras los cracked no tiene
     * que pasar por este protocolo. Sí se deshabilita todos los
     * usuarios aparecerán como cracked en su sesión esto hace
     * que todos tenga que usar el /login para iniciar sesión
     */

    @Getter @Setter private static boolean mixedMode = false;

    /**
     * Si está habilitado al momento de revisar si un jugador
     * está baneado se tiene en cuenta la ip si no, lo omite
     */

    @Getter @Setter private static boolean checkBanByIp = false;

    /**
     * Indica a partir de que fecha los tag de rango son validós
     * si los tag son más antiguo que la fecha marcada se borraran
     * del mundo, es mejor dejar en 0
     */

    @Getter @Setter private static long purgeTagRange = 0;

    @Getter @Setter private static long expirationSession = 1000*20;

}
