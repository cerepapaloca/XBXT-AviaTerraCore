package net.atcore.command.commnads;

import net.atcore.command.*;
import net.atcore.messages.CategoryMessages;
import net.atcore.messages.Message;
import net.atcore.messages.TypeMessages;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import static net.atcore.messages.MessagesManager.sendFormatMessage;
import static net.atcore.messages.MessagesManager.sendMessage;

public class TellCommand extends BaseCommand implements CommandAliase {

    public static String lastNamePlayer = "";

    public TellCommand() {
        super("tell",
                new ArgumentUse("tell")
                        .addArgPlayer(ModeTabPlayers.ADVANCED)
                        .addNote("Mensaje..."),
                CommandVisibility.PUBLIC,
                "Le envías un mensaje privado a un jugador o un grupo de estos");
        addAlias("msg", "w");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        switch (args.length) {
            case 0 -> sendMessage(sender, Message.COMMAND_TELL_MISSING_TARGET);
            case 1 -> sendMessage(sender, Message.COMMAND_TELL_MISSING_MESSAGE);
            default -> {
                String message = "";
                for (int i = 1; i < args.length; i++){
                    message = message.concat(args[i] + " ");
                }
                String finalMessage = message;
                sendFormatMessage(sender, Message.COMMAND_TELL_FEEDBACK, args[0], finalMessage);
                CommandUtils.executeForPlayer(sender, args[0], true, (name, player) -> {
                    sendMessage(player,String.format(Message.COMMAND_TELL_FORMAT_MESSAGE.getMessage(player), sender.getName(), finalMessage), TypeMessages.NULL, CategoryMessages.PRIVATE, false);
                });
            }
        }
    }

    @Override
    public List<String> getCommandsAliases() {
        return List.of("r");
    }

    @Override
    public List<BiConsumer<CommandSender, String[]>> getExecuteAliase() {
        var list = new ArrayList<BiConsumer<CommandSender, String[]>>();
        list.add((sender, args) -> {
            StringBuilder builder = new StringBuilder();
            builder.append(lastNamePlayer);
            for (int i = 1; i < args.length; i++) {
                builder.append(' ').append(args[i]);
            }
            execute(sender, builder.toString().split(" "));
        });
        return list;
    }

    @Override
    public List<BiFunction<CommandSender, String[], List<String>>> getTabAliase() {
        var list = new ArrayList<BiFunction<CommandSender, String[], List<String>>>();
        list.add(((sender, strings) -> List.of("Mensaje...")));
        return list;
    }
}
