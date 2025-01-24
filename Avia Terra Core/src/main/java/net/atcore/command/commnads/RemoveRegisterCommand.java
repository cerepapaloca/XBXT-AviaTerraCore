package net.atcore.command.commnads;

import net.atcore.AviaTerraCore;
import net.atcore.command.BaseTabCommand;
import net.atcore.command.CommandUtils;
import net.atcore.command.ModeTabPlayers;
import net.atcore.command.ArgumentUse;
import net.atcore.data.sql.DataBaseRegister;
import net.atcore.messages.Message;
import net.atcore.messages.TypeMessages;
import net.atcore.security.Login.LoginManager;
import org.bukkit.command.CommandSender;

import java.util.HashSet;
import java.util.List;

import static net.atcore.messages.MessagesManager.*;

public class RemoveRegisterCommand extends BaseTabCommand {

    public static final HashSet<String> names = new HashSet<>();

    public RemoveRegisterCommand() {
        super("removeRegister",
                new ArgumentUse("removeRegister").addArgPlayer(ModeTabPlayers.ADVANCED),
                "Le borras el registro al jugador, util para jugadores temporales",
                false);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 1) {
            CommandUtils.executeForPlayer(sender, args[0], false, dataTemporalPlayer ->
                    AviaTerraCore.enqueueTaskAsynchronously(() -> {
                        if (DataBaseRegister.removeRegister(dataTemporalPlayer.name(), sender.getName())){
                            sendFormatMessage(sender, Message.COMMAND_REMOVE_REGISTER_SUCCESSFUL, dataTemporalPlayer.name());
                        }else {
                            sendFormatMessage(sender, Message.COMMAND_REMOVE_REGISTER_ERROR, dataTemporalPlayer.name());
                        }
                        LoginManager.removeDataLogin(dataTemporalPlayer.name());
                    }));
        }else{
            sendArgument(sender, this.getUsage(), TypeMessages.ERROR);
        }
    }

    @Override
    public List<String> onTab(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return CommandUtils.listTab(args[0], names.stream().toList());
        }
        return List.of();
    }
}
