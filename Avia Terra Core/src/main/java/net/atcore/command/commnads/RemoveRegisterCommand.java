package net.atcore.command.commnads;

import net.atcore.AviaTerraCore;
import net.atcore.command.BaseTabCommand;
import net.atcore.command.CommandUtils;
import net.atcore.command.ModeTabPlayers;
import net.atcore.command.ArgumentUse;
import net.atcore.data.sql.DataBaseRegister;
import net.atcore.messages.Message;
import net.atcore.messages.MessagesType;
import net.atcore.security.Login.LoginManager;
import org.bukkit.command.CommandSender;

import java.util.HashSet;
import java.util.List;

import static net.atcore.messages.MessagesManager.sendMessage;

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
                            sendMessage(sender, String.format(Message.COMMAND_REMOVE_REGISTER_SUCCESSFUL.getMessage(sender), dataTemporalPlayer.name()), MessagesType.SUCCESS);
                        }else {
                            sendMessage(sender, String.format(Message.COMMAND_REMOVE_REGISTER_ERROR.getMessage(sender), dataTemporalPlayer.name()), MessagesType.ERROR);
                        }
                        LoginManager.removeDataLogin(dataTemporalPlayer.name());
                    }));
        }else{
            sendMessage(sender, this.getUsage().toString(), MessagesType.ERROR);
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
