package net.atcore.command.commnads;

import net.atcore.command.ArgumentUse;
import net.atcore.command.BaseCommand;
import net.atcore.command.CommandUtils;
import net.atcore.command.ModeTabPlayers;
import net.atcore.messages.CategoryMessages;
import net.atcore.messages.Message;
import net.atcore.messages.TypeMessages;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static net.atcore.messages.MessagesManager.*;

public class TellCommand extends BaseCommand {

    public TellCommand() {
        super("tell",
                new ArgumentUse("tell")
                        .addArgPlayer(ModeTabPlayers.ADVANCED)
                        .addNote("Mensaje..."),
                "*",
                "Le envías un mensaje privado a un jugador o un grupo de estos",
                false);
        addAlias("msg", "w", "r");
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
                sendFormatMessage(sender,Message.COMMAND_TELL_FEEDBACK ,args[0]);
                CommandUtils.executeForPlayer(sender, args[0], true, (name, player) -> {
                    sendMessage(player,String.format(Message.COMMAND_TELL_FORMAT_MESSAGE.getMessage(player), sender.getName(), finalMessage), TypeMessages.NULL, CategoryMessages.PRIVATE, false);
                });
            }
        }
    }
}
