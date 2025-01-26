package net.atcore.command.commnads;

import net.atcore.command.ArgumentUse;
import net.atcore.command.BaseCommand;
import net.atcore.messages.Message;
import net.atcore.messages.MessagesManager;
import org.bukkit.command.CommandSender;

public class VoteCommand extends BaseCommand {
    public VoteCommand() {
        super("vote",
                new ArgumentUse("vote"),
                "*",
                "te enviá el link de la pagina de vote",
                false
        );
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        MessagesManager.sendFormatMessage(sender, Message.COMMAND_VOTE_MESSAGE, MessagesManager.LINK_VOTE);
    }
}
