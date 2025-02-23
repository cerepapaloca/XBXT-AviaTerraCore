package net.atcore.command.commnads;

import net.atcore.command.ArgumentUse;
import net.atcore.command.BaseCommand;
import net.atcore.command.CommandVisibility;
import net.atcore.messages.Message;
import net.atcore.messages.MessagesManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KillCommand extends BaseCommand {

    public KillCommand() {
        super("kill", new ArgumentUse("kill"), CommandVisibility.PUBLIC, "Te matás");
    }

    @Override
    public void execute(CommandSender sender, String[] args) throws Exception {
        if (sender instanceof Player player) {
            player.setHealth(0);
        }else {
            MessagesManager.sendMessage(sender, Message.COMMAND_GENERIC_NO_PLAYER);
        }
    }
}
