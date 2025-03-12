package net.atcore.command;

import lombok.Getter;
import net.atcore.messages.CategoryMessages;
import net.atcore.messages.Message;
import net.atcore.messages.MessagesManager;
import net.atcore.messages.TypeMessages;
import net.atcore.security.login.LoginManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import static net.atcore.messages.MessagesManager.sendErrorException;
import static net.atcore.messages.MessagesManager.sendMessage;

@Getter
public final class CommandHandler implements TabExecutor {

    private final HashMap<String, CommandAliase> aliases = new HashMap<>();
    public static final HashSet<BaseCommand> AVIA_TERRA_COMMANDS = new HashSet<>();
    public static final HashMap<UUID, String> SAVES_COMMANDS_CONFIRMS = new HashMap<>();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        for (BaseCommand command : AVIA_TERRA_COMMANDS) {// Pasa por todas las clases para saber que comando es
            if (!(cmd.getName().equalsIgnoreCase(command.getName()))) continue;
            if (command instanceof Confirmable confirmable && sender instanceof Player player) {
                if (SAVES_COMMANDS_CONFIRMS.containsKey(player.getUniqueId())) {
                    SAVES_COMMANDS_CONFIRMS.remove(player.getUniqueId());
                } else {
                    SAVES_COMMANDS_CONFIRMS.put(player.getUniqueId(), label + " " + String.join(" ", args));
                    MessagesManager.sendString(sender, confirmable.getMessageConfirm(), TypeMessages.INFO);
                    return true;
                }
            }
            try {
                if (sender instanceof Player player) {
                    if (CommandUtils.hasPermission(command, command.getVisibility(), player, true)) {
                        executedCommand(sender, label, args, command);
                        return true;
                    }else{
                        if (LoginManager.checkLogin(player)){
                            sendMessage(sender, Message.COMMAND_GENERIC_NO_PERMISSION);
                        }else {
                            sendMessage(player, Message.COMMAND_GENERIC_NO_LOGIN);
                        }
                    }
                }else {
                    executedCommand(sender, label, args, command);
                    return true;
                }
            }catch (Exception e) {
                sendMessage(sender, Message.COMMAND_GENERIC_EXCEPTION_ERROR);
                StringBuilder sb = new StringBuilder();
                sb.append(label).append(" ");
                for (String arg : args) sb.append(arg).append(" ");
                MessagesManager.logConsole(String.format("[%s] <|%s|> -> `%s`",e.getMessage(), sender.getName(), sb), TypeMessages.ERROR, CategoryMessages.COMMANDS);
                sendErrorException("Error al ejecutar el comando", e);
                return false;
            }
        }
        return false;
    }

    private static void executedCommand(@NotNull CommandSender sender, @NotNull String label, String[] args, BaseCommand command) throws Exception {
        if (command instanceof CommandAliase commandAlias) {
            for (int i = 0; i < commandAlias.getCommandsAliases().size(); i++) {
                String alias = commandAlias.getCommandsAliases().get(i);
                if (alias.equalsIgnoreCase(label)) {
                    // Si el comando tiene un aliase se va ejecutar el alise
                    commandAlias.getExecuteAliase().get(i).accept(sender, args);
                    return;
                }
            }
        }
        command.execute(sender, args);
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        for (BaseCommand command : AVIA_TERRA_COMMANDS) {
            if (!(cmd.getName().equalsIgnoreCase(command.getName()))) continue;
            List<String> list;

            if (command instanceof BaseTabCommand tabCommand){
                list = tabCommand.onTab(sender, args);
            }else {
                list = command.getAviaTerraUsage().onTab(args);
            }

            if (command instanceof CommandAliase commandAlias) {
                for (int i = 0; i < commandAlias.getCommandsAliases().size(); i++) {
                    String alias = commandAlias.getCommandsAliases().get(i);
                    if (alias.equalsIgnoreCase(label)) {
                        list = commandAlias.getTabAliase().get(i).apply(sender, args);
                        break;
                    }
                }
            }

            ArgumentUse argsUse = command.getAviaTerraUsage();
            if (list == null)return null;

            if (list.size() == 1) {
                if (list.getFirst().equalsIgnoreCase(args[args.length - 1])) {
                    if (argsUse.getLength() >= args.length) {
                        try {
                            if (command.getAviaTerraUsage().getArg(args.length).isRequired()) {
                                return List.of("§c" + String.format(// La única vez que se tiene que usar '§'
                                        Message.COMMAND_GENERIC_ARGS_ERROR.getMessage(sender),
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

    public static BaseCommand getCommand(String name) {
        for (BaseCommand baseCommand : AVIA_TERRA_COMMANDS){
            for  (String aliase : baseCommand.getAliases()){
                if (aliase.equalsIgnoreCase(name)){
                    return baseCommand;
                }
            }
            if (baseCommand.getName().equalsIgnoreCase(name)){
                return baseCommand;
            }
        }
        return null;
    }
}
