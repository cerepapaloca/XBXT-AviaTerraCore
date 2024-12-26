package net.atcore.command.commnads;

import net.atcore.aviaterraplayer.AviaTerraPlayer;
import net.atcore.aviaterraplayer.ModerationPlayer;
import net.atcore.command.BaseTabCommand;
import net.atcore.command.CommandUtils;
import net.atcore.messages.Message;
import net.atcore.messages.TypeMessages;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

import static net.atcore.messages.MessagesManager.sendMessage;

public class FreezeCommand extends BaseTabCommand {
    public FreezeCommand() {
        super("freeze",
                "/freeze <player> <on_|_off>",
                "Este comando congelas a un jugador por actividad sospechosa"
        );
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sendMessage(sender, this.getUsage(), TypeMessages.ERROR);
            return;
        }
        if (args.length == 1) {
            sendMessage(sender, Message.COMMAND_FREEZE_MISSING_ARGS_LAST.getMessage(), TypeMessages.ERROR);
            return;
        }
        CommandUtils.executeForPlayer(sender, args[0], true, dataTemporalPlayer -> {
            ModerationPlayer moderationPlayer = AviaTerraPlayer.getPlayer(dataTemporalPlayer.player()).getModerationPlayer();
            Player player = dataTemporalPlayer.player();
            switch (args[1].toLowerCase()){
                case "true" -> {
                    if (moderationPlayer.isFreeze()) {
                        sendMessage(sender, Message.COMMAND_FREEZE_ALREADY_FREEZE.getMessage(), TypeMessages.ERROR);
                    }else {
                        moderationPlayer.setFreeze(true);
                        player.closeInventory();
                        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Integer.MAX_VALUE, 1));
                        sendMessage(player, Message.COMMAND_FREEZE_FREEZE_TARGET.getMessage(), TypeMessages.INFO);
                        sendMessage(sender, Message.COMMAND_FREEZE_FREEZE_AUTHOR.getMessage(), TypeMessages.SUCCESS);
                    }

                }
                case "false" -> {
                    if (moderationPlayer.isFreeze()) {
                        moderationPlayer.setFreeze(false);
                        player.removePotionEffect(PotionEffectType.BLINDNESS);
                        sendMessage(sender, Message.COMMAND_FREEZE_UNFREEZE_AUTHOR.getMessage(), TypeMessages.SUCCESS);
                        sendMessage(player, Message.COMMAND_FREEZE_UNFREEZE_TARGET.getMessage(), TypeMessages.INFO);
                    }else{
                        sendMessage(sender, Message.COMMAND_FREEZE_ALREADY_UNFREEZE.getMessage(), TypeMessages.ERROR);
                    }
                }
            }
        });
    }

    @Override
    public List<String> onTab(CommandSender sender, String[] args) {
        if (args.length == 2) {
            return CommandUtils.listTab(args[1], new String[]{"true", "false"});
        }else if (args.length == 1) {
            return CommandUtils.tabForPlayer(args[0]);
        }
        return null;
    }
}
