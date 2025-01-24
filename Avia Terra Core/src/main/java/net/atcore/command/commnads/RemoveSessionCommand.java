package net.atcore.command.commnads;

import net.atcore.command.BaseCommand;
import net.atcore.command.CommandUtils;
import net.atcore.command.ModeTabPlayers;
import net.atcore.command.ArgumentUse;
import net.atcore.messages.Message;
import net.atcore.messages.TypeMessages;
import net.atcore.security.Login.LoginManager;
import org.bukkit.command.CommandSender;

import static net.atcore.messages.MessagesManager.sendArgument;
import static net.atcore.messages.MessagesManager.sendMessage;

public class RemoveSessionCommand extends BaseCommand {

    public RemoveSessionCommand() {
        super("removeSession",
                new ArgumentUse("/removeSession").addArgPlayer(ModeTabPlayers.ADVANCED),
                "Le borras la sesión al jugador",
                false
        );
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 1) {
            CommandUtils.executeForPlayer(sender, args[0], true, dataTemporalPlayer -> {
                LoginManager.getDataLogin(dataTemporalPlayer.player()).setSession(null);
                //GlobalUtils.kickPlayer(dataTemporalPlayer.player(), "Vuelve a iniciar sesión");
            });
            sendMessage(sender, Message.COMMAND_REMOVE_SESSION_SUCCESSFUL);
        }else{
            sendArgument(sender, this.getUsage(), TypeMessages.ERROR);
        }
    }
}
