package net.atcore.command.commnads;

import net.atcore.AviaTerraCore;
import net.atcore.Config;
import net.atcore.Section;
import net.atcore.aviaterraplayer.AviaTerraPlayer;
import net.atcore.command.BaseTabCommand;
import net.atcore.command.CommandUtils;
import net.atcore.command.ArgumentUse;
import net.atcore.data.DataSection;
import net.atcore.messages.TypeMessages;
import net.atcore.security.Login.ServerMode;
import net.atcore.utils.GlobalUtils;
import net.atcore.utils.RegisterManager;
import org.bukkit.command.CommandSender;

import java.util.List;

import static net.atcore.messages.MessagesManager.sendString;

public class AviaTerraCommand extends BaseTabCommand {

    public AviaTerraCommand() {
        super("AviaTerra",
                new ArgumentUse("aviaTerra"),
                "Se encarga de la configuración de algunos apartados del plugin",
                false);
        addAlias("at");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        switch (args[0].toLowerCase().replace("_","")) {
            case "reload" -> {
                AviaTerraCore.getInstance().reloadConfig();
                AviaTerraCore.enqueueTaskAsynchronously(() -> sendString(sender,"Reload Terminado", TypeMessages.SUCCESS));
            }
            case "antiop" -> {
                if (args.length >= 2) {
                    if (CommandUtils.isTrueOrFalse(args[1])){
                        Config.setCheckAntiOp(true);
                        sendString(sender,"Anti Op <|Activado|>", TypeMessages.INFO);
                    }else{
                        Config.setCheckAntiOp(false);
                        sendString(sender,"Anti Op <|Desactivado|>", TypeMessages.INFO);
                    }

                }else{
                    sendString(sender,"El anti Op esta <|" + CommandUtils.booleanToString(Config.isCheckAntiOp()) + "|>", TypeMessages.INFO);
                }
                DataSection.getConfigFile().saveData();
            }
            case "antiilegalitems" -> {
                if (args.length >= 2) {
                    if (CommandUtils.isTrueOrFalse(args[1])){
                        Config.setCheckAntiIllegalItems(true);
                        sendString(sender,"Anti Items Ilegales <|Activado|>", TypeMessages.INFO);
                    }else{
                        Config.setCheckAntiIllegalItems(false);
                        sendString(sender,"Anti Items Ilegales <|Desactivado|>", TypeMessages.INFO);
                    }
                }else{
                    sendString(sender,"el Anti Items Ilegales esta <|" + CommandUtils.booleanToString(Config.isCheckAntiIllegalItems()) + "|>", TypeMessages.INFO);
                }
                DataSection.getConfigFile().saveData();
            }
            case "servermode" -> {
                if (args.length >= 2) {
                    ServerMode mode;
                    try {
                        mode = ServerMode.valueOf(args[1].toUpperCase());
                    }catch (Exception e){
                        sendString(sender, "Modo no valido", TypeMessages.ERROR);
                        return;
                    }
                    if (CommandUtils.isTrueOrFalse(args[1])){
                        Config.setServerMode(mode);
                        sendString(sender,"El modo del servidor esta en " + Config.getServerMode().name().toLowerCase().replace("_"," "), TypeMessages.INFO);
                    }else{
                        Config.setServerMode(mode);
                        sendString(sender,"El modo del servidor esta en " + Config.getServerMode().name().toLowerCase().replace("_"," "), TypeMessages.INFO);
                    }

                }else{
                    sendString(sender,"El modo mixto esta <|" + Config.getServerMode().name().toLowerCase() + "|>", TypeMessages.INFO);
                }
                DataSection.getConfigFile().saveData();
            }
            case "checkbanporip" -> {
                if (args.length >= 2) {
                    if (CommandUtils.isTrueOrFalse(args[1])){
                        Config.setCheckBanByIp(true);
                        sendString(sender,"El check de baneo por ip <|Activado|>", TypeMessages.INFO);
                    }else{
                        Config.setCheckBanByIp(false);
                        sendString(sender,"El check de baneo por ip <|Desactivado|>", TypeMessages.INFO);
                    }

                }else{
                    sendString(sender,"El check de baneo por ip esta <|" + CommandUtils.booleanToString(Config.isCheckBanByIp()) + "|>", TypeMessages.INFO);
                }
                DataSection.getConfigFile().saveData();
            }
            case "purgarangos" -> {
                Config.setPurgeTagRange(System.currentTimeMillis());
                sendString(sender,"Todas los tags de rango ya no son validas y comenzara su eliminación", TypeMessages.INFO);
                DataSection.getConfigFile().saveData();
            }
            case "tiempodesession" -> {
                if (args.length >= 2) {
                    try {
                        Config.setExpirationSession(CommandUtils.StringToMilliseconds(args[1], true));
                        sendString(sender, "se cambio el la duración de la sesión", TypeMessages.SUCCESS);
                    }catch (RuntimeException e){
                        sendString(sender, "formato de fecha incorrecto", TypeMessages.ERROR);
                    }
                }else{
                    sendString(sender,"el tiempo de expiración de expiration esta en <|" +
                            GlobalUtils.timeToString(Config.getExpirationSession(), 2) + "|>", TypeMessages.INFO);
                }
                DataSection.getConfigFile().saveData();
            }
            case "levelmoderationchat" -> {
                if (args.length >= 2) {
                    try {
                        Config.setLevelModerationChat(Float.parseFloat(args[1]));
                        sendString(sender, "se cambio el nivel de moderación", TypeMessages.SUCCESS);
                    }catch (RuntimeException e){
                        sendString(sender, "solo números con decimales", TypeMessages.ERROR);
                    }
                }else{
                    sendString(sender,"El nivel de moderación en el chat esta en <|" + Config.getLevelModerationChat() + "|>", TypeMessages.INFO);
                }
                DataSection.getConfigFile().saveData();
            }

            case "antibot" -> {
                if (args.length >= 2) {
                    if (CommandUtils.isTrueOrFalse(args[1])){
                        Config.setAntiBot(true);
                        sendString(sender,"AntiBot <|Activado|>", TypeMessages.INFO);
                    }else{
                        Config.setAntiBot(false);
                        sendString(sender,"AntiBot <|Desactivado|>", TypeMessages.INFO);
                    }
                }else{
                    sendString(sender,"El sistema antiBot esta <|" + CommandUtils.booleanToString(Config.isAntiBot()) + "|>", TypeMessages.INFO);
                }
                DataSection.getConfigFile().saveData();
            }
            case "autoban" -> {
                if (args.length >= 2) {
                    if (CommandUtils.isTrueOrFalse(args[1])){
                        Config.setAutoBan(true);
                        sendString(sender,"autoBan <|Activado|>", TypeMessages.INFO);
                    }else{
                        Config.setAutoBan(false);
                        sendString(sender,"autoBan <|Desactivado|>", TypeMessages.INFO);
                    }
                }else{
                    sendString(sender,"El sistema autoBan esta <|" + CommandUtils.booleanToString(Config.isAntiBot()) + "|>", TypeMessages.INFO);
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
