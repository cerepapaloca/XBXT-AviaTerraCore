package net.atcore.command.commnads;

import net.atcore.AviaTerraCore;
import net.atcore.command.BaseTabCommand;
import net.atcore.command.CommandUtils;
import net.atcore.command.ModeTabPlayers;
import net.atcore.command.ArgumentUse;
import net.atcore.messages.Message;
import net.atcore.messages.MessagesType;
import net.atcore.moderation.ban.ContextBan;
import net.atcore.moderation.ModerationSection;
import org.bukkit.command.CommandSender;

import java.util.List;

import static net.atcore.messages.MessagesManager.sendMessage;

public class UnbanCommand extends BaseTabCommand {

    public UnbanCommand() {
        super("unban",
                new ArgumentUse("unban")
                        .addArgPlayer(ModeTabPlayers.ADVANCED)
                        .addArg("Contexto"),
                "desbanea a al jugador que le caes bien"
        );
    }

    @Override
    public void execute(CommandSender sender, String[] args) {//
        ContextBan contextBan;
        try {
            contextBan = ContextBan.valueOf(args[1].toUpperCase());
        }catch (Exception ignored) {
            sendMessage(sender, Message.COMMAND_UNBAN_NOT_FOUND_CONTEXT, MessagesType.ERROR);
            return;
        }
        //en un hilo aparte por qué explota el servidor
        CommandUtils.executeForPlayer(sender, args[0], false, dataTemporalPlayer ->
                AviaTerraCore.enqueueTaskAsynchronously(() ->
                        ModerationSection.getBanManager().removeBanPlayer(dataTemporalPlayer.name(), contextBan, sender.getName())));
        sendMessage(sender, Message.COMMAND_UNBAN_SUCCESSFUL, MessagesType.INFO);
    }

    @Override
    public List<String> onTab(CommandSender sender, String[] args) {
        if (args.length == 2){
            return CommandUtils.listTab(args[1], CommandUtils.enumsToStrings(ContextBan.values()));
        }else if (args.length == 1){
            return CommandUtils.tabForPlayer(args[0]);
        }
        return null;
    }
}
