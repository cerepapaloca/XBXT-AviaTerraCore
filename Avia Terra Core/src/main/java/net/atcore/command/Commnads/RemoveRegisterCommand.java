package net.atcore.command.Commnads;

import net.atcore.AviaTerraCore;
import net.atcore.command.BaseCommand;
import net.atcore.command.CommandUtils;
import net.atcore.data.DataBaseRegister;
import net.atcore.messages.TypeMessages;
import net.atcore.security.Login.LoginManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static net.atcore.messages.MessagesManager.sendMessage;

public class RemoveRegisterCommand extends BaseCommand {

    public RemoveRegisterCommand() {
        super("removeRegister",
                "/removeRegister <Jugador>",
                true,
                "le borras el registro al jugador"
        );
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 1) {
            CommandUtils.excuteForPlayer(sender, args[0], false, dataTemporalPlayer ->
                    AviaTerraCore.getInstance().enqueueTaskDataBase(() -> DataBaseRegister.removeRegister(dataTemporalPlayer.name(), sender.getName())));
            sendMessage(sender, "va ser borrado el registro del jugador revisa los logs", TypeMessages.INFO);
        }else{
            sendMessage(sender, this.getUsage(), TypeMessages.ERROR);
        }
    }
}
