package net.atcore;

import lombok.Getter;
import lombok.Setter;

public class Config {

    /**
     * Comprueba si tiene items ilegales en su inventario cuando
     * está en survival
     */

    @Getter @Setter private static boolean checkAntiIllegalItems = false;

    /**
     * Al momento de ejecutar un comando se revisa si es op
     * si esí se lo quita (esto no aplica en local host)
     */

    @Getter @Setter private static boolean checkAntiOp = false;

    /**
     * Habilitará el protocolo de encriptación de minecraft
     * para los usuarios premium mientras los cracked no tiene
     * que pasar por este protocolo. Sí se deshabilita todos los
     * usuarios aparecerán como cracked en su sesión
     */

    @Getter @Setter private static boolean mixedMode = false;

    /**
     * Si está habilitado al momento de revisar si un jugador
     * está baneado se tiene en cuenta la ip si no, lo omite
     */

    @Getter @Setter private static boolean checkBanByIp = false;

}
