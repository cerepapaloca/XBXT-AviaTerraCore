package net.atcore.command.commnads;

import net.atcore.command.ArgumentUse;
import net.atcore.command.BaseCommand;
import net.atcore.command.CommandUtils;
import net.atcore.command.ModeTabPlayers;
import net.atcore.messages.Message;
import net.atcore.messages.MessagesManager;
import net.atcore.messages.MessagesType;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SayCommand extends BaseCommand {

    public SayCommand() {
        super("say",//"info", "error", "waring", "susses"
                new ArgumentUse("say").addArgPlayer(ModeTabPlayers.ADVANCED).addArg(MessagesType.values()).addFinalArg("Mensaje"),
                "Envias un mensaje global como si fuese del plugin"
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
            MessagesType type;
            try {
                type = MessagesType.valueOf(args[1].toUpperCase());
            }catch (IllegalArgumentException e) {
                MessagesManager.sendMessage(sender, Message.COMMAND_SAY_TYPE_MESSAGE_ERROR, MessagesType.ERROR);
                return;
            }
            StringBuilder message = new StringBuilder();
            for (int i = 2; i < args.length; i++){
                message.append(args[i]).append(" ");
            }
            for (Player p : Bukkit.getOnlinePlayers()) {
                MessagesManager.sendMessage(p, message.toString(), type);
            }
            CommandUtils.executeForPlayer(sender, args[0], true, dataTemporalPlayer -> {
                MessagesManager.sendMessage(dataTemporalPlayer.player(), message.toString(), type);
            });
        }
    }
}
