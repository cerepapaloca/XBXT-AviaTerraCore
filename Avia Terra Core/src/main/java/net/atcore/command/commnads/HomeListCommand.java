package net.atcore.command.commnads;

import net.atcore.AviaTerraCore;
import net.atcore.aviaterraplayer.AviaTerraPlayer;
import net.atcore.command.ArgumentUse;
import net.atcore.command.BaseCommand;
import net.atcore.command.CommandVisibility;
import net.atcore.messages.Message;
import net.atcore.messages.MessagesManager;
import net.atcore.messages.TypeMessages;
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
            AviaTerraCore.enqueueTaskAsynchronously(() -> {
                AviaTerraPlayer aviaTerraPlayer = AviaTerraPlayer.getPlayer(player);
                AtomicInteger homeCount = new AtomicInteger(0);
                aviaTerraPlayer.getHomes().forEach((name, location) -> {
                    homeCount.set(homeCount.get() + 1);
                    MessagesManager.sendString(sender,"<click:run_command:/home " + name + "><hover:show_text:'" +
                            String.format(Message.COMMAND_HOME_LIST_HOVER.getMessage(player), name) + "'>" +
                            String.format(Message.COMMAND_HOME_LIST_SUCCESSFUL.getMessage(player),
                            homeCount.get(),
                            name,
                            GlobalUtils.locationToString(location),
                            location.getWorld() == player.getLocation().getWorld() ?
                                    (int) player.getLocation().distance(location) :
                                    "--"
                            ) + "</hover></click>", TypeMessages.INFO
                    );
                });
            });
        }else {
            MessagesManager.sendMessage(sender, Message.COMMAND_GENERIC_NO_PLAYER);
        }
    }
}
