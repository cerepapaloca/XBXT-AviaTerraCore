package net.atcore.command.commnads;

import net.atcore.command.ArgumentUse;
import net.atcore.command.BaseCommand;
import net.atcore.command.CommandUtils;
import net.atcore.command.ModeTabPlayers;
import net.atcore.messages.Message;
import net.atcore.messages.MessagesManager;
import net.atcore.messages.MessagesType;
import org.bukkit.command.CommandSender;

public class SayCommand extends BaseCommand {

    private static final String SUB_PREFIX = "<dark_gray>[<b><gradient:#ff0400:#ff2f00>CONSOLE</gradient></b><dark_gray>]|!> ";

    public SayCommand() {
        super("say",//"info", "error", "waring", "susses"
                new ArgumentUse("say").addArgPlayer(ModeTabPlayers.ADVANCED).addFinalArg("Mensaje"),
                "Envias un mensaje global como si fuese del plugin",
                false
        );
    }

    @Override
    public void execute(CommandSender sender, String[] args) throws Exception {
        if (args.length == 0){
            MessagesManager.sendMessage(sender, this.getUsage().toString(), MessagesType.ERROR);
            return;
        }
        if (args.length == 1) {
            MessagesManager.sendMessage(sender, Message.COMMAND_SAY_MISSING_ARGS, MessagesType.ERROR);
        }else {
            StringBuilder message = new StringBuilder();
            for (int i = 1; i < args.length; i++){
                message.append(args[i]).append(" ");
            }
            CommandUtils.executeForPlayer(sender, args[0], true, dataTemporalPlayer ->
                    MessagesManager.sendMessage(dataTemporalPlayer.player(),SUB_PREFIX + message, MessagesType.INFO));
            MessagesManager.sendMessage(sender,  message.toString(), MessagesType.INFO);
        }
    }
}
