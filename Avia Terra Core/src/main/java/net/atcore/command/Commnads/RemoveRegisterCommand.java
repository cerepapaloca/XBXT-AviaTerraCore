package net.atcore.command.Commnads;

import net.atcore.AviaTerraCore;
import net.atcore.command.BaseTabCommand;
import net.atcore.command.CommandUtils;
import net.atcore.data.sql.DataBaseRegister;
import net.atcore.messages.TypeMessages;
import net.atcore.security.Login.LoginManager;
import org.bukkit.command.CommandSender;

import java.util.HashSet;
import java.util.List;

import static net.atcore.messages.MessagesManager.sendMessage;

public class RemoveRegisterCommand extends BaseTabCommand {

    public static final HashSet<String> names = new HashSet<>();

    public RemoveRegisterCommand() {
        super("removeRegister",
                "/removeRegister <Jugador>",
                "le borras el registro al jugador"
        );
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 1) {
            CommandUtils.excuteForPlayer(sender, args[0], false, dataTemporalPlayer ->
                    AviaTerraCore.getInstance().enqueueTaskAsynchronously(() -> {
                        DataBaseRegister.removeRegister(dataTemporalPlayer.name(), sender.getName());
                        LoginManager.removeDataLogin(dataTemporalPlayer.name());
                    }));
            sendMessage(sender, "va ser borrado el registro del jugador revisa los logs", TypeMessages.INFO);
        }else{
            sendMessage(sender, this.getUsage(), TypeMessages.ERROR);
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
