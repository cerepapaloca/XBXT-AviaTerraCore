package net.atcore.command.commnads;

import net.atcore.command.ArgumentUse;
import net.atcore.command.BaseCommand;
import net.atcore.command.CommandHandler;
import net.atcore.command.CommandVisibility;
import net.atcore.messages.Message;
import net.atcore.messages.MessagesManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ConfirmCommand extends BaseCommand {
    public ConfirmCommand() {
        super("confirm",
                new ArgumentUse("confirm"),
                CommandVisibility.ALL,
                "Confirmas comando importantes"
        );
    }

    @Override
    public void execute(CommandSender sender, String[] args) throws Exception {
        if (sender instanceof Player player){
            String command = CommandHandler.SAVES_COMMANDS_CONFIRMS.get(player.getUniqueId());
            if (command != null){
                Bukkit.dispatchCommand(player, command);
            }else {
                MessagesManager.sendMessage(player, Message.COMMAND_CONFIRM_NOT_FOUND);
            }

        }else {
            MessagesManager.sendMessage(sender, Message.COMMAND_GENERIC_NO_PLAYER);
        }
    }
}
