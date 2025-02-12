package net.atcore.command.commnads;

import net.atcore.command.*;
import net.atcore.messages.Message;
import net.atcore.messages.MessagesManager;
import net.atcore.messages.TypeMessages;
import org.bukkit.command.CommandSender;

public class SayCommand extends BaseCommand {

    private static final String SUB_PREFIX = "<dark_gray>[<b><gradient:#ff0400:#ff2f00>CONSOLE</gradient></b><dark_gray>]|!> ";

    public SayCommand() {
        super("say",//"info", "error", "waring", "susses"
                new ArgumentUse("say").addArgPlayer(ModeTabPlayers.ADVANCED).addFinalArg("Mensaje"),
                CommandVisibility.PRIVATE,
                "Enviás un mensaje a un usuario o a varios"
        );
    }

    @Override
    public void execute(CommandSender sender, String[] args) throws Exception {
        if (args.length == 0){
            MessagesManager.sendArgument(sender, this.getAviaTerraUsage(), TypeMessages.ERROR);
            return;
        }
        if (args.length == 1) {
            MessagesManager.sendMessage(sender, Message.COMMAND_SAY_MISSING_ARGS);
        }else {
            StringBuilder message = new StringBuilder();
            for (int i = 1; i < args.length; i++){
                message.append(args[i]).append(" ");
            }
            CommandUtils.executeForPlayer(sender, args[0], true, (name, player) ->
                    MessagesManager.sendMessage(player,SUB_PREFIX + message, TypeMessages.INFO));
            MessagesManager.sendString(sender,  message.toString(), TypeMessages.INFO);
        }
    }
}
