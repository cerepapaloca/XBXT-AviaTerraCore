package net.atcore.BaseCommand.Commnads;

import net.atcore.BaseCommand.BaseCommand;
import net.atcore.Security.Login.DataRegister;
import net.atcore.Security.Login.DataSession;
import net.atcore.Security.Login.LoginManager;
import net.atcore.Security.Login.StateLogins;
import net.atcore.Utils.RegisterManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.BufferedReader;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class CommandLogin extends BaseCommand {

    public CommandLogin() {
        super("login",
                "/login <contraseña>",
                "aviaterra.command.login",
                false,
                "Te logueas"
        );
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof Player player) {
            try {
                if (LoginManager.isEqualPassword(player.getName(), args[0])){

                    DataRegister dataRegister = LoginManager.getListRegister().get(getName());
                    if (dataRegister.getUuidCracked().equals(player.getUniqueId())) {
                        new DataSession(player.getName(), player.getUniqueId(), dataRegister.getUuidPremium(), StateLogins.CRACKED);
                    }else {
                        throw new RuntimeException();
                    }
                }else{
                    throw new RuntimeException();
                }
            } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
