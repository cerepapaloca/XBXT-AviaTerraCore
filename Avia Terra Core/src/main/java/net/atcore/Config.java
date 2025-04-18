package net.atcore;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.UtilityClass;
import net.atcore.security.login.ServerMode;

@UtilityClass
public class Config {

    /**
     * Habilitará el protocolo de encriptación de minecraft
     * para los usuarios premium. Mientras los cracked no tiene
     * que pasar por este protocolo. Sí se deshabilita todos los
     * usuarios aparecerán como cracked en su sesión esto hace
     * que todos tenga que usar el /login para iniciar sesión
     */

    @Getter @Setter private ServerMode serverMode = ServerMode.MIX_MODE;

    /**
     * Indica a partir de que fecha los tag de rango son validós
     * si los tag son más antiguo que la fecha marcada se borraran
     * del mundo, es mejor dejar en 0
     */

    @Getter @Setter private long purgeTagRange = 0;

    /**
     * El tiempo de expiración de la session de los jugadores a loguearse
     * esto solo tiene sentido con las sesiones no premium por qué los
     * jugadores premium cuando se le agota la sesión sé crea una nueva
     * automáticamente y obviamente el servidor tiene que estar mixMode u
     * OnlineMode si no esto último no aplica
     */

    @Getter @Setter private long expirationSession = 1000;

    /**
     * Índica la cantidad de punto de chat genera un jugador en un tick
     * si lo pones en 0 el jugador no podría regenerar sus puntos de chat y
     * lo pones muy alto los jugadores no tentarían límites a escribir en el
     * chat
     */

    @Getter @Setter private double levelModerationChat = 0;

    /**
     * El porcentaje de un jugador tenga la posibilidad de duplicar un
     * item en el itemFrame
     */

    @Getter @Setter private double chaceDupeFrame = 0.5;

    @Getter @Setter private String passwordSSL = "";

}
