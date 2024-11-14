package net.atcore.command.Commnads;

import net.atcore.AviaTerraCore;
import net.atcore.command.BaseCommand;
import net.atcore.command.CommandUtils;
import net.atcore.command.ModeAutoTab;
import net.atcore.data.DataBaseRegister;
import net.atcore.messages.TypeMessages;
import org.bukkit.command.CommandSender;

import static net.atcore.messages.MessagesManager.sendMessage;

public class RemoveRegisterCommand extends BaseCommand {

    public RemoveRegisterCommand() {
        super("removeRegister",
                "/removeRegister <Jugador>",
                "le borras el registro al jugador",
                ModeAutoTab.ADVANCED
        );
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 1) {
            CommandUtils.excuteForPlayer(sender, args[0], false, dataTemporalPlayer ->
                    AviaTerraCore.getInstance().enqueueTaskAsynchronously(() -> DataBaseRegister.removeRegister(dataTemporalPlayer.name(), sender.getName())));
            sendMessage(sender, "va ser borrado el registro del jugador revisa los logs", TypeMessages.INFO);
        }else{
            sendMessage(sender, this.getUsage(), TypeMessages.ERROR);
        }
    }
}
