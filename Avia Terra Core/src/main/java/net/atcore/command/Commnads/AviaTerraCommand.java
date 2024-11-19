package net.atcore.command.Commnads;

import net.atcore.AviaTerraCore;
import net.atcore.command.BaseTabCommand;
import net.atcore.command.CommandUtils;
import net.atcore.Config;
import net.atcore.data.DataBaseMySql;
import net.atcore.data.DataSection;
import net.atcore.data.FileYaml;
import net.atcore.messages.TypeMessages;
import net.atcore.Section;
import net.atcore.security.Login.*;
import net.atcore.utils.GlobalUtils;
import net.atcore.utils.RegisterManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

import static net.atcore.messages.MessagesManager.sendMessage;

public class AviaTerraCommand extends BaseTabCommand {

    public AviaTerraCommand() {
        super("AviaTerra",
                "/AviaTerra",
                "Es un comando en cargador de todas la funciones de básicas del servidor para el staff"
        );
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        switch (args[0].toLowerCase().replace("_","")) {
            case "reload" -> {
                if (args.length >= 2) {
                    switch (args[1].toLowerCase().replace("_","")) {
                        case "yaml" -> AviaTerraCore.getInstance().enqueueTaskAsynchronously(() -> {

                            for (FileYaml filePermission : DataSection.getFileYaml()) filePermission.reloadConfig();
                            sendMessage(sender, "Archivos yaml recargado exitosamente", TypeMessages.SUCCESS);
                        });
                        case "sql" -> AviaTerraCore.getInstance().enqueueTaskAsynchronously(() -> {
                            sendMessage(sender, "Comenzando con la recarga de las bases de datos...", TypeMessages.INFO);
                            for (DataBaseMySql dataBaseMySql : DataSection.getDataBases()) dataBaseMySql.reloadDatabase();
                            sendMessage(sender, "Base de datos sql recargado exitosamente", TypeMessages.SUCCESS);
                        });
                    }

                }else{
                    for (Section section : RegisterManager.sections){
                        section.reloadConfig();
                    }
                }
            }
            case "antiop" -> {
                if (args.length >= 2) {
                    if (CommandUtils.isTrueOrFalse(args[1])){
                        Config.setCheckAntiOp(true);
                        sendMessage(sender,"Anti Op <|Activado|>", TypeMessages.INFO);
                    }else{
                        Config.setCheckAntiOp(false);
                        sendMessage(sender,"Anti Op <|Desactivado|>", TypeMessages.INFO);
                        sendMessage(sender,"****************************", TypeMessages.WARNING);
                        sendMessage(sender,"DESACTIVAR SOLO PARA PRUEBAS", TypeMessages.WARNING);
                        sendMessage(sender,"****************************", TypeMessages.WARNING);
                    }

                }else{
                    sendMessage(sender,"El anti Op esta <|" + CommandUtils.booleanToString(Config.isCheckAntiOp()) + "|>", TypeMessages.INFO);
                }

            }
            case "antiilegalitems" -> {
                if (args.length >= 2) {
                    if (CommandUtils.isTrueOrFalse(args[1])){
                        Config.setCheckAntiIllegalItems(true);
                        sendMessage(sender,"Anti Items Ilegales <|Activado|>", TypeMessages.INFO);
                    }else{
                        Config.setCheckAntiIllegalItems(false);
                        sendMessage(sender,"Anti Items Ilegales <|Desactivado|>", TypeMessages.INFO);
                        sendMessage(sender,"****************************", TypeMessages.WARNING);
                        sendMessage(sender,"DESACTIVAR SOLO PARA PRUEBAS", TypeMessages.WARNING);
                        sendMessage(sender,"****************************", TypeMessages.WARNING);
                    }
                }else{
                    sendMessage(sender,"el Anti Items Ilegales esta <|" + CommandUtils.booleanToString(Config.isCheckAntiIllegalItems()) + "|>", TypeMessages.INFO);
                }
            }
            case "servermode" -> {
                if (args.length >= 2) {
                    ServerMode mode;
                    try {
                        mode = ServerMode.valueOf(args[1].toUpperCase());
                    }catch (Exception e){
                        sendMessage(sender, "Modo no valido", TypeMessages.ERROR);
                        return;
                    }
                    if (CommandUtils.isTrueOrFalse(args[1])){
                        Config.setServerMode(mode);
                        sendMessage(sender,"El modo del servidor esta en " + Config.getServerMode().name().toLowerCase().replace("_"," "), TypeMessages.INFO);
                    }else{
                        Config.setServerMode(mode);
                        sendMessage(sender,"El modo del servidor esta en " + Config.getServerMode().name().toLowerCase().replace("_"," "), TypeMessages.INFO);
                        switch (Config.getServerMode()){
                            case OFFLINE_MODE -> {
                                for (Player player : Bukkit.getOnlinePlayers()){
                                    DataLogin login = LoginManager.getDataLogin(player);
                                    if (!login.hasSession()) {
                                        GlobalUtils.kickPlayer(player, "Se tiene que registrar/iniciar sesión con la contraseña code: 1");
                                        return;
                                    }
                                    if (login.getSession().getState().equals(StateLogins.PREMIUM)){
                                        login.setSession(null);
                                        GlobalUtils.kickPlayer(player, "Se tiene que registrar/iniciar sesión con la contraseña code: 2");
                                    }
                                }
                            }
                            case ONLINE_MODE -> {
                                for (Player player : Bukkit.getOnlinePlayers()){
                                    if (!LoginManager.getDataLogin(player).hasSession()) continue;
                                    if (LoginManager.getDataLogin(player).getSession().getState().equals(StateLogins.CRACKED)){
                                        GlobalUtils.kickPlayer(player, "El servidor entro en Online Mode");
                                    }
                                }
                            }
                        }
                    }

                }else{
                    sendMessage(sender,"El modo mixto esta <|" + Config.getServerMode().name().toLowerCase() + "|>", TypeMessages.INFO);
                }
            }
            case "checkbanporip" -> {
                if (args.length >= 2) {
                    if (CommandUtils.isTrueOrFalse(args[1])){
                        Config.setCheckBanByIp(true);
                        sendMessage(sender,"El check de baneo por ip <|Activado|>", TypeMessages.INFO);
                    }else{
                        Config.setCheckBanByIp(false);
                        sendMessage(sender,"El check de baneo por ip <|Desactivado|>", TypeMessages.INFO);
                        sendMessage(sender,"********************************************************", TypeMessages.WARNING);
                        sendMessage(sender,"SOLO PARA PRUEBAS O/Y PROBLEMAS CON LOS SERVIDOR DE AUTH", TypeMessages.WARNING);
                        sendMessage(sender,"********************************************************", TypeMessages.WARNING);
                    }

                }else{
                    sendMessage(sender,"El check de baneo por ip esta <|" + CommandUtils.booleanToString(Config.isCheckBanByIp()) + "|>", TypeMessages.INFO);
                }
            }
            case "purgarangos" -> {
                Config.setPurgeTagRange(System.currentTimeMillis());
                sendMessage(sender,"Todas los tags de rango ya no son validas y comenzara su eliminación", TypeMessages.INFO);
            }
            case "tiempodesesion" -> {
                if (args.length >= 2) {
                    try {
                        Config.setExpirationSession(CommandUtils.StringToMilliseconds(args[1], true));
                        sendMessage(sender, "se cambio el la duración de la sesión", TypeMessages.SUCCESS);
                    }catch (RuntimeException e){
                        sendMessage(sender, "formato de fecha incorrecto", TypeMessages.ERROR);
                    }
                }else{
                    sendMessage(sender,"el tiempo de expiración de expiration esta en <|" +
                            GlobalUtils.timeToString(Config.getExpirationSession(), 2) + "|>", TypeMessages.INFO);
                }
            }
            case "levelmoderationchat" -> {
                if (args.length >= 2) {
                    try {
                        Config.setLevelModerationChat(Float.parseFloat(args[1]));
                        sendMessage(sender, "se cambio el nivel de moderación", TypeMessages.SUCCESS);
                    }catch (RuntimeException e){
                        sendMessage(sender, "solo números con decimales", TypeMessages.ERROR);
                    }
                }else{
                    sendMessage(sender,"el nivel de moderación en el chat esta en <|" + Config.getLevelModerationChat() + "|>", TypeMessages.INFO);
                }
            }

            case "antibot" -> {
                if (args.length >= 2) {
                    if (CommandUtils.isTrueOrFalse(args[1])){
                        Config.setAntiBot(true);
                        sendMessage(sender,"AntiBot <|Activado|>", TypeMessages.INFO);
                    }else{
                        Config.setAntiBot(false);
                        sendMessage(sender,"AntiBot <|Desactivado|>", TypeMessages.INFO);
                        sendMessage(sender,"****************************", TypeMessages.WARNING);
                        sendMessage(sender,"DESACTIVAR SOLO PARA PRUEBAS", TypeMessages.WARNING);
                        sendMessage(sender,"****************************", TypeMessages.WARNING);
                    }
                }else{
                    sendMessage(sender,"El sistema antiBot esta <|" + CommandUtils.booleanToString(Config.isAntiBot()) + "|>", TypeMessages.INFO);
                }
            }
        }
    }

    @Override
    public List<String> onTab(CommandSender sender, String[] args) {
        String[] argsRoot = new String[]{"antiBot","reload", "antiOp", "antiIlegalItems", "serverMode", "checkBanPorIp", "purgaRangos","tiempoDeSesion", "levelModerationChat"};
        if (args.length >= 2) {
            switch (args[0].toLowerCase().replace("_","")) {
                case "antiop", "antiilegalitems", "checkbanporip", "antiBot" -> {
                    return CommandUtils.listTab(args[1], new String[]{"true", "false"});
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
                case "reload" -> {
                    return CommandUtils.listTab(args[1], new String[]{"yaml", "sql"});
                }
            }
        }
        return CommandUtils.listTab(args[0], argsRoot);
    }
}
