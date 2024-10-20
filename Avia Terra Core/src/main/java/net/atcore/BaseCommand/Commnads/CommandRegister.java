package net.atcore.BaseCommand.Commnads;

import net.atcore.BaseCommand.BaseCommand;
import net.atcore.Messages.TypeMessages;
import net.atcore.Security.Login.DataSession;
import net.atcore.Security.Login.LoginManager;
import net.atcore.Security.Login.StateLogins;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;

import static net.atcore.Messages.MessagesManager.COLOR_ESPECIAL;
import static net.atcore.Messages.MessagesManager.sendMessage;

public class CommandRegister extends BaseCommand {

    public CommandRegister() {
        super("register",
                "/register <contraseña> <contraseña>",
                "aviaterra.command.login",
                false,
                "Te registras"
        );
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof Player player){
            if (LoginManager.getListRegister().containsKey(player.getName())){
                if (LoginManager.getListRegister().get(player.getName()).getPasswordShaded() == null){
                    if (args.length >= 2){
                        if (Objects.equals(args[0], args[1])){
                            sendMessage(player, "la contraseña se guardo exitosamente y registraste exitosamente", TypeMessages.SUCCESS);
                            DataSession session = new DataSession(player.getName(), player.getUniqueId(), StateLogins.CRACKED, player.getAddress().getAddress());
                            LoginManager.newRegisterCracked(player.getName(), player.getAddress().getAddress(),  args[0]);
                            player.sendTitle(ChatColor.translateAlternateColorCodes('&',COLOR_ESPECIAL + "Te haz registrado!"), "", 20, 20*3, 40);
                            session.setEndTimeLogin(System.currentTimeMillis() + 1000*60);
                            LoginManager.isLoginIn(player, true);
                        }else{
                            sendMessage(player, "las contra seña no son iguales", TypeMessages.ERROR);
                        }
                    }else{
                        sendMessage(player, "tiene que escribir la contraseña de nuevo", TypeMessages.ERROR);
                    }
                }else {
                    sendMessage(player, "Ya estas registrado", TypeMessages.ERROR);
                }
            }else {
                sendMessage(player, "Ya estas registrado", TypeMessages.ERROR);
            }
        }
    }
}
