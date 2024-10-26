package net.atcore.BaseCommand.Commnads;

import net.atcore.AviaTerraCore;
import net.atcore.BaseCommand.BaseCommand;
import net.atcore.Data.DataBaseRegister;
import net.atcore.Messages.TypeMessages;
import net.atcore.Security.Login.LoginManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static net.atcore.Messages.MessagesManager.sendMessage;

public class CommandRemoveRegister extends BaseCommand {

    public CommandRemoveRegister() {
        super("removeRegister",
                "/removeRegister <Jugador>",
                true,
                "le borras el registro al jugador"
        );
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 1) {
            Player player = Bukkit.getPlayer(args[0]);
            if (player != null) {
                Bukkit.getScheduler().runTaskAsynchronously(AviaTerraCore.getInstance(), () -> DataBaseRegister.removeRegister(args[0], sender.getName()));
                LoginManager.removeDataLogin(player.getUniqueId());
                sendMessage(sender, "va ser borrado el registro del jugador revisa los logs", TypeMessages.INFO);
            }else {
                sendMessage(sender ,"el jugador no existe o no esta conectado", TypeMessages.ERROR);
            }
        }else{
            sendMessage(sender, this.getUsage(), TypeMessages.ERROR);
        }
    }
}
