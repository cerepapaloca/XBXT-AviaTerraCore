package net.atcore.BaseCommand.Commnads;

import net.atcore.BaseCommand.BaseTabCommand;
import net.atcore.Section;
import net.atcore.Utils.RegisterManager;
import org.bukkit.command.CommandSender;

import java.util.*;

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
        switch (args[0].toLowerCase()) {//ya sé que no tiene tanto sentido, pero en el futuro sé va llenando de otras funciones
            case "reload" -> {
                for (Section section : RegisterManager.sections){
                    section.reloadConfig();
                }
            }
        }
    }

    @Override
    public List<String> onTab(CommandSender sender, String[] args) {
        String[] argsRoot = new String[]{"reload"};

        return Arrays.stream(argsRoot).toList();
    }
}
