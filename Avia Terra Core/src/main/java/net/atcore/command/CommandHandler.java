package net.atcore.command;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.atcore.messages.TypeMessages;
import net.atcore.security.AntiExploit;
import net.atcore.security.Login.LoginManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static net.atcore.messages.MessagesManager.sendMessage;
import static net.atcore.messages.MessagesManager.sendMessageConsole;

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
            if (sender instanceof Player player) {
               if (AntiExploit.checkOpAndCreative(player))return false;// mirar si el jugador tiene creativo o es OP)
            }
            try {
                if (sender instanceof Player player) {
                    if (CommandUtils.hasPermission(command.getPermissions(), player, true)) {
                        command.execute(sender, args);
                        return true;
                    }else{
                        if (LoginManager.checkLoginIn(player)){
                            sendMessage(sender, "No tienes Permisos", TypeMessages.ERROR);
                        }else {
                            sendMessage(player,"Primero inicia sessión usando /login", TypeMessages.ERROR);
                        }
                    }
                }else {
                    command.execute(sender, args);
                    return true;
                }
            }catch (Exception e) {
                sendMessage(sender, "Ops!! Hubo un error al ejecutar el comando contacta con el desarrollador", TypeMessages.ERROR);
                e.printStackTrace();
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
            switch (command.getModeAutoTab()){
                case NONE -> {
                    return List.of();
                }
                case NORMAL -> {
                    return null;
                }
                case ADVANCED -> {
                    return CommandUtils.tabForPlayer(args[0]);
                }
            }
        }
        return null;
    }
}
