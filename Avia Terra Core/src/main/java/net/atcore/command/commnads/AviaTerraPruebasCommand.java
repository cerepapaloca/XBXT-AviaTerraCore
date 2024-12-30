package net.atcore.command.commnads;

import net.atcore.aviaterraplayer.AviaTerraPlayer;
import net.atcore.command.UseArgs;
import net.atcore.test.TypeTest;
import net.atcore.command.BaseTabCommand;
import net.atcore.command.CommandUtils;
import net.atcore.messages.MessagesManager;
import net.atcore.messages.TypeMessages;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class AviaTerraPruebasCommand extends BaseTabCommand {

    public AviaTerraPruebasCommand() {
        super("AviaTerraPruebas",
                new UseArgs("AviaTerraPruebas").addArg("Pruebas"),
                "Commando para realizar pruebas del plugin"
        );
    }

    @Override
    public void execute(CommandSender sender, String[] args) throws Exception {
        if (sender instanceof Player player) {
            if (args.length >= 1) {
                AviaTerraPlayer atp = AviaTerraPlayer.getPlayer(player);
                TypeTest typeTest;
                try {
                    typeTest = TypeTest.valueOf(args[0].toUpperCase());
                }catch (Exception e) {
                    MessagesManager.sendMessage(player, "Esta prueba no existe", TypeMessages.ERROR);
                    return;
                }
                typeTest.runtTest(atp);
            }else {
                MessagesManager.sendMessage(player, this.getUsage().toString(), TypeMessages.ERROR);
            }
        }
    }

    @Override
    public List<String> onTab(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return CommandUtils.listTab(args[0], CommandUtils.enumsToStrings(TypeTest.values(), true));
        }
        return null;
    }
}
