package net.atcore.command.commnads;

import net.atcore.command.ArgumentUse;
import net.atcore.command.BaseCommand;
import net.atcore.command.CommandVisibility;
import net.atcore.messages.Message;
import net.atcore.messages.MessagesManager;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class OffHandCommand extends BaseCommand {

    public OffHandCommand() {
        super("offHand",
                new ArgumentUse("offHand"),
                CommandVisibility.PUBLIC,
                "puedes pasar un item de tu mano principal a la mano segundaria para los de jugadores de bedrock"
        );
        addAlias("oh");
    }

    @Override
    public void execute(CommandSender sender, String[] args) throws Exception {
        if (sender instanceof Player player){
            ItemStack itemMainHand = player.getInventory().getItemInMainHand();
            ItemStack itemOffHand = player.getInventory().getItemInOffHand();
            player.getInventory().setItemInMainHand(itemOffHand);
            player.getInventory().setItemInOffHand(itemMainHand);
            player.playSound(player, Sound.ITEM_ARMOR_EQUIP_GENERIC, 1.0F, 1.0F);
        }else {
            MessagesManager.sendMessage(sender, Message.COMMAND_GENERIC_NO_PLAYER);
        }
    }
}
