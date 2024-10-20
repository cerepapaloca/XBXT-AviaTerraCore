package net.atcore.BaseCommand.Commnads;

import net.atcore.AviaTerraCore;
import net.atcore.BaseCommand.BaseTabCommand;
import net.atcore.BaseCommand.CommandUtils;
import net.atcore.Messages.TypeMessages;
import net.atcore.Section;
import net.atcore.Security.AntiExploit;
import net.atcore.Utils.RegisterManager;
import org.bukkit.command.CommandSender;

import javax.swing.*;
import java.io.BufferedReader;
import java.util.*;

import static net.atcore.Messages.MessagesManager.sendMessage;
import static net.atcore.Messages.MessagesManager.sendMessageConsole;

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
        switch (args[0].toLowerCase()) {
            case "reload" -> {
                for (Section section : RegisterManager.sections){
                    section.reloadConfig();
                }
            }
            case "antiop" -> {
                if (args.length >= 2) {
                    if (CommandUtils.isTrueOrFalse(args[1])){
                        AntiExploit.setCheckAntiOP(true);
                        sendMessage(sender,"Anti Op <|Activado|>", TypeMessages.INFO);
                    }else{
                        AntiExploit.setCheckAntiOP(false);
                        sendMessage(sender,"Anti Op <|Desactivado|>", TypeMessages.INFO);
                        sendMessage(sender,"****************************", TypeMessages.WARNING);
                        sendMessage(sender,"DESACTIVAR SOLO PARA PRUEBAS", TypeMessages.WARNING);
                        sendMessage(sender,"****************************", TypeMessages.WARNING);
                    }

                }
                sendMessage(sender,"El anti Op esta" + CommandUtils.booleanToString(AntiExploit.isCheckAntiOP()), TypeMessages.INFO);
            }
        }
    }

    @Override
    public List<String> onTab(CommandSender sender, String[] args) {
        String[] argsRoot = new String[]{"reload", "antiop"};

        return Arrays.stream(argsRoot).toList();
    }
}
