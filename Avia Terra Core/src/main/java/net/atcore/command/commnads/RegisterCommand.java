package net.atcore.command.commnads;

import net.atcore.AviaTerraCore;
import net.atcore.Config;
import net.atcore.command.ArgumentUse;
import net.atcore.command.BaseCommand;
import net.atcore.messages.Message;
import net.atcore.messages.MessagesManager;
import net.atcore.messages.TypeMessages;
import net.atcore.security.Login.LoginManager;
import net.atcore.security.Login.ServerMode;
import net.atcore.security.Login.StateLogins;
import net.atcore.security.Login.model.LoginData;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;

import static net.atcore.messages.MessagesManager.sendFormatMessage;
import static net.atcore.messages.MessagesManager.sendMessage;

public class RegisterCommand extends BaseCommand {

    public RegisterCommand() {
        super("register",
                new ArgumentUse("/register").addNote("contraseña").addNote("contraseña"),//"/register <contraseña> <contraseña>"
                "**",
                "Te registras en el servidor añadiendo una contraseña",
                false);
        addAlias("reg");
    }

    private static final int LENGTH_MIN_PASSWORD = 4;

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof Player player){
            LoginData loginData = LoginManager.getDataLogin(player);
            if (loginData.getRegister().getPasswordShaded() == null){
                if (loginData.getRegister().getStateLogins() != StateLogins.PREMIUM || Config.getServerMode().equals(ServerMode.OFFLINE_MODE)){
                    if (args.length >= 2){
                        if (Objects.equals(args[0], args[1])){
                            if (args[0].length() > LENGTH_MIN_PASSWORD){
                                if (!args[0].equalsIgnoreCase(player.getName())){
                                    sendMessage(player, Message.COMMAND_REGISTER_SUCCESSFUL_CHAT.getMessage(player), TypeMessages.SUCCESS);
                                    LoginManager.newRegisterCracked(player,  args[0]);
                                    LoginManager.startPlaySessionCracked(player).getRegister().setTemporary(false);
                                    sendDiscord(sender);
                                    MessagesManager.sendTitle(player, String.format( Message.COMMAND_REGISTER_SUCCESSFUL_TITLE.getMessage(player), MessagesManager.PREFIX),
                                            String.format(Message.COMMAND_REGISTER_SUCCESSFUL_SUBTITLE.getMessage(player), player.getName())
                                            , 20, 20*3, 40, TypeMessages.INFO);
                                    LoginManager.checkLogin(player);
                                }else {
                                    sendMessage(player, Message.COMMAND_REGISTER_PASSWORD_EQUAL_NAME);
                                }
                            }else {
                                sendFormatMessage(player, Message.COMMAND_REGISTER_PASSWORD_TOO_SHORT, LENGTH_MIN_PASSWORD);
                            }
                        }else{
                            sendMessage(player, Message.COMMAND_REGISTER_NO_EQUAL_PASSWORD);
                        }
                    }else{
                        sendMessage(player, Message.COMMAND_REGISTER_MISSING_ARGS_PASSWORD);
                    }
                }else {
                    sendMessage(player, Message.COMMAND_REGISTER_IS_PREMIUM);
                }
            }else {
                sendMessage(player, Message.COMMAND_REGISTER_ALREADY);
            }
        }
    }
    public void sendDiscord(CommandSender sender) {
        new BukkitRunnable() {
            @Override
            public void run() {
                MessagesManager.sendFormatMessage(sender, Message.COMMAND_DISCORD_MESSAGE, MessagesManager.LINK_DISCORD);
            }
        }.runTaskLater(AviaTerraCore.getInstance(), 20*5);
    }
}
