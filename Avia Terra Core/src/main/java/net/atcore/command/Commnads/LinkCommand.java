package net.atcore.command.Commnads;

import net.atcore.AviaTerraCore;
import net.atcore.command.BaseTabCommand;
import net.atcore.command.CommandUtils;
import net.atcore.data.DataBaseRegister;
import net.atcore.messages.TypeMessages;
import net.atcore.security.Login.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

import static net.atcore.messages.MessagesManager.sendMessage;

public class LinkCommand extends BaseTabCommand {
    public LinkCommand() {
        super("link",
                "/link <gmail | discord> <Cuenta>",
                false,
                "Vinculas una cuenta para que mayor seguridad en el servidor"
        );
    }

    @Override
    public void execute(CommandSender sender, String[] args) throws Exception {
        if (sender instanceof Player player) {
            UUID uuid = player.getUniqueId();
            DataLogin dataLogin = LoginManager.getDataLogin(player);
            switch (args[0].toLowerCase()) {
                case "gmail" -> {
                    if (args.length >= 2) {
                        if (args[1].contains("@")){
                            CodeAuth codeAuth = new CodeAuth(UUID.randomUUID(), System.currentTimeMillis()+(1000*60*60), uuid, args[1].toLowerCase());
                            TwoFactorAuth.getCodes().put(uuid, codeAuth);
                            sendMessage(sender, "Se envió un correo a " + args[1] + " con el código", TypeMessages.INFO);
                            if (dataLogin.getRegister().getGmail() != null){
                                sendMessage(sender, "Ya Tiene un correo vinculado solo usar el comando para vincular un nuevo correo", TypeMessages.WARNING);
                            }
                            AviaTerraCore.getInstance().enqueueTaskAsynchronously(() -> {
                                TwoFactorAuth.sendVerificationEmail(args[1], codeAuth, FormatMessage.LINK);
                                sendMessage(sender, "Revisa su bandeja de recibidos, ya tuvo que haber llegado", TypeMessages.SUCCESS);
                            });
                        }else if (args[1].charAt(8) == '-'){
                            if (TwoFactorAuth.checkCode(player, args[1])){
                                AviaTerraCore.getInstance().enqueueTaskAsynchronously(() -> {
                                    DataBaseRegister.updateGmail(player.getName(), TwoFactorAuth.getCodes().get(uuid).getMedia());
                                    TwoFactorAuth.getCodes().remove(uuid);
                                    sendMessage(player, "Autenticación completa", TypeMessages.SUCCESS);
                                });
                            }
                        }else {
                            sendMessage(sender, "Tiene poner tu gmail o el código de validación", TypeMessages.ERROR);
                        }
                    }else {
                        if (dataLogin.getRegister().getGmail() != null){
                            CodeAuth codeAuth = new CodeAuth(UUID.randomUUID(), System.currentTimeMillis()+(1000*60*60), uuid, dataLogin.getRegister().getGmail());
                            TwoFactorAuth.getCodes().put(uuid, codeAuth);
                            sendMessage(sender, "Se esta enviando un correo con el código", TypeMessages.INFO);
                            AviaTerraCore.getInstance().enqueueTaskAsynchronously(() -> {
                                TwoFactorAuth.sendVerificationEmail(dataLogin.getRegister().getGmail(), codeAuth, FormatMessage.GENERIC);
                                sendMessage(sender, "Revisa su bandeja de recibidos, ya tuvo que haber llegado", TypeMessages.SUCCESS);
                            });
                        }else {
                            sendMessage(sender, "no tiene un Gmail vinculado", TypeMessages.ERROR);
                        }
                    }
                }
                case "discord" -> {
                    if (args.length >= 2) {
                        if (args[1].length() == 18){
                            CodeAuth codeAuth = new CodeAuth(UUID.randomUUID(), System.currentTimeMillis()+(1000*60*60), uuid, args[1].toLowerCase());
                            TwoFactorAuth.getCodes().put(uuid, codeAuth);
                            sendMessage(sender, "Se esta enviando un mensaje directo con el código", TypeMessages.INFO);
                            if (dataLogin.getRegister().getGmail() != null){
                                sendMessage(sender, "Ya Tiene un discord vinculado solo usar el comando para vincular un nuevo discord", TypeMessages.WARNING);
                            }
                            AviaTerraCore.getInstance().enqueueTaskAsynchronously(() -> TwoFactorAuth.sendVerificationDiscord(args[1], player, FormatMessage.LINK));
                        }else if (args[1].charAt(8) == '-') {
                            if (TwoFactorAuth.checkCode(player, args[1])){
                                AviaTerraCore.getInstance().enqueueTaskAsynchronously(() -> {
                                    DataBaseRegister.updateDiscord(player.getName(), TwoFactorAuth.getCodes().get(uuid).getMedia());
                                    TwoFactorAuth.getCodes().remove(uuid);
                                    sendMessage(player, "Autenticación completa", TypeMessages.SUCCESS);
                                });
                            }
                        }else {
                            sendMessage(player, "Tiene poner tu id del discord o el código de validación", TypeMessages.ERROR);
                        }
                    }else {
                        if (dataLogin.getRegister().getDiscord() != null){
                            CodeAuth codeAuth = new CodeAuth(UUID.randomUUID(), System.currentTimeMillis()+(1000*60*60), uuid, dataLogin.getRegister().getDiscord());
                            TwoFactorAuth.getCodes().put(uuid, codeAuth);
                            sendMessage(sender, "Se esta enviando un mensaje directo con el código", TypeMessages.INFO);
                            AviaTerraCore.getInstance().enqueueTaskAsynchronously(() ->
                                    TwoFactorAuth.sendVerificationDiscord(dataLogin.getRegister().getDiscord(), player, FormatMessage.GENERIC));
                        }else {
                            sendMessage(sender, "no tiene un Discord vinculado", TypeMessages.ERROR);
                        }
                    }
                }
                default -> sendMessage(sender, "tiene que poner discord o gmail", TypeMessages.ERROR);
            }
        }else {
            sendMessage(sender, "Solo lo puede ejecutar jugadores", TypeMessages.ERROR);
        }
    }

    @Override
    public List<String> onTab(CommandSender sender, String[] args) {
        switch (args.length){
            case 1 -> {
                return CommandUtils.listTab(args[0], List.of("gmail", "discord"));
            }
            case 2 -> {
                switch (args[0].toLowerCase()){
                    case "discord" -> {
                        if (args[1].length() > 9 && Character.isDigit(args[1].charAt(9))) {
                            return List.of("##################");
                        }else if (args[1].contains("-")){
                            return List.of("xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx");
                        }
                        return List.of("##################", "xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx");
                    }
                    case "gmail" -> {
                        if (args[1].contains("@")){
                            return List.of("TuCorreo@gmail.com");
                        }else if (args[1].contains("-")){
                            return List.of("xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx");
                        }
                        return List.of("TuCorreo@gmail.com", "xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx");
                    }
                }
            }
        }
        return null;
    }
}