package net.atcore.command.commnads;

import net.atcore.command.ArgumentUse;
import net.atcore.command.BaseCommand;
import net.atcore.messages.Message;
import net.atcore.messages.MessagesManager;
import net.atcore.security.Login.LoginManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LogOutCommand extends BaseCommand {
    public LogOutCommand() {
        super("logOut",
                new ArgumentUse("logOut"),
                "*",
                "Cierras sessión",
                true
        );
        addAlias("signOut");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof Player player){
            LoginManager.getDataLogin(player).setSession(null);
            LoginManager.checkLogin(player,false,true);
        }else {
            MessagesManager.sendMessage(sender, Message.COMMAND_GENERIC_NO_PLAYER);
        }
    }
}
