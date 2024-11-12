package net.atcore.command.Commnads;

import net.atcore.AviaTerraCore;
import net.atcore.AviaTerraPlayer;
import net.atcore.command.BaseCommand;
import net.atcore.command.CommandUtils;
import net.atcore.command.ModeAutoTab;
import net.atcore.messages.TypeMessages;
import net.atcore.security.Login.LoginManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static net.atcore.messages.MessagesManager.sendMessage;

public class RemoveSessionCommand extends BaseCommand {

    public RemoveSessionCommand() {
        super("removeSession",
                "/removeSession <Jugador>",
                true,
                "le borras la sesión al jugador",
                ModeAutoTab.ADVANCED
        );
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 1) {
            CommandUtils.excuteForPlayer(sender, args[0], true, dataTemporalPlayer ->
                    AviaTerraCore.getInstance().enqueueTaskDataBase(() -> LoginManager.getDataLogin(dataTemporalPlayer.player()).setSession(null)));
            sendMessage(sender, "se le borro la sesión al jugador", TypeMessages.SUCCESS);
        }else{
            sendMessage(sender, this.getUsage(), TypeMessages.ERROR);
        }
    }
}
