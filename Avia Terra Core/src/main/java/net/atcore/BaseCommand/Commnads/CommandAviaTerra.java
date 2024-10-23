package net.atcore.BaseCommand.Commnads;

import net.atcore.BaseCommand.BaseTabCommand;
import net.atcore.BaseCommand.CommandUtils;
import net.atcore.Config;
import net.atcore.Messages.TypeMessages;
import net.atcore.Section;
import net.atcore.Security.Login.LoginManager;
import net.atcore.Security.Login.StateLogins;
import net.atcore.Utils.GlobalUtils;
import net.atcore.Utils.RegisterManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

import static net.atcore.Messages.MessagesManager.sendMessage;

public class CommandAviaTerra extends BaseTabCommand {

    public CommandAviaTerra() {
        super("AviaTerra",
                "/AviaTerra",
                "aviaterra.command.AviaTerra",
                true,
                "Es un comando en cargador de todas la funciones de básicas del servidor para el staff"
        );
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        switch (args[0].toLowerCase().replace("_","")) {
            case "reload" -> {
                for (Section section : RegisterManager.sections){
                    section.reloadConfig();
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
                    sendMessage(sender,"El anti Op esta " + CommandUtils.booleanToString(Config.isCheckAntiOp()), TypeMessages.INFO);
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
                    sendMessage(sender,"el Anti Items Ilegales esta " + CommandUtils.booleanToString(Config.isCheckAntiIllegalItems()), TypeMessages.INFO);
                }
            }
            case "mixmode" -> {
                if (args.length >= 2) {
                    if (CommandUtils.isTrueOrFalse(args[1])){
                        Config.setMixedMode(true);
                        sendMessage(sender,"El Modo Mixto <|Activado|>", TypeMessages.INFO);
                    }else{
                        Config.setMixedMode(false);
                        sendMessage(sender,"El Modo Mixto <|Desactivado|>", TypeMessages.INFO);
                        sendMessage(sender,"********************************************************", TypeMessages.WARNING);
                        sendMessage(sender,"SOLO PARA PRUEBAS O/Y PROBLEMAS CON LOS SERVIDOR DE AUTH", TypeMessages.WARNING);
                        sendMessage(sender,"********************************************************", TypeMessages.WARNING);

                        for (Player player : Bukkit.getOnlinePlayers()){
                            if (LoginManager.getListSession().get(player.getName()).getStateLogins().equals(StateLogins.PREMIUM)){
                                GlobalUtils.kickPlayer(player, "Se Tiene que registrar/iniciar sesión con la contraseña");
                            }
                        }
                    }

                }else{
                    sendMessage(sender,"El modo mixto esta " + CommandUtils.booleanToString(Config.isMixedMode()), TypeMessages.INFO);
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
                    sendMessage(sender,"El check de baneo por ip esta " + CommandUtils.booleanToString(Config.isCheckBanByIp()), TypeMessages.INFO);
                }
            }
            case "purgarangos" ->{
                Config.setPurgeTagRange(System.currentTimeMillis());
                sendMessage(sender,"Todas los tags de rango ya no son validas y comenzara su eliminación", TypeMessages.INFO);
            }
        }
    }

    @Override
    public List<String> onTab(CommandSender sender, String[] args) {
        String[] argsRoot = new String[]{"reload", "anti_Op", "anti_Ilegal_Items", "mix_Mode", "check_Ban_Por_Ip", "purga_Rangos",};
        if (args.length >= 2) {
            switch (args[0].toLowerCase().replace("_","")) {
                case "antiop", "antiilegalitems", "mixmode" -> {
                    return CommandUtils.listTab(args[1], new String[]{"true", "false"});
                }
            }
        }
        return Arrays.stream(argsRoot).toList();
    }
}
