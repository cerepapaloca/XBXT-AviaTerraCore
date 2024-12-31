package net.atcore.command.commnads;

import net.atcore.command.BaseCommand;
import net.atcore.Config;
import net.atcore.command.ArgumentUse;
import net.atcore.messages.Message;
import net.atcore.messages.MessagesManager;
import net.atcore.messages.MessagesType;
import net.atcore.security.Login.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;

import static net.atcore.messages.MessagesManager.sendMessage;
import static net.atcore.security.Login.LoginManager.startPlaySessionCracked;

public class RegisterCommand extends BaseCommand {

    public RegisterCommand() {
        super("register",
                new ArgumentUse("/register").addNote("contraseña").addNote("contraseña"),//"/register <contraseña> <contraseña>"
                "**",
                "Te registras"
        );
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof Player player){
            DataLogin dataLogin = LoginManager.getDataLogin(player);
            if (dataLogin.getRegister().getPasswordShaded() == null){
                if (dataLogin.getRegister().getStateLogins() == StateLogins.CRACKED || Config.getServerMode().equals(ServerMode.OFFLINE_MODE)){
                    if (args.length >= 2){
                        if (Objects.equals(args[0], args[1])){
                            sendMessage(player, Message.COMMAND_REGISTER_SUCCESSFUL_CHAT.getMessage(), MessagesType.SUCCESS);
                            LoginManager.newRegisterCracked(player.getName(), player.getAddress().getAddress(),  args[0]);
                            MessagesManager.sendTitle(player, Message.COMMAND_REGISTER_SUCCESSFUL_TITLE.getMessage(),
                                    String.format(Message.COMMAND_REGISTER_SUCCESSFUL_SUBTITLE.getMessage(), player.getDisplayName())
                                    , 20, 20*3, 40, MessagesType.INFO);
                            startPlaySessionCracked(player).getRegister().setTemporary(false);
                            LoginManager.checkLoginIn(player);
                        }else{
                            sendMessage(player, Message.COMMAND_REGISTER_NO_EQUAL_PASSWORD, MessagesType.ERROR);
                        }
                    }else{
                        sendMessage(player, Message.COMMAND_REGISTER_MISSING_ARGS_PASSWORD, MessagesType.ERROR);
                    }
                }else {
                    sendMessage(player, Message.COMMAND_REGISTER_IS_PREMIUM, MessagesType.ERROR);
                }
            }else {
                sendMessage(player, Message.COMMAND_REGISTER_ALREADY, MessagesType.ERROR);
            }
        }
    }
}
