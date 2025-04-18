package net.atcore.command.commnads;

import net.atcore.AviaTerraCore;
import net.atcore.command.ArgumentUse;
import net.atcore.command.BaseCommand;
import net.atcore.command.CommandVisibility;
import net.atcore.command.Confirmable;
import net.atcore.data.sql.DataBaseRegister;
import net.atcore.messages.Message;
import net.atcore.messages.MessagesManager;
import net.atcore.security.login.LoginManager;
import net.atcore.security.login.StateLogins;
import net.atcore.security.login.model.LoginData;
import net.atcore.security.login.model.RegisterData;
import net.atcore.utils.AviaTerraScheduler;
import net.atcore.utils.GlobalUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CrackedCommand extends BaseCommand implements Confirmable {

    public CrackedCommand() {
        super("cracked",
                new ArgumentUse("cracked"),
                CommandVisibility.PUBLIC,
                "Pasa de modo premium a modo cracked pidiéndote contraseña de nuevo"
        );
    }

    @Override
    public void execute(CommandSender sender, String[] args) throws Exception {
        if (sender instanceof Player player) {
            LoginData data = LoginManager.getDataLogin(player);
            switch (data.getRegister().getStateLogins()){
                case SEMI_CRACKED, CRACKED -> MessagesManager.sendMessage(player, Message.COMMAND_CRACKED_IS_CRACKED);
                case PREMIUM -> AviaTerraScheduler.enqueueTaskAsynchronously(() -> {
                    if (DataBaseRegister.changeState(GlobalUtils.getRealName(player), StateLogins.SEMI_CRACKED)) {
                        LoginData loginData = LoginManager.getDataLogin(player);
                        RegisterData register = loginData.getRegister();
                        register.setStateLogins(StateLogins.SEMI_CRACKED);
                        register.setTemporary(false);
                        loginData.getSession().setState(StateLogins.SEMI_CRACKED);
                        GlobalUtils.synchronizeKickPlayer(player, Message.COMMAND_CRACKED_SUCCESSFUL);
                    }else {
                        MessagesManager.sendMessage(player, Message.COMMAND_CRACKED_ERROR);
                    }
                });
            }
        }else {
            MessagesManager.sendMessage(sender, Message.COMMAND_GENERIC_NO_PLAYER);
        }
    }

    @Override
    public Message getMessageConfirm() {
        return Message.COMMAND_CRACKED_CONFIRM;
    }
}
