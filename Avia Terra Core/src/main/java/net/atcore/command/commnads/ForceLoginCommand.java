package net.atcore.command.commnads;

import net.atcore.command.ArgumentUse;
import net.atcore.command.BaseCommand;
import net.atcore.command.ModeTabPlayers;
import net.atcore.messages.Message;
import net.atcore.messages.MessagesManager;
import net.atcore.messages.MessagesType;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static net.atcore.security.Login.LoginManager.startPlaySessionCracked;

public class ForceLoginCommand extends BaseCommand {

    public ForceLoginCommand() {
        super("forceLogin",
                new ArgumentUse("forceLogin").addArgPlayer(ModeTabPlayers.NORMAL),
                "!*",
                "Fuerza a iniciar session como un jugador cracked");
    }

    @Override
    public void execute(CommandSender sender, String[] args) throws Exception {
        if (!(sender instanceof Player)){
            if (args.length <= 1){
                Player p = Bukkit.getPlayer(args[0]);
                if (p != null){
                    startPlaySessionCracked(p).getRegister().setTemporary(false);
                }else {
                    MessagesManager.sendMessage(sender, Message.COMMAND_GENERIC_PLAYER_NOT_FOUND, MessagesType.ERROR);
                }
            }
        }
    }
}
