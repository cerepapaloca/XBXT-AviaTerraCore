package net.atcore.command.Commnads;

import net.atcore.AviaTerraCore;
import net.atcore.command.BaseTabCommand;
import org.bukkit.command.CommandSender;

import java.util.List;

public class AviaTerraPruebasCommand extends BaseTabCommand {

    AviaTerraPruebasCommand(AviaTerraCore plugin) {
        super("AviaTerraPruebas",
                "/AviaTerraPruebas <Pruebas>",
                true,
                "puede ver y modificar el inventario del jugador"
        );
    }

    @Override
    public void execute(CommandSender sender, String[] args) throws Exception {

    }

    @Override
    public List<String> onTab(CommandSender sender, String[] args) {
        return List.of();
    }
}
