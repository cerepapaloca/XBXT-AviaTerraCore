package net.atcore.command.commnads;

import net.atcore.AviaTerraCore;
import net.atcore.command.BaseTabCommand;
import net.atcore.command.CommandUtils;
import net.atcore.command.ModeTabPlayers;
import net.atcore.command.UseArgs;
import net.atcore.messages.TypeMessages;
import net.atcore.utils.GlobalUtils;
import org.bukkit.command.CommandSender;

import java.util.List;

import static net.atcore.messages.MessagesManager.sendMessage;

public class KickCommand extends BaseTabCommand {

    public KickCommand() {
        super("kick",
                new UseArgs("kick").addArgPlayer(ModeTabPlayers.ADVANCED).addArg("razón"),
                "Echas al jugador del servidor"
        );
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        switch (args.length){
            case 0, 1 -> sendMessage(sender, this.getUsage().toString(), TypeMessages.ERROR);
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

    @Override
    public List<String> onTab(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return CommandUtils.tabForPlayer(args[0]);
        }
        return List.of("Razón del kick...");
    }
}
