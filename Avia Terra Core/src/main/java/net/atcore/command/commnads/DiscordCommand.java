package net.atcore.command.commnads;

import net.atcore.command.ArgumentUse;
import net.atcore.command.BaseCommand;
import net.atcore.command.CommandVisibility;
import net.atcore.messages.Message;
import net.atcore.messages.MessagesManager;
import org.bukkit.command.CommandSender;

public class DiscordCommand extends BaseCommand {

    public DiscordCommand() {
        super("discord",
                new ArgumentUse("discord"),
                CommandVisibility.PUBLIC,
                "Te enviá el link de invitación de discord"
        );
        addAlias("dc");
    }

    @Override
    public void execute(CommandSender sender, String[] args){
        MessagesManager.sendFormatMessage(sender, Message.COMMAND_DISCORD_MESSAGE, MessagesManager.LINK_DISCORD);
    }
}
