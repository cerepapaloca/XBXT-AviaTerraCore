package net.atcore.command.commnads;

import net.atcore.command.*;
import net.atcore.messages.MessagesType;
import net.atcore.utils.GlobalUtils;
import org.bukkit.command.CommandSender;

import java.util.List;

import static net.atcore.messages.MessagesManager.sendMessage;

public class KickCommand extends BaseCommand {

    public KickCommand() {
        super("kick",
                new ArgumentUse("kick").addArgPlayer(ModeTabPlayers.ADVANCED).addFinalArg("razón"),
                "Echas al jugador del servidor",
                false);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        switch (args.length){
            case 0, 1 -> sendMessage(sender, this.getUsage().toString(), MessagesType.ERROR);
            default -> {
                String reason = "";
                for (int i = 1; i < args.length; i++){
                    reason = reason.concat(args[i] + " ");
                }

                String finalReason = reason;//esto porque no puede se una variable reasignada
                CommandUtils.executeForPlayer(sender, args[0], true, dataTemporalPlayer -> GlobalUtils.kickPlayer(dataTemporalPlayer.player(), finalReason));
            }
        }
    }
}
