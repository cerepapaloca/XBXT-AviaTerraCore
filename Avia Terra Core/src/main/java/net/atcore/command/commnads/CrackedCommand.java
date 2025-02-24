package net.atcore.command.commnads;

import net.atcore.AviaTerraCore;
import net.atcore.command.ArgumentUse;
import net.atcore.command.BaseCommand;
import net.atcore.command.CommandVisibility;
import net.atcore.command.Confirmable;
import net.atcore.data.sql.DataBaseRegister;
import net.atcore.messages.Message;
import net.atcore.messages.MessagesManager;
import net.atcore.security.Login.LoginManager;
import net.atcore.security.Login.StateLogins;
import net.atcore.security.Login.model.LoginData;
import net.atcore.security.Login.model.RegisterData;
import net.atcore.utils.GlobalUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CrackedCommand extends BaseCommand implements Confirmable {

    public CrackedCommand() {
        super("cracked",
                new ArgumentUse("cracked"),
                CommandVisibility.PUBLIC,
                "Pasa de modo premium a modo cracked pidiéndote"
        );
    }

    @Override
    public void execute(CommandSender sender, String[] args) throws Exception {
        if (sender instanceof Player player) {
            LoginData data = LoginManager.getDataLogin(player);
            switch (data.getRegister().getStateLogins()){
                case SEMI_CRACKED, CRACKED -> MessagesManager.sendMessage(player, Message.COMMAND_CRACKED_IS_CRACKED);
                case PREMIUM -> AviaTerraCore.enqueueTaskAsynchronously(() -> {
                    if (DataBaseRegister.changeState(GlobalUtils.getRealName(player), StateLogins.SEMI_CRACKED)) {
                        RegisterData register = LoginManager.getDataLogin(player).getRegister();
                        register.setStateLogins(StateLogins.SEMI_CRACKED);
                        register.setTemporary(false);//TODO: Revisar si esto funciona bien
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
    public String getMessageConfirm() {
        return "<red><b>Advertencia</b></red> En caso de que tu cuenta tenga una contraseña, tendrás que iniciar con esa contraseña, " +
                "En caso contrario te pedirá que te registre. Si quieres proseguir ejecuta <|este mismo comando|> o " +
                "<|<Click:suggest_command:/premium>/premium</click>|>";
    }
}
