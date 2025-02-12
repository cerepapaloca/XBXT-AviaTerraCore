package net.atcore.command.commnads;

import net.atcore.command.ArgumentUse;
import net.atcore.command.BaseCommand;
import net.atcore.command.CommandVisibility;
import net.atcore.messages.Message;
import net.atcore.messages.MessagesManager;
import org.bukkit.command.CommandSender;

public class HelpCommand extends BaseCommand {
    public HelpCommand() {
        super("help",
                new ArgumentUse("help"),
                CommandVisibility.PUBLIC,
                "Te pasa un link de la pagina para ver los comandos"
        );
        addAlias("?");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        MessagesManager.sendMessage(sender, Message.COMMAND_HELP_MESSAGE);
    }
}
