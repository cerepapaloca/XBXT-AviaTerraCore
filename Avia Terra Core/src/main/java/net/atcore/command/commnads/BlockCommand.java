package net.atcore.command.commnads;

import net.atcore.AviaTerraCore;
import net.atcore.aviaterraplayer.AviaTerraPlayer;
import net.atcore.command.*;
import net.atcore.messages.Message;
import net.atcore.messages.MessagesManager;
import net.atcore.utils.AviaTerraScheduler;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class BlockCommand extends BaseCommand {

    public BlockCommand(){
        super("block",
                new ArgumentUse("block").addArgPlayer(ModeTabPlayers.ADVANCED),
                CommandVisibility.PUBLIC,
                "Hace que la actividad del jugador no la puedas ver"
        );
    }

    @Override
    public void execute(CommandSender sender, String[] args) throws Exception {
        if (sender instanceof Player p){
            if (args.length > 0){
                AviaTerraPlayer aviaTerraPlayer = AviaTerraPlayer.getPlayer(p);
                List<String> list = new ArrayList<>();
                CommandUtils.executeForPlayer(sender, args[0], false, (name, player) -> {
                    aviaTerraPlayer.getPlayersBLock().add(name);
                    list.add(name);
                });
                AviaTerraScheduler.enqueueTaskAsynchronously(() -> {
                    aviaTerraPlayer.getPlayerDataFile().saveData();
                    MessagesManager.sendFormatMessage(p, Message.COMMAND_BLOCK_SUCCESSFUL, String.join(", ", list));
                });

            }else {
                MessagesManager.sendMessage(p, Message.COMMAND_BLOCK_MISSING_ARG);
            }

        }else {
            MessagesManager.sendMessage(sender, Message.COMMAND_GENERIC_NO_PLAYER);
        }
    }
}
