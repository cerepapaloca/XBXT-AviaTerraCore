package net.atcore.command.commnads;

import net.atcore.AviaTerraCore;
import net.atcore.Config;
import net.atcore.Section;
import net.atcore.command.BaseTabCommand;
import net.atcore.command.CommandUtils;
import net.atcore.command.ArgumentUse;
import net.atcore.data.DataSection;
import net.atcore.messages.MessagesType;
import net.atcore.security.Login.ServerMode;
import net.atcore.utils.GlobalUtils;
import net.atcore.utils.RegisterManager;
import org.bukkit.command.CommandSender;

import java.util.List;

import static net.atcore.messages.MessagesManager.sendMessage;

public class AviaTerraCommand extends BaseTabCommand {

    public AviaTerraCommand() {
        super("AviaTerra",
                new ArgumentUse("aviaTerra"),
                "Se encarga de la configuración de algunos apartados del plugin",
                false);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        switch (args[0].toLowerCase().replace("_","")) {
            case "reload" -> {
                for (Section section : RegisterManager.sections){
                    section.reload();
                }
                AviaTerraCore.enqueueTaskAsynchronously(() -> sendMessage(sender,"Reload Terminado", MessagesType.SUCCESS));
            }
            case "antiop" -> {
                if (args.length >= 2) {
                    if (CommandUtils.isTrueOrFalse(args[1])){
                        Config.setCheckAntiOp(true);
                        sendMessage(sender,"Anti Op <|Activado|>", MessagesType.INFO);
                    }else{
                        Config.setCheckAntiOp(false);
                        sendMessage(sender,"Anti Op <|Desactivado|>", MessagesType.INFO);
                        sendMessage(sender,"****************************", MessagesType.WARNING);
                        sendMessage(sender,"DESACTIVAR SOLO PARA PRUEBAS", MessagesType.WARNING);
                        sendMessage(sender,"****************************", MessagesType.WARNING);
                    }

                }else{
                    sendMessage(sender,"El anti Op esta <|" + CommandUtils.booleanToString(Config.isCheckAntiOp()) + "|>", MessagesType.INFO);
                }
                DataSection.getConfigFile().saveData();
            }
            case "antiilegalitems" -> {
                if (args.length >= 2) {
                    if (CommandUtils.isTrueOrFalse(args[1])){
                        Config.setCheckAntiIllegalItems(true);
                        sendMessage(sender,"Anti Items Ilegales <|Activado|>", MessagesType.INFO);
                    }else{
                        Config.setCheckAntiIllegalItems(false);
                        sendMessage(sender,"Anti Items Ilegales <|Desactivado|>", MessagesType.INFO);
                        sendMessage(sender,"****************************", MessagesType.WARNING);
                        sendMessage(sender,"DESACTIVAR SOLO PARA PRUEBAS", MessagesType.WARNING);
                        sendMessage(sender,"****************************", MessagesType.WARNING);
                    }
                }else{
                    sendMessage(sender,"el Anti Items Ilegales esta <|" + CommandUtils.booleanToString(Config.isCheckAntiIllegalItems()) + "|>", MessagesType.INFO);
                }
                DataSection.getConfigFile().saveData();
            }
            case "servermode" -> {
                if (args.length >= 2) {
                    ServerMode mode;
                    try {
                        mode = ServerMode.valueOf(args[1].toUpperCase());
                    }catch (Exception e){
                        sendMessage(sender, "Modo no valido", MessagesType.ERROR);
                        return;
                    }
                    if (CommandUtils.isTrueOrFalse(args[1])){
                        Config.setServerMode(mode);
                        sendMessage(sender,"El modo del servidor esta en " + Config.getServerMode().name().toLowerCase().replace("_"," "), MessagesType.INFO);
                    }else{
                        Config.setServerMode(mode);
                        sendMessage(sender,"El modo del servidor esta en " + Config.getServerMode().name().toLowerCase().replace("_"," "), MessagesType.INFO);
                    }

                }else{
                    sendMessage(sender,"El modo mixto esta <|" + Config.getServerMode().name().toLowerCase() + "|>", MessagesType.INFO);
                }
                DataSection.getConfigFile().saveData();
            }
            case "checkbanporip" -> {
                if (args.length >= 2) {
                    if (CommandUtils.isTrueOrFalse(args[1])){
                        Config.setCheckBanByIp(true);
                        sendMessage(sender,"El check de baneo por ip <|Activado|>", MessagesType.INFO);
                    }else{
                        Config.setCheckBanByIp(false);
                        sendMessage(sender,"El check de baneo por ip <|Desactivado|>", MessagesType.INFO);
                        sendMessage(sender,"********************************************************", MessagesType.WARNING);
                        sendMessage(sender,"SOLO PARA PRUEBAS O/Y PROBLEMAS CON LOS SERVIDOR DE AUTH", MessagesType.WARNING);
                        sendMessage(sender,"********************************************************", MessagesType.WARNING);
                    }

                }else{
                    sendMessage(sender,"El check de baneo por ip esta <|" + CommandUtils.booleanToString(Config.isCheckBanByIp()) + "|>", MessagesType.INFO);
                }
                DataSection.getConfigFile().saveData();
            }
            case "purgarangos" -> {
                Config.setPurgeTagRange(System.currentTimeMillis());
                sendMessage(sender,"Todas los tags de rango ya no son validas y comenzara su eliminación", MessagesType.INFO);
                DataSection.getConfigFile().saveData();
            }
            case "tiempodesession" -> {
                if (args.length >= 2) {
                    try {
                        Config.setExpirationSession(CommandUtils.StringToMilliseconds(args[1], true));
                        sendMessage(sender, "se cambio el la duración de la sesión", MessagesType.SUCCESS);
                    }catch (RuntimeException e){
                        sendMessage(sender, "formato de fecha incorrecto", MessagesType.ERROR);
                    }
                }else{
                    sendMessage(sender,"el tiempo de expiración de expiration esta en <|" +
                            GlobalUtils.timeToString(Config.getExpirationSession(), 2) + "|>", MessagesType.INFO);
                }
                DataSection.getConfigFile().saveData();
            }
            case "levelmoderationchat" -> {
                if (args.length >= 2) {
                    try {
                        Config.setLevelModerationChat(Float.parseFloat(args[1]));
                        sendMessage(sender, "se cambio el nivel de moderación", MessagesType.SUCCESS);
                    }catch (RuntimeException e){
                        sendMessage(sender, "solo números con decimales", MessagesType.ERROR);
                    }
                }else{
                    sendMessage(sender,"El nivel de moderación en el chat esta en <|" + Config.getLevelModerationChat() + "|>", MessagesType.INFO);
                }
                DataSection.getConfigFile().saveData();
            }

            case "antibot" -> {
                if (args.length >= 2) {
                    if (CommandUtils.isTrueOrFalse(args[1])){
                        Config.setAntiBot(true);
                        sendMessage(sender,"AntiBot <|Activado|>", MessagesType.INFO);
                    }else{
                        Config.setAntiBot(false);
                        sendMessage(sender,"AntiBot <|Desactivado|>", MessagesType.INFO);
                    }
                }else{
                    sendMessage(sender,"El sistema antiBot esta <|" + CommandUtils.booleanToString(Config.isAntiBot()) + "|>", MessagesType.INFO);
                }
                DataSection.getConfigFile().saveData();
            }
            case "autoban" -> {
                if (args.length >= 2) {
                    if (CommandUtils.isTrueOrFalse(args[1])){
                        Config.setAutoBan(true);
                        sendMessage(sender,"autoBan <|Activado|>", MessagesType.INFO);
                    }else{
                        Config.setAutoBan(false);
                        sendMessage(sender,"autoBan <|Desactivado|>", MessagesType.INFO);
                    }
                }else{
                    sendMessage(sender,"El sistema autoBan esta <|" + CommandUtils.booleanToString(Config.isAntiBot()) + "|>", MessagesType.INFO);
                }
            }
        }
    }

    @Override
    public List<String> onTab(CommandSender sender, String[] args) {
        String[] argsRoot = new String[]{"antiBot","reload", "antiOp", "antiIlegalItems", "serverMode", "checkBanPorIp", "purgaRangos","tiempoDeSession", "levelModerationChat", "autoBan"};
        if (args.length >= 2) {
            switch (args[0].toLowerCase().replace("_","")) {
                case "antiop", "antiilegalitems", "checkbanporip", "antiBot", "autban" -> {
                    return CommandUtils.listTab(args[1], "true", "false");
                }
                case "servermode" -> {
                    return CommandUtils.listTab(args[1], CommandUtils.enumsToStrings(ServerMode.values()));
                }
                case "tiempodesesion" -> {
                    return CommandUtils.listTabTime(args[1], false);
                }
                case "levelmoderationchat" -> {
                    return List.of("#.#");
                }
            }
        }
        return CommandUtils.listTab(args[0], argsRoot);
    }
}
