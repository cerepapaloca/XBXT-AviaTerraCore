package net.atcore.command.commnads;

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
            CommandUtils.executeForPlayer(sender, args[0], false, dataTemporalPlayer ->
                    AviaTerraCore.getInstance().enqueueTaskAsynchronously(() -> {
                        if (DataBaseRegister.removeRegister(dataTemporalPlayer.name(), sender.getName())){
                            sendMessage(sender, String.format("Se borro el registro al jugador <|%s|>", dataTemporalPlayer.name()), TypeMessages.SUCCESS);
                        }else {
                            sendMessage(sender, String.format("Hubo un error al borrar al jugador <|%s|>, vuelva a intentarlo", dataTemporalPlayer.name()), TypeMessages.ERROR);
                        }
                        LoginManager.removeDataLogin(dataTemporalPlayer.name());
                    }));
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
