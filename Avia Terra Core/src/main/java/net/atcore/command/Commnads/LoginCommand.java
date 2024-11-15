package net.atcore.command.Commnads;

import net.atcore.AviaTerraCore;
import net.atcore.command.BaseCommand;
import net.atcore.command.ModeAutoTab;
import net.atcore.messages.MessagesManager;
import net.atcore.messages.TypeMessages;
import net.atcore.moderation.Ban.BanManager;
import net.atcore.moderation.Ban.ContextBan;
import net.atcore.moderation.ModerationSection;
import net.atcore.security.Login.*;
import net.atcore.utils.GlobalUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.UUID;

import static net.atcore.messages.MessagesManager.COLOR_ESPECIAL;
import static net.atcore.messages.MessagesManager.sendMessage;
import static net.atcore.security.Login.LoginManager.startPlaySessionCracked;

public class LoginCommand extends BaseCommand {

    public LoginCommand() {
        super("login",
                "/login <contraseña>",
                "*",
                "Te logueas",
                ModeAutoTab.NONE
        );
    }

    private final HashMap<UUID, Integer> attempts = new HashMap<>();

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof Player player) {
            DataLogin dataLogin = LoginManager.getDataLogin(player);
            if (dataLogin.getRegister().getPasswordShaded() != null) {
                if (LoginManager.isEqualPassword(player.getName(), args[0])){
                    if (LoginManager.checkLoginIn(player, true)) {
                        sendMessage(player, "Ya estas logueado", TypeMessages.ERROR);
                        return;
                    }
                    startPlaySessionCracked(player);
                    attempts.remove(player.getUniqueId());
                    LoginManager.updateLoginDataBase(player.getName(), Objects.requireNonNull(player.getAddress()).getAddress());
                    MessagesManager.sendTitle(player,"Bienvenido de vuelta", "<|&o" + player.getDisplayName() + "|>", 20, 20*3, 40, TypeMessages.INFO);
                    sendMessage(player, "Has iniciado session exitosamente", TypeMessages.SUCCESS);
                }else{
                    int i = attempts.getOrDefault(player.getUniqueId(), 0);
                    attempts.put(player.getUniqueId(), ++i);
                    if (i >= 5) AviaTerraCore.getInstance().enqueueTaskAsynchronously(() -> ModerationSection.getBanManager().banPlayer(player,
                            "Por su seguridad esta cuenta esta suspendido temporalmente por mucho intentos fallidos",
                            1000*60*5,
                            ContextBan.GLOBAL,
                            "Servidor"));
                    GlobalUtils.kickPlayer(player, "contraseña incorrecta, vuele a intentarlo. " +
                            "Si no se acuerda de su contraseña y tiene un corro o un discord vinculado puede" +
                            "puede enviar un código de verificación usando <|/link <discord | gmail>|>");
                }
            }else {
                sendMessage(player, "No estas registrado usa el /register", TypeMessages.ERROR);
            }
        }
    }
}
