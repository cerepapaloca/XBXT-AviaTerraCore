package net.atcore.command.commnads;

import net.atcore.AviaTerraCore;
import net.atcore.command.*;
import net.atcore.data.sql.DataBaseRegister;
import net.atcore.messages.Message;
import net.atcore.messages.TypeMessages;
import net.atcore.security.Login.LoginManager;
import net.atcore.security.Login.model.LoginData;
import org.bukkit.command.CommandSender;

import static net.atcore.messages.MessagesManager.sendArgument;
import static net.atcore.messages.MessagesManager.sendFormatMessage;

public class RemovePasswordCommand extends BaseCommand {

    public RemovePasswordCommand() {
        super("removePassword",
                new ArgumentUse("removePassword").addArgPlayer(ModeTabPlayers.ADVANCED),
                CommandVisibility.PRIVATE,
                null
        );
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 1) {
            CommandUtils.executeForPlayer(sender, args[0], false, (name, player) ->
                    AviaTerraCore.enqueueTaskAsynchronously(() -> {
                        if (DataBaseRegister.updatePassword(name, null)){
                            sendFormatMessage(sender, Message.COMMAND_REMOVE_PASSWORD_SUCCESSFUL, name);
                        }else {
                            sendFormatMessage(sender, Message.COMMAND_REMOVE_PASSWORD_ERROR, name);
                        }
                        LoginData data = LoginManager.getDataLogin(name);
                        if (data != null) {
                            data.getRegister().setPasswordShaded(null);
                        }else {
                            sendFormatMessage(sender, Message.COMMAND_REMOVE_PASSWORD_NOT_FOUND, name);
                        }
                    }));
        }else{
            sendArgument(sender, this.getAviaTerraUsage(), TypeMessages.ERROR);
        }
    }
}
