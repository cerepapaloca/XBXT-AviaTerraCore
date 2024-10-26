package net.atcore.BaseCommand.Commnads;

import net.atcore.BaseCommand.BaseCommand;
import net.atcore.Messages.TypeMessages;
import net.atcore.Security.Login.LoginManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static net.atcore.Messages.MessagesManager.sendMessage;

public class CommandRemoveSession extends BaseCommand {

    public CommandRemoveSession() {
        super("removeSession",
                "/removeSession <Jugador>",
                true,
                "le borras la sesión al jugador"
        );
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 1) {
            Player player = Bukkit.getPlayer(args[0]);
            if (player != null) {
                LoginManager.getDataLogin(player).setSession(null);
                sendMessage(sender, "va ser borrado el registro del jugador revisa los logs", TypeMessages.INFO);
            }else {
                sendMessage(sender ,"el jugador no existe o no esta conectado", TypeMessages.ERROR);
            }
        }else{
            sendMessage(sender, this.getUsage(), TypeMessages.ERROR);
        }
    }
}
