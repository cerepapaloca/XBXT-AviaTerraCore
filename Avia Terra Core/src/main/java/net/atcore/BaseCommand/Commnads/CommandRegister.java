package net.atcore.BaseCommand.Commnads;

import net.atcore.BaseCommand.BaseCommand;
import net.atcore.Config;
import net.atcore.ListenerManager.JoinAndExitListener;
import net.atcore.Messages.TypeMessages;
import net.atcore.Security.Login.*;
import net.atcore.Service.SimulateOnlineMode;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Objects;

import static net.atcore.Messages.MessagesManager.COLOR_ESPECIAL;
import static net.atcore.Messages.MessagesManager.sendMessage;

public class CommandRegister extends BaseCommand {

    public CommandRegister() {
        super("register",
                "/register <contraseña> <contraseña>",
                "",
                false,
                "Te registras"
        );
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof Player player){
            DataRegister register = LoginManager.getListRegister().get(player.getName());
            if (register.getPasswordShaded() == null){
                if (register.getStateLogins() == StateLogins.CRACKED || !Config.isMixedMode()){
                    if (args.length >= 2){
                        if (Objects.equals(args[0], args[1])){
                            sendMessage(player, "la contraseña se guardo exitosamente y registraste exitosamente", TypeMessages.SUCCESS);
                            DataSession session = new DataSession(player.getName(), player.getUniqueId(), StateLogins.CRACKED, player.getAddress().getAddress());
                            LoginManager.newRegisterCracked(player.getName(), player.getAddress().getAddress(),  args[0]);
                            player.sendTitle(ChatColor.translateAlternateColorCodes('&',COLOR_ESPECIAL + "Te haz registrado!"), "", 20, 20*3, 40);
                            session.setEndTimeLogin(System.currentTimeMillis() + 1000*60);
                            DataLimbo dataLimbo = LoginManager.getInventories().get(player.getUniqueId());
                            player.getInventory().setContents(dataLimbo.getItems());
                            player.teleport(dataLimbo.getLocation());
                            player.setGameMode(GameMode.SURVIVAL);
                            register.setTemporary(false);
                            try {
                                session.setPasswordShaded(LoginManager.hashPassword(player.getName(), args[0]));
                            } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
                                throw new RuntimeException(e);
                            }
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
