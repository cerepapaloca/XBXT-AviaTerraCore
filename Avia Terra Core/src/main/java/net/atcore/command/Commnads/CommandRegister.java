package net.atcore.command.Commnads;

import net.atcore.command.BaseCommand;
import net.atcore.Config;
import net.atcore.messages.TypeMessages;
import net.atcore.security.Login.*;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;

import static net.atcore.messages.MessagesManager.COLOR_ESPECIAL;
import static net.atcore.messages.MessagesManager.sendMessage;
import static net.atcore.security.Login.LoginManager.startPlaySessionCracked;

public class CommandRegister extends BaseCommand {

    public CommandRegister() {
        super("register",
                "/register <contraseña> <contraseña>",
                "*",
                false,
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
                            sendMessage(player, "la contraseña se guardo exitosamente y registraste exitosamente", TypeMessages.SUCCESS);
                            LoginManager.newRegisterCracked(player.getName(), player.getAddress().getAddress(),  args[0]);
                            player.sendTitle(ChatColor.translateAlternateColorCodes('&',COLOR_ESPECIAL + "Te haz registrado!"), "", 20, 20*3, 40);
                            startPlaySessionCracked(player).getRegister().setTemporary(false);;
                            LoginManager.checkLoginIn(player, true);
                        }else{
                            sendMessage(player, "las contraseña no son iguales", TypeMessages.ERROR);
                        }
                    }else{
                        sendMessage(player, "tiene que escribir la contraseña de nuevo", TypeMessages.ERROR);
                    }
                }else {
                    sendMessage(player, "Los premium no se registran", TypeMessages.ERROR);
                }
            }else {
                sendMessage(player, "Ya estas registrado", TypeMessages.ERROR);
            }
        }
    }
}
