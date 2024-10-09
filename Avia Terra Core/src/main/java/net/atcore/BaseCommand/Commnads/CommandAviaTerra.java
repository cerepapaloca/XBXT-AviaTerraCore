package net.atcore.BaseCommand.Commnads;

import net.atcore.BaseCommand.BaseTabCommand;
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

    }

    @Override
    public List<String> onTab(CommandSender sender, String[] args) {
        return null;
    }
}
