package net.atcore.command.commnads;

import net.atcore.armament.ArmamentActions;
import net.atcore.command.ArgumentUse;
import net.atcore.command.BaseCommand;
import net.atcore.command.CommandVisibility;
import net.atcore.messages.Message;
import net.atcore.messages.MessagesManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReloadCommand extends BaseCommand {

    public ReloadCommand() {
        super("reload",
                new ArgumentUse("reload"),
                CommandVisibility.PUBLIC,
                "Recargas el arma de fuego (Commando temporal)"
        );
        addAlias("rl");
    }
    @Override
    public void execute(CommandSender sender, String[] args) throws Exception {
        if (sender instanceof Player player){
            ArmamentActions.reloadAction(player, player.getInventory().getItemInMainHand());
        }else {
            MessagesManager.sendMessage(sender, Message.COMMAND_GENERIC_NO_PLAYER);
        }
    }
}
