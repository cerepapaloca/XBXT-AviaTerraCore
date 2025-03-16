package net.atcore.command.commnads;

import net.atcore.aviaterraplayer.AviaTerraPlayer;
import net.atcore.command.ArgumentUse;
import net.atcore.command.BaseCommand;
import net.atcore.command.CommandVisibility;
import net.atcore.messages.Message;
import net.atcore.messages.MessagesManager;
import net.atcore.utils.GlobalUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.concurrent.atomic.AtomicInteger;

public class HomeListCommand extends BaseCommand {

    public HomeListCommand() {
        super("HomeList",
                new ArgumentUse("HomeList"),
                CommandVisibility.PUBLIC,
                "Vez todos tus homes"
        );
    }

    @Override
    public void execute(CommandSender sender, String[] args) throws Exception {
        if (sender instanceof Player player){
            AviaTerraPlayer aviaTerraPlayer = AviaTerraPlayer.getPlayer(player);
            AtomicInteger homeCount = new AtomicInteger(0);
            aviaTerraPlayer.getHomes().forEach((s, location) -> {
                homeCount.set(homeCount.get() + 1);
                MessagesManager.sendFormatMessage(sender,
                        Message.COMMAND_HOME_LIST_SUCCESSFUL,
                        homeCount.get(),
                        s,
                        GlobalUtils.locationToString(location),
                        player.getLocation().distance(location)
                );
            });
        }else {
            MessagesManager.sendMessage(sender, Message.COMMAND_GENERIC_NO_PLAYER);
        }
    }
}
