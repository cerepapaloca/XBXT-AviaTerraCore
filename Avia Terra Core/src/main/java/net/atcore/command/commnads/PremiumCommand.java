package net.atcore.command.commnads;

import net.atcore.AviaTerraCore;
import net.atcore.command.ArgumentUse;
import net.atcore.command.BaseCommand;
import net.atcore.data.sql.DataBaseRegister;
import net.atcore.messages.Message;
import net.atcore.messages.MessagesManager;
import net.atcore.messages.MessagesType;
import net.atcore.security.Login.LoginManager;
import net.atcore.security.Login.StateLogins;
import net.atcore.security.Login.model.LoginData;
import net.atcore.utils.GlobalUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.geysermc.floodgate.api.FloodgateApi;

public class PremiumCommand extends BaseCommand {

    public PremiumCommand() {
        super("premium",
                new ArgumentUse("premium"),
                "**",
                "Tu cuenta de usuario semi-cracked pasa a ser de premium",
                true
        );
        this.setMessageConfirm("<red><b>Advertencia</b></red> Solo ejecutar cuando tiene una cuenta oficial de microsoft," +
                " En caso que sea asi ejecuta este commando <|<Click:suggest_command:/confirm>/confirm</click>|> para pasar al modo premium");
    }

    @Override
    public void execute(CommandSender sender, String[] args) throws Exception {
        if (sender instanceof Player player) {
            LoginData data = LoginManager.getDataLogin(player);
            switch (data.getRegister().getStateLogins()){
                case PREMIUM -> MessagesManager.sendMessage(player,"Esta cuenta ya es premium", MessagesType.ERROR);
                case CRACKED -> MessagesManager.sendMessage(player,"El nombre de usuario no pertenece a mojan", MessagesType.ERROR);
                case SEMI_CRACKED -> AviaTerraCore.enqueueTaskAsynchronously(() -> {
                    DataBaseRegister.changeState(GlobalUtils.getRealName(player), StateLogins.PREMIUM);
                    LoginData loginData = LoginManager.getDataLogin(player);
                    loginData.getRegister().setStateLogins(StateLogins.PREMIUM);
                    GlobalUtils.synchronizeKickPlayer(player, "Ya se hizo el cambio de tu cuenta. Ya es premium, vuelve a entrar");
                });
            }
        }else {
            MessagesManager.sendMessage(sender, Message.COMMAND_GENERIC_NO_PLAYER, MessagesType.ERROR);
        }
    }
}
