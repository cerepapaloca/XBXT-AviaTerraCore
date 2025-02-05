package net.atcore.command.commnads;

import net.atcore.AviaTerraCore;
import net.atcore.command.ArgumentUse;
import net.atcore.command.BaseCommand;
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

public class PremiumCommand extends BaseCommand {

    public PremiumCommand() {
        super("premium",
                new ArgumentUse("premium"),
                "**",
                "La cuenta de un usuario semi-cracked pasa a ser de premium esto provoca que no use su contraseña" +
                        " y haga el protocolo de encriptación",
                true
        );
        this.messageConfirm = "<red><b>Advertencia</b></red> Solo ejecutar cuando tiene una cuenta oficial de microsoft," +
                " En caso que sea asi ejecuta este commando <|<Click:suggest_command:/confirm>/confirm</click>|> para pasar al modo premium";
    }

    @Override
    public void execute(CommandSender sender, String[] args) throws Exception {
        if (sender instanceof Player player) {
            LoginData data = LoginManager.getDataLogin(player);
            switch (data.getRegister().getStateLogins()){
                case PREMIUM -> MessagesManager.sendMessage(player, Message.COMMAND_PREMIUM_IS_PREMIUM);
                case CRACKED -> MessagesManager.sendMessage(player, Message.COMMAND_PREMIUM_IS_CRACKED);
                case SEMI_CRACKED -> AviaTerraCore.enqueueTaskAsynchronously(() -> {
                    if (DataBaseRegister.changeState(GlobalUtils.getRealName(player), StateLogins.PREMIUM)){
                        RegisterData register = LoginManager.getDataLogin(player).getRegister();
                        register.setStateLogins(StateLogins.PREMIUM);
                        register.setTemporary(false);
                        GlobalUtils.synchronizeKickPlayer(player, Message.COMMAND_PREMIUM_SUCCESSFUL);
                    }else {
                        MessagesManager.sendMessage(player, Message.COMMAND_PREMIUM_ERROR);
                    }

                });
            }
        }else {
            MessagesManager.sendMessage(sender, Message.COMMAND_GENERIC_NO_PLAYER);
        }
    }
}
