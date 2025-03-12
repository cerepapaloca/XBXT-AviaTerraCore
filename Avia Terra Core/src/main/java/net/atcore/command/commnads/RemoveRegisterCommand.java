package net.atcore.command.commnads;

import net.atcore.AviaTerraCore;
import net.atcore.command.*;
import net.atcore.data.sql.DataBaseRegister;
import net.atcore.messages.Message;
import net.atcore.messages.TypeMessages;
import net.atcore.security.login.LoginManager;
import org.bukkit.command.CommandSender;

import java.util.List;

import static net.atcore.messages.MessagesManager.*;

public class RemoveRegisterCommand extends BaseTabCommand {

    public RemoveRegisterCommand() {
        super("removeRegister",
                new ArgumentUse("removeRegister").addArgPlayer(ModeTabPlayers.ADVANCED),
                CommandVisibility.PRIVATE,
                "Le borras el registro al jugador, util para jugadores temporales"
        );
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 1) {
            CommandUtils.executeForPlayer(sender, args[0], false, (name, player) ->
                    AviaTerraCore.enqueueTaskAsynchronously(() -> {
                        if (DataBaseRegister.removeRegister(name, sender.getName())){
                            sendFormatMessage(sender, Message.COMMAND_REMOVE_REGISTER_SUCCESSFUL, name);
                        }else {
                            sendFormatMessage(sender, Message.COMMAND_REMOVE_REGISTER_ERROR, name);
                        }
                        LoginManager.removeDataLogin(name);
                    }));
        }else{
            sendArgument(sender, this.getAviaTerraUsage(), TypeMessages.ERROR);
        }
    }

    @Override
    public List<String> onTab(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return CommandUtils.listTab(args[0], LoginManager.getDataLogin().stream().map(dataLogin -> dataLogin.getRegister().getUsername()).toList());
        }
        return List.of();
    }
}
