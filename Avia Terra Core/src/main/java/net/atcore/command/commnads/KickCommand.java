package net.atcore.command.commnads;

import net.atcore.command.*;
import net.atcore.messages.TypeMessages;
import net.atcore.utils.GlobalUtils;
import org.bukkit.command.CommandSender;

import static net.atcore.messages.MessagesManager.*;

public class KickCommand extends BaseCommand {

    public KickCommand() {
        super("kick",
                new ArgumentUse("kick").addArgPlayer(ModeTabPlayers.ADVANCED).addFinalArg("razón"),
                CommandVisibility.PRIVATE,
                "Echas al jugador del servidor"
        );
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        switch (args.length){
            case 0, 1 -> sendArgument(sender, this.getAviaTerraUsage(), TypeMessages.ERROR);
            default -> {
                String reason = "";
                for (int i = 1; i < args.length; i++){
                    reason = reason.concat(args[i] + " ");
                }

                String finalReason = reason;//esto porque no puede se una variable reasignada
                CommandUtils.executeForPlayer(sender, args[0], true, (name, player) -> GlobalUtils.kickPlayer(player, finalReason));
            }
        }
    }
}
