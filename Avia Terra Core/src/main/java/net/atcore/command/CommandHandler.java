package net.atcore.command;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.atcore.messages.Message;
import net.atcore.messages.TypeMessages;
import net.atcore.security.AntiExploit;
import net.atcore.security.Login.LoginManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.List;

import static net.atcore.messages.MessagesManager.*;

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
        for (BaseCommand command : commands) {// Pasa por todas las clases para saber que comando es
            if (!(cmd.getName().equalsIgnoreCase(command.getName()))) continue;
            if (sender instanceof Player player) {
               if (AntiExploit.checkOpAndCreative(player))return false;// mirar si el jugador tiene creativo o es OP
            }
            try {
                if (sender instanceof Player player) {
                    if (CommandUtils.hasPermission(command.getPermissions(), player, true)) {
                        command.execute(sender, args);
                        return true;
                    }else{
                        if (LoginManager.checkLoginIn(player)){
                            sendMessage(sender, Message.COMMAND_GENERIC_NO_PERMISSION.getMessage(), TypeMessages.ERROR);
                        }else {
                            sendMessage(player, Message.COMMAND_GENERIC_NO_LOGIN.getMessage(), TypeMessages.ERROR);
                        }
                    }
                }else {
                    command.execute(sender, args);
                    return true;
                }
            }catch (Exception e) {
                sendMessage(sender, Message.COMMAND_GENERIC_EXCEPTION_ERROR.getMessage(), TypeMessages.ERROR);
                sendErrorException("Error al ejecutar el comando", e);
                return false;
            }
        }
        return false;
    }

    /**
     * Es casí lo mismo que el OnCommand, pero se dispara cada vez que un jugador escribe los argumentos
     * @return Te vuelve una lista de argumentos que puede ejecutar
     */

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String alias, String[] args) {
        for (BaseCommand command : commands) {
            if (!(cmd.getName().equalsIgnoreCase(command.getName()))) continue;
            List<String> list;

            if (command instanceof BaseTabCommand tabCommand){
                list = tabCommand.onTab(sender, args);
            }else {
                list = command.getUsage().onTab(args);
            }

            UseArgs argsUse = command.getUsage();
            if (list == null)return null;

            if (list.size() == 1) {
                if (list.getFirst().equalsIgnoreCase(args[args.length - 1])) {
                    if (argsUse.getLength() >= args.length) {
                        try {
                            if (command.getUsage().getArg(args.length).isRequired()) {
                                return List.of(TypeMessages.ERROR.getMainColorWithColorChart() + String.format(
                                        Message.COMMAND_GENERIC_ARGS_ERROR.getMessage(),
                                        CommandUtils.useToUseDisplay(argsUse.getArgRaw(args.length))
                                ));
                            }
                        }catch (IndexOutOfBoundsException e) {
                            return list;
                        }
                    }
                }
            }
            return list;
        }
        return null;
    }
}
