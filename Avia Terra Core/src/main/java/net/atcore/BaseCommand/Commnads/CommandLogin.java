package net.atcore.BaseCommand.Commnads;

import com.comphenix.protocol.PacketType;
import net.atcore.BaseCommand.BaseCommand;
import net.atcore.Messages.TypeMessages;
import net.atcore.Security.Login.DataSession;
import net.atcore.Security.Login.LoginManager;
import net.atcore.Security.Login.StateLogins;
import net.atcore.Utils.GlobalUtils;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import static net.atcore.Messages.MessagesManager.COLOR_ESPECIAL;
import static net.atcore.Messages.MessagesManager.sendMessage;

public class CommandLogin extends BaseCommand {

    public CommandLogin() {
        super("login",
                "/login <contraseña>",
                "",
                false,
                "Te logueas"
        );
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof Player player) {
            try {
                if (LoginManager.getListRegister().get(player.getName()).getPasswordShaded() != null) {
                    if (LoginManager.isEqualPassword(player.getName(), args[0])){
                        if (LoginManager.checkLoginIn(player, true)) {
                            sendMessage(player, "Ya estas logueado", TypeMessages.ERROR);
                            return;
                        }
                        DataSession session = new DataSession(player.getName(), player.getUniqueId(), StateLogins.CRACKED, player.getAddress().getAddress());
                        session.setEndTimeLogin(System.currentTimeMillis() + 1000*30);
                        session.setPasswordShaded(LoginManager.hashPassword(player.getName(), args[0]));
                        player.sendTitle(ChatColor.translateAlternateColorCodes('&',COLOR_ESPECIAL + "Te haz logueado!"), "", 20, 20*3, 40);
                        sendMessage(player, "Has iniciado session exitosamente", TypeMessages.SUCCESS);
                        player.setGameMode(GameMode.SURVIVAL);
                        LoginManager.updateLoginDataBase(player.getName(), player.getAddress().getAddress());
                    }else{
                        GlobalUtils.kickPlayer(player, "contraseña incorrecta, vuele a intentarlo");
                    }
                }else {
                    sendMessage(player, "No estas registrado usa el /register", TypeMessages.ERROR);
                }
            } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
