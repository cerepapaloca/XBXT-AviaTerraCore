package net.atcore.command.Commnads;

import net.atcore.AviaTerraCore;
import net.atcore.command.BaseCommand;
import net.atcore.command.CommandUtils;
import net.atcore.command.ModeAutoTab;
import net.atcore.messages.TypeMessages;
import net.atcore.security.Login.LoginManager;
import net.atcore.utils.GlobalUtils;
import org.bukkit.command.CommandSender;

import static net.atcore.messages.MessagesManager.sendMessage;

public class RemoveSessionCommand extends BaseCommand {

    public RemoveSessionCommand() {
        super("removeSession",
                "/removeSession <Jugador>",
                "le borras la sesión al jugador",
                ModeAutoTab.ADVANCED
        );
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 1) {
            CommandUtils.excuteForPlayer(sender, args[0], true, dataTemporalPlayer -> {
                LoginManager.getDataLogin(dataTemporalPlayer.player()).setSession(null);
                //GlobalUtils.kickPlayer(dataTemporalPlayer.player(), "Vuelve a iniciar sesión");
            });
            sendMessage(sender, "se le borro la sesión al jugador", TypeMessages.SUCCESS);
        }else{
            sendMessage(sender, this.getUsage(), TypeMessages.ERROR);
        }
    }
}
