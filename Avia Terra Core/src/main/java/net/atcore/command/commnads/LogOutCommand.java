package net.atcore.command.commnads;

import net.atcore.command.ArgumentUse;
import net.atcore.command.BaseCommand;
import net.atcore.command.CommandVisibility;
import net.atcore.command.Confirmable;
import net.atcore.messages.Message;
import net.atcore.messages.MessagesManager;
import net.atcore.security.login.LoginManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LogOutCommand extends BaseCommand implements Confirmable {
    public LogOutCommand() {
        super("logOut",
                new ArgumentUse("logOut"),
                CommandVisibility.PUBLIC,
                "Cierras sessión"
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

    @Override
    public Message getMessageConfirm() {
        return Message.COMMAND_LOGOUT_CONFIRM;
    }
}
