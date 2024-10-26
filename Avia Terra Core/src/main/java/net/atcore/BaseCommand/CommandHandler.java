package net.atcore.BaseCommand;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.atcore.Messages.TypeMessages;
import net.atcore.Security.AntiExploit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static net.atcore.Messages.MessagesManager.sendMessage;

@Getter
@RequiredArgsConstructor //esta anotación crea un constructor con las variables que tenga el final
public final class CommandHandler implements TabExecutor {
    private final HashSet<BaseCommand> commands = new HashSet<>();

    /**
     * Este method es de bukkit y se dispara cada vez que un jugador ejecuta un comando
     * @param sender esta es la instancia del que ejecuto el comando que puede ser un Player o la Consola
     * @param cmd con este parámetro puede saber cuál comando está ejecutado
     * @param args esto son los argumentos que tiene los comando
     */

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        for (BaseCommand command : commands) {
            if (!(cmd.getName().equalsIgnoreCase(command.getName()))) continue;
            boolean hasPermission = command.getPermissions()[0].equals("*");
            for (String permission : command.getPermissions()) {
                if (sender.hasPermission(permission)) {
                    hasPermission = true;
                    break;
                }
            }
            if (sender instanceof Player player) {
               if (AntiExploit.checkOpAndCreative(player))return false;// mirar si el jugador tiene creativo o es OP)
            }
            try {
                if (command.getIsHide()) {
                    if (sender.isOp()){
                        command.execute(sender, args);
                    } else {
                        sendMessage(sender, "No tienes Permisos", TypeMessages.ERROR);
                    }
                    break;
                }

                if (hasPermission) {
                    command.execute(sender, args);
                }else{
                    sendMessage(sender, "No tienes Permisos", TypeMessages.ERROR);
                }
            }catch (Exception e) {
                sendMessage(sender, "Ops!! Hubo un error al ejecutar el comando contacta con el desarrollador", TypeMessages.ERROR);
                throw new RuntimeException(e);
            }

            break;
        }
        return true;
    }

    /**
     * Es casí lo mismo que el OnCommand, pero se dispara cada vez que un jugador escribe los argumentos
     * @return Te vuelve una lista de argumentos que puede ejecutar
     */

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String alias, String[] args) {
        for (BaseCommand command : commands) {
            if (!(cmd.getName().equalsIgnoreCase(command.getName()))) continue;
            if (command instanceof BaseTabCommand tabCommand)return tabCommand.onTab(sender, args);
        }
        return null;
    }
}
