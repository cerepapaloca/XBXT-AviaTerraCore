package net.atcore.messages;

import lombok.Getter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static net.atcore.messages.TypeMessages.*;

@Getter
public enum Message {
    TEST_FINISHED("test", "La prueba ha finalizado", INFO),
    TEST_MESSAGE("test", "Hola mundo!! esto es un <|Test|>", INFO),
    LOGIN_KICK_GENERIC("login", "Error de connexion, vuele a intentarlo", KICK),
    LOGIN_KICK_PREMIUM_VALIDATION("login", "Se detecto una discrepancia. Reinicie su cliente", KICK),
    LOGIN_KICK_PASSWORD_ERROR("login", "Hubo un error al guardar tu contraseña, por favor vuelve a entrar", KICK),
    LOGIN_KICK_ADDRESS_ERROR("login", "Hubo un error al guardar tus datos, por favor vuelve a entrar", KICK),
    LOGIN_KICK_REGISTER_ERROR("login", "Hubo un problema con tu registro, vuelve a registrarte", KICK),
    LOGIN_KICK_SESSION_ERROR("login", "Hubo un problema con tu sesión, vuelve a sesión", KICK),
    LOGIN_KICK_KEY_ERROR("login", "Hubo problema con tu llave secreta vuelve a entrar al servidor. Si el problema persiste reinicie su launcher", KICK),
    LOGIN_KICK_ONLINE_MODE("login", "El servidor entro en modo online", KICK),
    LOGIN_KICK_UNKNOWN_STATE("login", "Vuelve a entrar, Hubo un problema con tu cuenta", KICK),
    LOGIN_KICK_ENTRY_LIMBO_ERROR("login", "Error al entrar al modo limbo mode, Por favor vuelve a entrar", KICK),
    LOGIN_KICK_NO_REGISTER("login", "No estas registrado, vuelve a entrar al servidor", KICK),
    LOGIN_LIMBO_INITIATED_BY_SESSION_CHAT("login", "Para Loguear utiliza el siguiente comando:\n <|<click:suggest_command:/login>/login</click>|>", TypeMessages.INFO),
    LOGIN_LIMBO_INITIATED_BY_SESSION_TITLE("login", "Utiliza Este Comando", INFO),
    LOGIN_LIMBO_INITIATED_BY_SESSION_SUBTITLE("login", "<|/login <Contraseña>|>", INFO),
    LOGIN_LIMBO_INITIATED_BY_REGISTER_CHAT("login", "Para registrarte utiliza el siguiente comando:\n <|<click:suggest_command:/register>/register</click>|>.", TypeMessages.INFO),
    LOGIN_LIMBO_INITIATED_BY_REGISTER_TITLE("login", "Utiliza Este Comando", INFO),
    LOGIN_LIMBO_INITIATED_BY_REGISTER_SUBTITLE("login", "<|/register <Contraseña> <Contraseña>|>", INFO),
    LOGIN_LIMBO_TIME_OUT_SESSION("login", "Tardaste mucho en iniciar sesión", KICK),
    LOGIN_LIMBO_TIME_OUT_REGISTER("login", "Tardaste mucho en registrarte", KICK),
    LOGIN_LIMBO_CHAT_WRITE("login", "Te tienes que loguear para escribir en el chat", ERROR),
    LOGIN_LIMBO_BEDROCK_ERROR("login", "Hubo un error con la conexión del servidor, vuele a intentar a entrar", ERROR),
    LOGIN_TWO_FACTOR_LINK_TITLE("login", "Tienes que ejecutar este comando para iniciar vincular su cuenta", GENERIC),
    LOGIN_TWO_FACTOR_LINK_SUBTITLE("login", "/link %s", GENERIC),
    LOGIN_TWO_FACTOR_CODE_TITLE("login", "Aquí esta tu código de un solo uso. Este código no se tiene que compartir y tiene una validez de 2 minutos", GENERIC),
    LOGIN_TWO_FACTOR_CODE_SUBTITLE("login", "%s", GENERIC),
    LOGIN_TWO_FACTOR_SUBJECT_GMAIL("login", "Código de verificación en dos pasos de xB XT", GENERIC),
    LOGIN_TWO_FACTOR_NO_FOUND_CODE("login", "no tienes un código usa el <|/link|> para tener uno", ERROR),
    LOGIN_TWO_FACTOR_UUID_NO_EQUAL("login", "hubo una discrepancia vuelve a enviar un nuevo código", ERROR),
    LOGIN_TWO_FACTOR_EXPIRE_CODE("login", "El código ya expiro", ERROR),
    LOGIN_TWO_FACTOR_CODE_NO_EQUAL("login", "El código no son iguales", ERROR),
    LOGIN_TWO_FACTOR_ARRIVED_MESSAGE_DISCORD("login", "Revise su discord ya tuvo que haber llegado el mensaje", SUCCESS),
    LOGIN_FORM_NEW_PASSWORD("login.form", "Tu nueva contraseña", GENERIC),
    LOGIN_FORM_PASSWORD_AGAIN("login.form", "Otras vez tu contraseña", GENERIC),
    LOGIN_FORM_YOUR_PASSWORD("login.form", "Tu contraseña", GENERIC),
    LOGIN_FORM_TITLE_LOGIN("login.form", "Inicia de sesión", GENERIC),
    LOGIN_FORM_TITLE_REGISTER("login.form", "Registrate", GENERIC),
    SECURITY_KICK_ANTI_TWO_PLAYER("security", "Hay un jugador con tu nombre", KICK),
    EVENT_QUIT("event", "&8[&4-&8]|!> El jugador <|%s|> se a desconecto", INFO),
    EVENT_JOIN("event", "&8[&a+&8]|!> El jugador <click:suggest_command:/w %1$s><|%1$s|></click> se a unido", INFO),
    EVENT_CHAT_FORMAT("event.chat", "%1$s&r » %2$s", GENERIC),// Dejar el &r
    EVENT_CHAT_HOVER("event.chat", """
                Distancia recorrida: <|%sKM|>
                Tiempo jugado: <|%sH|>
                Nombre Real: <|%s|>
                Idioma: <|%s|>
                Rango: <|%s|>
                """, INFO),
    EVENT_CHAT_ADVANCEMENT_CHALLENGE("event.chat", "%s ha completado el desafío %s", INFO),
    EVENT_CHAT_ADVANCEMENT_GOAL("event.chat", "%s ha completado el meta %s", INFO),
    EVENT_CHAT_ADVANCEMENT_TASK("event.chat", "%s ha completado el objetivo %s", INFO),
    BAN_ERROR("ban", "Hubo un problema con las bases de datos al banear <|%s|> por <|%s|>", ERROR),
    BAN_AUTO_BAN_BOT("ban", "Uso de bots (Baneo Automático)", KICK),
    BAN_AUTO_BAN_SPAM("ban", "Por enviar mensajes automatizado en el chat (Baneo Automático)", KICK),
    BAN_AUTO_BAN_DUPE("ban", "Uso de Dupes (Baneo Automático)", KICK),
    BAN_AUTO_BAN_ILEGAL_ITEMS("ban", "Obtención de items ilegal (Baneo Automático)", KICK),
    BAN_AUTHOR_AUTO_BAN("ban","Servidor", GENERIC),
    INVENTORY_MANIPULATOR_TITLE("inventory.manipulator", "Inventario de moderación", GENERIC),
    DATA_MYSQL_EXCEPTION("data.mysql", "Hubo un error al modificar los datos", ERROR),
    COMMAND_GENERIC_NO_PERMISSION("command", "No tienes autorización para ejecutar ese comando", ERROR),
    COMMAND_GENERIC_NOT_FOUND("command", "El comandos no existe haz click <green><click:open_url:https://xbxt.xyz/commands>[Aquí]</click></green> para ver los comandos", ERROR),
    COMMAND_GENERIC_NO_LOGIN("command", "Primero inicia sessión usando <|<click:suggest_command:/login>/login</click>|> si estas registrado si no usa <|<click:suggest_command:/register>/register</click>|>", ERROR),
    COMMAND_GENERIC_NO_PERMISSION_CONSOLE("command", "No tienes autorización para ejecutar comandos en la consola", ERROR),
    COMMAND_GENERIC_RUN_LOG("command", "<|%s|> -> %s", INFO),
    COMMAND_GENERIC_EXCEPTION_ERROR("command", "Ops!! Hubo un error al ejecutar el comando contacta con el desarrollador", ERROR),
    COMMAND_GENERIC_ARGS_ERROR("command", "Te falta el argumento: %s", ERROR),
    COMMAND_GENERIC_FORMAT_DATE_ERROR("command", "EL formato de fecha es incorrecto", ERROR),
    COMMAND_GENERIC_NO_PLAYER("command", "Este comando solo lo puede ejecutar un jugador", ERROR),
    COMMAND_GENERIC_PLAYER_NOT_FOUND("command", "El jugador no existe o esta desconectado", ERROR),
    COMMAND_GENERIC_PLAYERS_NOT_FOUND("command", "El jugador/es <|%s|> no esta conectado o no existe", WARNING),
    COMMAND_NAME_COLOR_NOT_FOUND("command.name-color", "Estos colores no existe", ERROR),
    COMMAND_DISCORD_MESSAGE("command.discord", "Únete a nuestra comunidad de discord: %s", INFO),
    COMMAND_VOTE_MESSAGE("command.vote", "Seria un agradecimiento si votaras aquí: %s", INFO),
    COMMAND_HELP_MESSAGE("command.help", "Haz click en <green><click:open_url:https://xbxt.xyz/commands>[https://xbxt.xyz/commands]</click></green> para ver lista de comandos que tiene el servidor", INFO),
    COMMAND_ADD_RANGE_NOT_FOUND_RANGE("command.add-range", "El rango no existe", ERROR),
    COMMAND_ADD_RANGE_SUCCESSFUL("command.add-range", "El item se le dio exitosamente", SUCCESS),
    COMMAND_ADD_RANGE_NAME("command.add-range", "Rango %1$s por %2$s", INFO),
    COMMAND_ADD_RANGE_MISSING_ARGUMENT_TIME("command.add-range", "Te falta especificar el tiempo de duración", ERROR),
    COMMAND_BAN_MISSING_ARGUMENT_TIME("command.ban", "Tienes que por un tiempo de baneo", ERROR),
    COMMAND_BAN_MISSING_ARGUMENT_REASON("command.ban", "Tienes dar una razón de baneo", ERROR),
    COMMAND_BAN_NOT_FOUND_CONTEXT("command.ban", "El contexto no existe", ERROR),
    COMMAND_BAN_DATA_BASE_ERROR("command.ban", "Hubo un problema con las base de datos vuelva a ejecutar el comando",ERROR),
    COMMAND_BAN_SUCCESSFUL("command.ban", "El jugador sera baneado mira los logs para confirmar", INFO),
    COMMAND_CHANGE_PASSWORD_NOT_EQUAL_PASSWORD("command.change-password", "Contraseña incorrecta. Si no se acuerda de su contraseña y" +
            " tiene un corro o un discord vinculado puede enviar un código de verificación usando <|/link <discord | gmail>|>", ERROR),
    COMMAND_CHANGE_PASSWORD_SUCCESSFUL("command.change-password", "La contraseña se cambio correctamente", SUCCESS),
    COMMAND_CHANGE_PASSWORD_ERROR("command.change-password", "Hubo un error al cambiar la contraseña", ERROR),
    COMMAND_CHANGE_PASSWORD_SUCCESSFUL_LOG("command.change-password", "El jugador <|%1$s|> se cambio la contraseña con su <|%2$s>", INFO),
    COMMAND_CHECK_BAN_MISSING_ARGUMENT_CONTEXT("command.check-ban", "Falta el contexto", ERROR),
    COMMAND_CHECK_BAN_NOT_FOUND_BAN("command.check-ban", "No esta baneado", INFO),
    COMMAND_CHECK_BAN_NOT_FOUND_CONTEXT("command.check-ban", "El contexto no existe", ERROR),
    COMMAND_CHECK_BAN_NOT_FOUND_BAN_IN_CONTEXT("command.check-ban", "El jugador no esta banedo de ningún contexto", INFO),
    COMMAND_CHECK_BAN_NOT_FOUND_BUT("command.check-ban", "El jugador esta baneado pero no del contexto seleccionado pero esta baneado de:", INFO),
    COMMAND_CHECK_BAN_FOUND_AND_KICK("command.check-ban", "El jugador <|%1$s|> fue echado del contexto <|%2$s|>", SUCCESS),
    COMMAND_CHECK_BAN_FOUND("command.check-ban", "<white>-|!> Esta baneado de <|%1$s|>, expira en <|%2$s|> y la razón es <|%3$s|>", INFO),
    COMMAND_CHECK_BAN_ERROR("command.check-ban", "Hubo un problema al encontrar la información del jugador <|%1$s|>", ERROR),
    COMMAND_FREEZE_MISSING_ARGS_LAST("command.freeze", "Tienes que poner true o false", ERROR),
    COMMAND_FREEZE_ALREADY_FREEZE("command.freeze", "El jugador ya fue congelado", ERROR),
    COMMAND_FREEZE_ALREADY_UNFREEZE("command.freeze", "El jugador ya fue descongelado", ERROR),
    COMMAND_FREEZE_UNFREEZE_AUTHOR("command.freeze", "El jugador se descongelo", INFO),
    COMMAND_FREEZE_UNFREEZE_TARGET("command.freeze", "Te descongelaron", INFO),
    COMMAND_FREEZE_FREEZE_AUTHOR("command.freeze","El jugador se congelo", INFO),
    COMMAND_FREEZE_FREEZE_TARGET("command.freeze","Te an congelado, por favor habla con el staff", INFO),
    COMMAND_HOME_LIST_SUCCESSFUL("command.home-list", "<white>%s.</white> <|%s|> localizado en <|%s|> a <|%sm|>", INFO),
    COMMAND_HOME_LIST_HOVER("command.home-list", "<white>click para ir a %s</white>", GENERIC),
    COMMAND_PREMIUM_CONFIRM("command.premium", "<red><b>Advertencia</b></red> Solo ejecutar cuando tiene una cuenta oficial de microsoft," +
            " En caso que sea asi ejecuta este commando <|<Click:suggest_command:/confirm>/confirm</click>|> o " +
            "<|<Click:suggest_command:/premium>/premium</click>|> para pasar al modo premium", INFO),
    COMMAND_PREMIUM_IS_PREMIUM("command.premium", "Esta cuenta ya es premium", ERROR),
    COMMAND_PREMIUM_IS_CRACKED("command.premium", "El nombre de usuario no pertenece a mojang", ERROR),
    COMMAND_PREMIUM_ERROR("command.premium", "Hubo un error al cambiar su estado de cuenta, vuelve a intentarlo", ERROR),

    COMMAND_PREMIUM_SUCCESSFUL("command.premium","Ya se hizo el cambio de tu cuenta. Ya es premium, vuelve a entrar", SUCCESS),
    COMMAND_CRACKED_ERROR("command.cracked", "Hubo un error al cambiar de modo cracked, vuelve a intentarlo", ERROR),
    COMMAND_CRACKED_IS_CRACKED("command.cracked", "Ya estas en modo cracked", ERROR),
    COMMAND_CRACKED_CONFIRM("command.cracked", "<red><b>Advertencia</b></red> En caso de que tu cuenta tenga una contraseña, tendrás que iniciar con esa contraseña, " +
            "En caso contrario te pedirá que te registre. Si quieres proseguir ejecuta <|este mismo comando|> o " +
            "<|<Click:suggest_command:/premium>/premium</click>|>", INFO),
    COMMAND_CRACKED_SUCCESSFUL("command.cracked", "Ya se hizo el cambio de tu cuenta. Ya es cracked, vuelve a entrar", SUCCESS),
    COMMAND_CONFIRM_NOT_FOUND("command.confirm", "No tienes comandos por confirmar", ERROR),
    COMMAND_PASSIVE_RESTART_CANCEL("command.passive", "El reseteo fue cancelado", INFO),
    COMMAND_PASSIVE_RESTART_INIT("command.passive", "Se inicio el reseteo pasivo usa <|<click:suggest_command:/passiverestart cancel>/passiverestart cancel</click>|> para cancelar", INFO),
    COMMAND_PASSIVE_RESTART_START("command.passive", "Se inicio el reinició", INFO),
    COMMAND_LINK_SEND_GMAIL_1("command.link", "Se envió un correo a %s con el código", INFO),
    COMMAND_LINK_SEND_DISCORD_1("command.link", "Se esta enviando un mensaje directo con el código", INFO),
    COMMAND_LINK_ALREADY_GMAIL("command.link", "Ya tienes un correo vinculado solo usar el comando para vincular un nuevo correo", WARNING),
    COMMAND_LINK_ALREADY_DISCORD("command.link", "Ya tiene una cuenta de discord vinculado solo usar el comando para vincular un nueva cuenta de discord", WARNING),
    COMMAND_LINK_ARRIVED_MESSAGE_GMAIL("command.link", "Revisa su bandeja de recibidos, ya tuvo que haber llegado", INFO),
    COMMAND_LINK_SUCCESSFUL("command.link", "Autenticación completada", SUCCESS),
    COMMAND_LINK_ERROR("command.link", "Hubo un error con la autenticación, vuelve a intentar", ERROR),
    COMMAND_LINK_MISSING_ARGS_GMAIL("command.link", "Tienes poner tu gmail o el código de validación", ERROR),
    COMMAND_LINK_GMAIL_NO_LOGIN("command.link", "Para vincular tu gmail tiene que estar logueado", ERROR),
    COMMAND_LINK_SEND_GMAIL_2("command.link", "Se esta enviando un correo con el código", INFO),
    COMMAND_LINK_NOT_FOUND_GMAIL("command.link", "No tienes un Gmail vinculado", ERROR),
    COMMAND_LINK_MISSING_ARGS_DISCORD("command.link", "Tienes poner tu id del Discord o el código de validación", ERROR),
    COMMAND_LINK_DISCORD_NO_LOGIN("command.link", "Para vincular tu Discord tiene que estar logueado", ERROR),
    COMMAND_LINK_SEND_DISCORD_2("command.link","Se esta enviando un mensaje directo con el código", INFO),
    COMMAND_LINK_NOT_FOUNT_DISCORD("command.link", "No tienes un Discord vinculado", INFO),
    COMMAND_LINK_MISSING_ARGS("command.link", "Tienes que poner discord, gmail o un condigo", ERROR),
    COMMAND_ROCKED_TITLE("command.rocked", "Lanzamiento en...", GENERIC),
    COMMAND_LOGOUT_CONFIRM("command.logout", "Seguro que desea cerrar sesión, Si es asi ejecuta <|<Click:suggest_command:/confirm>/confirm</click>|> o vuelve a ejecutar este commando", INFO),
    COMMAND_LOGIN_ALREADY("command.login", "Ya estas logueado", ERROR),
    COMMAND_LOGIN_NO_REGISTER("command.login", "No estas registrado usa el <|/register|>", ERROR),
    COMMAND_LOGIN_MISSING_ARGS("command.login", "Tienes que poner tu contraseña", ERROR),
    COMMAND_LOGIN_SUCCESSFUL_CHAT("command.login", "Has iniciado sesión exitosamente", SUCCESS),
    COMMAND_LOGIN_SUCCESSFUL_TITLE("command.login", "Bienvenido de vuelta", INFO),
    COMMAND_LOGIN_SUCCESSFUL_SUBTITLE("command.login", "<|&o%s|>", INFO),
    COMMAND_LOGIN_NO_EQUAL_PASSWORD("command.login", "contraseña incorrecta o código incorrecto, vuele a intentarlo." +
            "Si no se acuerda de su contraseña y tiene un corro o un discord vinculado puede" +
            "puede enviar un código de verificación usando <|/link <discord | gmail>|>", KICK),
    COMMAND_LOGIN_BANNED("command.login", "Por su seguridad esta cuenta esta suspendido temporalmente por mucho intentos fallidos", WARNING),
    COMMAND_REGISTER_SUCCESSFUL_CHAT("command.register", "Te registraste exitosamente", SUCCESS),
    COMMAND_REGISTER_SUCCESSFUL_TITLE("command.register", "Bienvenido A %s", INFO),
    COMMAND_REGISTER_SUCCESSFUL_SUBTITLE("command.register", "<|&o%s|>", INFO),
    COMMAND_REGISTER_NO_EQUAL_PASSWORD("command.register", "Las contraseñas no son iguales", ERROR),
    COMMAND_REGISTER_MISSING_ARGS_PASSWORD("command.register", "Tienes que escribir la contraseña dos veces asi /register <contraseña> <contraseña>", ERROR),
    COMMAND_REGISTER_PASSWORD_EQUAL_NAME("command.register", "Tu contraseña no puede ser igual a nombre de usuario", ERROR),
    COMMAND_REGISTER_PASSWORD_TOO_SHORT("command.register", "La contraseña es muy corta tiene que ser mayor a %s letras", ERROR),
    COMMAND_REGISTER_IS_PREMIUM("command.register", "Los premium no se registran", ERROR),
    COMMAND_REGISTER_ALREADY("command.register", "Ya estas registrado", ERROR),
    COMMAND_AVIA_TERRA_REMOVE_ERROR("command.avia-terra-remove", "Hubo un error al borrar los datos del jugador <|%s|>, Revise la consola", ERROR),
    COMMAND_AVIA_TERRA_REMOVE_SUCCESSFUL("command.avia-terra-remove","Se le borro exitosamente los datos", SUCCESS),
    COMMAND_SEE_INVENTORY_ERROR("command.see-inventory", "No puedes ver el inventarió de otro jugador que también este mirando un inventarió", ERROR),
    COMMAND_SEE_INVENTORY_MISSING_ARGS("command.see-inventory", "tiene que tener el nombre del jugador", ERROR),
    COMMAND_UNBAN_NOT_FOUND_CONTEXT("command.unban", "contexto no valido", ERROR),
    COMMAND_UNBAN_SUCCESSFUL("command.unban", "El jugador va ser desbaneado mira la los logs para confirmar", INFO),
    COMMAND_WEAPON_MISSING_ARGS_NAME("command.weapon", "Falta el nombre del armamento", ERROR),
    COMMAND_WEAPON_MISSING_ARGS_TYPE("command.weapon", "El tipo de armamento no existe", ERROR),
    COMMAND_WEAPON_SUCCESSFUL("command.weapon", "Se dio el armamento de manera exitosa", SUCCESS),
    COMMAND_WEAPON_NOT_FOUND_TYPE("command.weapon", "El tipo de armamento no existe", ERROR),
    COMMAND_TPA_SEND("command.tpa", "Solicitud enviada a %s", INFO),
    COMMAND_TPA_WAS_DISCONNECTED("command.tpa", "El jugador que te invito se desconecto", ERROR),
    COMMAND_TPA_NO_FOUND("command.tpa", "No tienes solicitudes de tpa", ERROR),
    COMMAND_TPA_RECEIVE("command.tpa", "Tienes una solicitud de <|%s|> Tpa, Click para <hover:show_text:Click para aceptar><click:run_command:/tpa y><green>[Aceptar]</green></click></hover>" +
            " o <hover:show_text:Click para denegar><click:run_command:/tpa n><green><red>[Denegar]</red></click></hover>", INFO),
    COMMAND_TPA_SELF("command.tpa", "No puedes enviar una solicitud a ti mismo", ERROR),
    COMMAND_TPA_EXPIRE("command.tpa","Ya expiro la solicitud de tpa", ERROR),
    COMMAND_TPA_CANCEL_RECEIVE("command.tpa", "Te cancelaron la solicitud de tpa", INFO),
    COMMAND_TPA_CANCEL_SELF("command.tpa", "Haz cancelado la solicitud de tpa", INFO),
    COMMAND_HOME_ADD_SUCCESSFUL("command.home", "El home fue creado", SUCCESS),
    COMMAND_HOME_REMOVE_SUCCESSFUL("command.home", "El home fue borrado", SUCCESS),
    COMMAND_HOME_NOT_FOUND_REMOVE("command.home", "El home no existe", ERROR),
    COMMAND_HOME_NOT_FOUND("command.home", "El home no existe puedes crear uno usando <|<click:run_command:/home %1$s add>/home %1$s add</click>|>", INFO),
    COMMAND_HOME_CONTAINS_POINT("command.home", "El home no puede tener puntos", ERROR),
    COMMAND_HOME_MAX_HOME("command.home", "Solo se permite máximo %s homes",ERROR),
    COMMAND_HOME_CLOSE_SPAWN("command.home", "Esta muy cercar de spawn te falta <|%s|> metros para hacer <|<click:run_command:/home>/home</click>|>", ERROR),
    COMMAND_SAY_MISSING_ARGS("command.say", "Te falta el mensaje", ERROR),
    COMMAND_TELL_MISSING_TARGET("command.tell", "Tienes que poner el nombre del jugador", ERROR),
    COMMAND_TELL_MISSING_MESSAGE("command.tell", "Te falta el mensaje", ERROR),
    COMMAND_TELL_FEEDBACK("command.tell", "&ole haz susurrado a %s -> %s", GENERIC),
    COMMAND_TELL_FORMAT_MESSAGE("command.tell", "&o %s -> %s", GENERIC),
    COMMAND_TELL_NO_LOGIN("command.tell", "No se puede enviar el mensaje a <|%s|>, por que no esta logueado", WARNING),
    COMMAND_BLOCK_SUCCESSFUL("command.block", "Se bloqueo a los jugador/es %s correctamente", SUCCESS),
    COMMAND_BLOCK_MISSING_ARG("command.block", "Tienes que poner al menos a un jugador", ERROR),
    COMMAND_UNBLOCK_SUCCESSFUL("command.unblock", "Se desbloqueo al jugado %s correctamente", SUCCESS),
    COMMAND_UNBLOCK_MISSING_ARG("command.unblock", "Tienes que poner al menos a un jugador", ERROR),
    COMMAND_UNBLOCK_NOT_FOUND("command.unblock", "No se encontrar a %s en tu lista de bloqueo", WARNING),
    DEATH_CAUSE_KILL("death-cause", "<|%1$s|> se murió por que si", INFO),
    DEATH_CAUSE_WORLD_BORDER("death-cause","<|%1$s|> murió por el world border", INFO),
    DEATH_CAUSE_CONTACT("death-cause", "<|%1$s|> murió por estaba tocando algo que no debía", INFO),
    DEATH_CAUSE_END_CRYSTAL("death-cause", "<|%1$s|> fue asesinado con un <lang:entity.minecraft.end_crystal> por <|%2$s|>", INFO),
    DEATH_CAUSE_ENTITY_ATTACK("death-cause", "<|%2$s|> mató a <|%1$s|> con su <|%3$s|>", INFO),
    DEATH_CAUSE_ENTITY_SWEEP_ATTACK("death-cause", "<|%2$s|> destrozo a <|%1$s|> con su con su <|%3$s|>", INFO),
    DEATH_CAUSE_PROJECTILE("death-cause", "<|%1$s|> disparado por <|%2$s|> con su <|%3$s|>", INFO),
    DEATH_CAUSE_SUFFOCATION("death-cause", "<|%1$s|> se asfixio", INFO),
    DEATH_CAUSE_FALL("death-cause", "<|%1$s|> se estampo contra el suelo", INFO),
    DEATH_CAUSE_FIRE("death-cause", "<|%1$s|> se quemo", INFO),
    DEATH_CAUSE_FIRE_TICK("death-cause", "<|%1$s|> fue reducido a cenizas", INFO),
    DEATH_CAUSE_MELTING("death-cause", "un muñeco de nieve mató a <|%1$s|>", INFO),
    DEATH_CAUSE_LAVA("death-cause", "<|%1$s|> nado en lava", INFO),
    DEATH_CAUSE_DROWNING("death-cause", "<|%1$s|> se ahogo", INFO),
    DEATH_CAUSE_BLOCK_EXPLOSION("death-cause", "<|%1$s|> algo le exploto en la cara", INFO),
    DEATH_CAUSE_ENTITY_EXPLOSION("death-cause", "<|%1$s|> alguien le exploto en la cara", INFO),
    DEATH_CAUSE_VOID("death-cause", "<|%1$s|> se metió en el vació", INFO),
    DEATH_CAUSE_LIGHTNING("death-cause", "<|%1$s|> le cayo un rayo", INFO),
    DEATH_CAUSE_SUICIDE("death-cause", "<|%1$s|> acabo con su vida", INFO),
    DEATH_CAUSE_STARVATION("death-cause", "<|%1$s|> se murió de hambre", INFO),
    DEATH_CAUSE_POISON("death-cause", "<|%1$s|> murió por una poción de veneno", INFO),
    DEATH_CAUSE_MAGIC("death-cause", "<|%1$s|> murió de una marera mágica xd", INFO),
    DEATH_CAUSE_WITHER("death-cause", "<|%1$s|> murió por el efecto de whiter", INFO),
    DEATH_CAUSE_FALLING_BLOCK("death-cause", "<|%1$s|> le cayo un bloque en la cabeza", INFO),
    DEATH_CAUSE_THORNS("death-cause","<|%1$s|> murió por el karma", INFO),
    DEATH_CAUSE_DRAGON_BREATH("death-cause", "<|%1$s|> por el dragon", INFO),
    DEATH_CAUSE_CUSTOM("death-cause", "<|%1$s|> lo mato algo", INFO),
    DEATH_CAUSE_FLY_INTO_WALL("death-cause", "<|%1$s|> se marco un 9/11", INFO),
    DEATH_CAUSE_HOT_FLOOR("death-cause", "<|%1$s|> se quemo las patas", INFO),
    DEATH_CAUSE_CAMPFIRE("death-cause", "<|%1$s|> se sentó sobre una fogata", INFO),
    DEATH_CAUSE_CRAMMING("death-cause", "<|%1$s|> se en potro con otro seres", INFO),
    DEATH_CAUSE_DRYOUT("death-cause", "<|%1$s|> se ahogo en el aire", INFO),
    DEATH_CAUSE_FREEZE("death-cause", "<|%1$s|> no soporto el team frio", INFO),
    DEATH_CAUSE_SONIC_BOOM("death-cause", "<|%1$s|> lo mato el grito del warden", INFO),
    MISC_KICK_UPPER("misc", "<dark_gray>< <b><st>           </st> <#4B2FDE>XT <#ff8C00>XB <dark_gray><st>           </st></b> >\n\n&r", GENERIC),
    MISC_KICK_LOWER("misc", "\n\n<dark_gray>< <st>                  </st> | <st>                  </st> >", GENERIC),
    MISC_WARING_ANTI_DUPE("misc", "⚠ Este Item Esta protegido, si intenta dupear se borrara el item automáticamente ⚠", WARNING),
    MISC_NETHER_ROOF("misc", "Techo del nether deshabilitado", WARNING),
    MISC_ACTIVE("misc", "Activo", GENERIC),
    MISC_DESACTIVE("misc", "Desactivado", GENERIC),
    MISC_TAB_HEADER("misc.tab", "<gradient:#581cce:#3f39ea>&lX Bᴜɪʟᴅᴇʀ</gradient> <gradient:#fb8316:#fbb61f>&lX Tᴏᴏʟꜱ</gradient>\n ", GENERIC),
    MISC_TAB_FOOTER("misc.tab", " \n<gradient:#581cce:#3f39ea>TPS %tps%</gradient> &8| <gradient:#fb8316:#fbb61f>PING %ping%</gradient>\n" +
            "<gradient:#581cce:#3f39ea>Tiempo Activo:</gradient><gradient:#fb8316:#fbb61f> %AviaTerraCore_active-time%</gradient> \n \n" +
            "<gradient:#581cce:#fb8316>ᴅɪꜱᴄᴏʀᴅ.ɢɢ/7ubQQFVMWF</gradient>", GENERIC);

    Message(String parent, String defaultMessage, TypeMessages type) {
        this.parent = parent;
        this.typeMessages = type;
        this.MapMessageLocale.put(MessagesManager.DEFAULT_LOCALE_PRIVATE, new String[]{defaultMessage});
    }

    private final TypeMessages typeMessages;
    private final String parent;
    private final HashMap<LocaleAvailable, String[]> MapMessageLocale = new HashMap<>();

    @NotNull
    @Contract(pure = true)
    public String getMessage(CommandSender sender) {
        LocaleAvailable locale;
        if (sender instanceof Player player) {
            locale = LocaleAvailable.getLocate(player.locale());
        }else {
            locale = MessagesManager.DEFAULT_LOCALE_PRIVATE;
        }
        return getRandom(getMessageLocate(locale));
    }

    @NotNull
    @Contract(pure = true)
    public String getMessage(CommandSender sender, int seed) {
        LocaleAvailable locale;
        if (sender instanceof Player player) {
            locale = LocaleAvailable.getLocate(player.locale());
        }else {
            locale = MessagesManager.DEFAULT_LOCALE_PRIVATE;
        }
        ArrayList<String> list = getMessageLocate(locale);
        return list.get(new Random(seed).nextInt(list.size()));
    }

    @Contract(value = "_ -> new", pure = true)
    private @NotNull ArrayList<String> getMessageLocate(@NotNull LocaleAvailable locale) {
        if (!MapMessageLocale.containsKey(locale)) locale = MessagesManager.DEFAULT_LOCALE_USER;
        String[] strings = this.MapMessageLocale.get(locale);
        if (strings == null) strings = this.MapMessageLocale.get(MessagesManager.DEFAULT_LOCALE_PRIVATE);
        return new ArrayList<>(Arrays.asList(strings));
    }

    private String getRandom(List<String> list){
        return list.get(new Random().nextInt(list.size()));
    }


    public String getMessageLocatePrivate() {
        return getRandom(getMessageLocate(MessagesManager.DEFAULT_LOCALE_PRIVATE));
    }

    public String getMessageLocaleDefault() {
        return getRandom(getMessageLocate(MessagesManager.DEFAULT_LOCALE_USER));
    }
}
