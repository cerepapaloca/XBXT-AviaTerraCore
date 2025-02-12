package net.atcore.command.commnads;

import net.atcore.aviaterraplayer.AviaTerraPlayer;
import net.atcore.aviaterraplayer.ModerationPlayer;
import net.atcore.command.*;
import net.atcore.messages.Message;
import net.atcore.messages.TypeMessages;
import org.bukkit.command.CommandSender;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

import static net.atcore.messages.MessagesManager.sendArgument;
import static net.atcore.messages.MessagesManager.sendMessage;

public class FreezeCommand extends BaseTabCommand {
    public FreezeCommand() {
        super("freeze",
                new ArgumentUse("freeze")
                        .addArgPlayer(ModeTabPlayers.ADVANCED)
                        .addArg("true", "false"),
                CommandVisibility.PRIVATE,
                "Este comando congelas a un jugador por actividad sospechosa"
        );
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sendArgument(sender, this.getAviaTerraUsage(), TypeMessages.ERROR);
            return;
        }
        if (args.length == 1) {
            sendMessage(sender, Message.COMMAND_FREEZE_MISSING_ARGS_LAST);
            return;
        }
        CommandUtils.executeForPlayer(sender, args[0], true, (name, player) -> {
            ModerationPlayer moderationPlayer = AviaTerraPlayer.getPlayer(player).getModerationPlayer();
            switch (args[1].toLowerCase()){
                case "true" -> {
                    if (moderationPlayer.isFreeze()) {
                        sendMessage(sender, Message.COMMAND_FREEZE_ALREADY_FREEZE);
                    }else {
                        moderationPlayer.setFreeze(true);
                        player.closeInventory();
                        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Integer.MAX_VALUE, 1));
                        sendMessage(player, Message.COMMAND_FREEZE_FREEZE_TARGET);
                        sendMessage(sender, Message.COMMAND_FREEZE_FREEZE_AUTHOR);
                    }

                }
                case "false" -> {
                    if (moderationPlayer.isFreeze()) {
                        moderationPlayer.setFreeze(false);
                        player.removePotionEffect(PotionEffectType.BLINDNESS);
                        sendMessage(sender, Message.COMMAND_FREEZE_UNFREEZE_AUTHOR);
                        sendMessage(player, Message.COMMAND_FREEZE_UNFREEZE_TARGET);
                    }else{
                        sendMessage(sender, Message.COMMAND_FREEZE_ALREADY_UNFREEZE);
                    }
                }
            }
        });
    }

    @Override
    public List<String> onTab(CommandSender sender, String[] args) {
        if (args.length == 2) {
            return CommandUtils.listTab(args[1], "true", "false");
        }else if (args.length == 1) {
            return CommandUtils.tabForPlayer(args[0]);
        }
        return null;
    }
}
