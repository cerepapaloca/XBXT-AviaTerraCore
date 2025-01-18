package net.atcore.command.commnads;

import net.atcore.command.ArgumentUse;
import net.atcore.command.BaseCommand;
import net.atcore.command.CommandUtils;
import net.atcore.command.ModeTabPlayers;
import net.atcore.messages.CategoryMessages;
import net.atcore.messages.Message;
import net.atcore.messages.MessagesType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static net.atcore.messages.MessagesManager.sendMessage;

public class TellCommand extends BaseCommand {

    public TellCommand() {
        super("tell",
                new ArgumentUse("tell")
                        .addArgPlayer(ModeTabPlayers.ADVANCED)
                        .addNote("Mensaje..."),
                "*",
                "Le envías un mensaje privado a un jugador o un grupo de estos"
        );
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        switch (args.length) {
            case 0 -> sendMessage(sender, "Tienes que poner el nombre del jugador", MessagesType.ERROR);
            case 1 -> sendMessage(sender, "Te falta el mensaje", MessagesType.ERROR);
            default -> {
                String message = "";
                for (int i = 1; i < args.length; i++){
                    message = message.concat(args[i] + " ");
                }
                String finalMessage = message;
                sendMessage(sender, String.format(Message.COMMAND_TELL_FEEDBACK.getMessage(sender) ,args[0]), MessagesType.NULL);
                CommandUtils.executeForPlayer(sender, args[0], true, dataTemporalPlayer -> {
                    Player player = dataTemporalPlayer.player();
                    sendMessage(player,String.format(Message.COMMAND_TELL_FORMAT_MESSAGE.getMessage(player), sender.getName(), finalMessage), MessagesType.NULL, CategoryMessages.PRIVATE, false);
                });
            }
        }
    }
}
