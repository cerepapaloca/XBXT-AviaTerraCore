package net.atcore.command.commnads;

import net.atcore.AviaTerraCore;
import net.atcore.command.*;
import net.atcore.messages.CategoryMessages;
import net.atcore.messages.MessagesManager;
import net.atcore.messages.TypeMessages;
import net.atcore.security.login.LoginManager;
import net.atcore.security.login.model.LoginData;
import net.atcore.utils.AviaTerraScheduler;
import net.atcore.utils.GlobalUtils;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class InfoCommand extends BaseCommand {

    public InfoCommand() {
        super("info",
                new ArgumentUse("info").addArgOptional().addArgPlayer(ModeTabPlayers.ADVANCED).addArg("more"),
                CommandVisibility.PRIVATE,
                "Te logueas"
        );
    }

    private static final int SPACE = 24;

    @Override
    public void execute(CommandSender sender, String[] args) throws Exception {
        User user = AviaTerraCore.getLp().getUserManager().getUser(sender.getName());
        String groupName;
        if (user != null) {
            groupName = user.getPrimaryGroup();
        }else {
            groupName = "? (usuario)";
        }
        if (args.length == 0) {
            send(sender,"|" + applySpace("Nombres") + "|" + applySpace("Localización") + "|" + applySpace("Rango") + "|" + applySpace("Ping"));
            for (Player p : Bukkit.getOnlinePlayers()) {
                send(sender,
                        "|" + applySpace(p.getName())
                        + "|" + applySpace(GlobalUtils.locationToString(p.getLocation()))
                        + "|" + applySpace(groupName)
                        + "|" + applySpace(String.valueOf(p.getPing()))
                );
            }
            send(sender," ");
        }else {
            AviaTerraScheduler.enqueueTaskAsynchronously(() -> CommandUtils.executeForPlayer(sender, args[0], true, (name, player) -> {
                LoginData data = LoginManager.getDataLogin(player);
                send(sender, String.format("Nombres: <|%s|>", player.getName()));
                send(sender, String.format("Display name: <|%s|>", player.displayName()));
                send(sender, String.format("UUID Cracked: <|%s|>", data.getRegister().getUuidCracked()));
                send(sender, String.format("UUID Premium: <|%s|>", data.getRegister().getUuidPremium()));
                send(sender, String.format("UUID Bedrock: <|%s|>", data.getRegister().getUuidBedrock()));
                send(sender, String.format("Localización: <|%s|>", GlobalUtils.locationToString(player.getLocation())));
                send(sender, String.format("Tiempo Logueado: <|%s|>", GlobalUtils.timeToString(System.currentTimeMillis() - data.getRegister().getLastLoginDate(), 1)));
                send(sender, String.format("Tiempo Registrado: <|%s|>", GlobalUtils.timeToString(System.currentTimeMillis() - data.getRegister().getRegisterDate(), 1)));
                send(sender, String.format("Tiempo Restante: <|%s|>", GlobalUtils.timeToString(System.currentTimeMillis() - data.getSession().getEndTimeLogin(), 1)));
                send(sender, String.format("Ping: <|%s|>", player.getPing()));
                send(sender, String.format("Ip: <|%s|>", player.getAddress() == null ? null : player.getAddress().getAddress().getHostAddress()));
                if (args.length == 1 || !args[1].equalsIgnoreCase("more")) return;
                send(sender, String.format("Modo de juego: <|%s|>", player.getGameMode().name().toLowerCase()));
                send(sender, String.format("Tipo de cuanta: <|%s|>", data.getRegister().getStateLogins().name().toLowerCase()));
                send(sender, String.format("Código de encriptación: <|%s|>", Arrays.toString(data.getSession().getSharedSecret())));
                send(sender, String.format("Modo Limbo: <|%s|>", data.isLimboMode()));
                send(sender, String.format("Contraseña: <|%s|>", data.getRegister().getPasswordShaded()));
                send(sender, String.format("Tiene sesión: <|%s|>", data.getSession() != null));
                send(sender, String.format("Cliente: <|%s|>", player.getClientBrandName()));
                send(sender, String.format("Discord: <|%s|>", data.getRegister().getDiscord()));
                send(sender, String.format("Mail: <|%s|>", data.getRegister().getMail()));
                send(sender, String.format("OP: <|%s|>", player.isOp()));
                send(sender, String.format("Sesión Expirada: <|%s|> (<|%s|>)", !LoginManager.checkLogin(player, false, false), !(data.getSession().getEndTimeLogin() > System.currentTimeMillis())));
                send(sender, String.format("Vida: <|%s|>", player.getHealth()));
                send(sender, String.format("Comida: <|%s|>", player.getFoodLevel()));
                send(sender, String.format("Nivel: <|%s|>", player.getLevel()));
                send(sender, String.format("Idioma: <|%s|>", player.locale()));
            }));
        }
    }

    private void send(CommandSender sender, String s){
        MessagesManager.sendMessage(sender,s, TypeMessages.INFO, CategoryMessages.PRIVATE, false);
    }

    private String applySpace(String s){
        return  s + " ".repeat(Math.max(0, SPACE - s.length()));
    }
}
