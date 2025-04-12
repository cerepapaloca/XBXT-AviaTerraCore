package net.atcore.command.commnads;

import net.atcore.AviaTerraCore;
import net.atcore.aviaterraplayer.AviaTerraPlayer;
import net.atcore.command.ArgumentUse;
import net.atcore.command.BaseTabCommand;
import net.atcore.command.CommandVisibility;
import net.atcore.messages.Message;
import net.atcore.messages.MessagesManager;
import net.atcore.utils.AviaTerraScheduler;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class UnblockCommand extends BaseTabCommand {
    public UnblockCommand() {
        super("unblock",
                new ArgumentUse("unblock").addNote("unBlock player"),
                CommandVisibility.PUBLIC,
                "desbloqueas a un jugador"
        );
    }

    @Override
    public void execute(CommandSender sender, String[] args) throws Exception {
        if (sender instanceof Player player){
            AviaTerraPlayer atp = AviaTerraPlayer.getPlayer(player);
            if (args.length > 0) {
                AviaTerraScheduler.enqueueTaskAsynchronously(() -> atp.getPlayerDataFile().saveData());
                if (atp.getPlayersBLock().remove(args[0])){
                    MessagesManager.sendFormatMessage(sender, Message.COMMAND_UNBLOCK_SUCCESSFUL, args[0]);
                }else {
                    MessagesManager.sendFormatMessage(sender, Message.COMMAND_UNBLOCK_NOT_FOUND, args[0]);
                }

            }else {
                MessagesManager.sendMessage(sender, Message.COMMAND_UNBLOCK_MISSING_ARG);
            }
        }else {
            MessagesManager.sendMessage(sender, Message.COMMAND_GENERIC_NO_PLAYER);
        }
    }

    @Override
    public List<String> onTab(CommandSender sender, String[] args) {
        if (sender instanceof Player player){
            AviaTerraPlayer atp = AviaTerraPlayer.getPlayer(player);
            if (args.length == 1) return atp.getPlayersBLock().stream().toList();
        }

        return List.of();
    }
}
