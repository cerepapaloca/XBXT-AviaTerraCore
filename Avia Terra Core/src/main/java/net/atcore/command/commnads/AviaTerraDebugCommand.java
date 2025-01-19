package net.atcore.command.commnads;

import net.atcore.aviaterraplayer.AviaTerraPlayer;
import net.atcore.command.ArgumentUse;
import net.atcore.utils.debug.TypeTest;
import net.atcore.command.BaseTabCommand;
import net.atcore.command.CommandUtils;
import net.atcore.messages.MessagesManager;
import net.atcore.messages.MessagesType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class AviaTerraDebugCommand extends BaseTabCommand {

    public AviaTerraDebugCommand() {
        super("AviaTerraDebug",
                new ArgumentUse("AviaTerraPruebas").addArg("Pruebas"),
                "Commando para realizar pruebas del plugin",
                false);
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
                    MessagesManager.sendMessage(player, "Esta prueba no existe", MessagesType.ERROR);
                    return;
                }
                typeTest.runtTest(atp);
            }else {
                MessagesManager.sendMessage(player, this.getUsage().toString(), MessagesType.ERROR);
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
