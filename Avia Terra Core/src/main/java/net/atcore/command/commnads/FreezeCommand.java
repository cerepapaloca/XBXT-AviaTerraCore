package net.atcore.command.commnads;

import net.atcore.aviaterraplayer.AviaTerraPlayer;
import net.atcore.aviaterraplayer.ModerationPlayer;
import net.atcore.command.BaseTabCommand;
import net.atcore.command.CommandUtils;
import net.atcore.command.ModeTabPlayers;
import net.atcore.command.ArgumentUse;
import net.atcore.messages.Message;
import net.atcore.messages.MessagesType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

import static net.atcore.messages.MessagesManager.sendMessage;

public class FreezeCommand extends BaseTabCommand {
    public FreezeCommand() {
        super("freeze",
                new ArgumentUse("freeze")
                        .addArgPlayer(ModeTabPlayers.ADVANCED)
                        .addArg("true", "false"),
                "Este comando congelas a un jugador por actividad sospechosa"
        );
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sendMessage(sender, this.getUsage().toString(), MessagesType.ERROR);
            return;
        }
        if (args.length == 1) {
            sendMessage(sender, Message.COMMAND_FREEZE_MISSING_ARGS_LAST, MessagesType.ERROR);
            return;
        }
        CommandUtils.executeForPlayer(sender, args[0], true, dataTemporalPlayer -> {
            ModerationPlayer moderationPlayer = AviaTerraPlayer.getPlayer(dataTemporalPlayer.player()).getModerationPlayer();
            Player player = dataTemporalPlayer.player();
            switch (args[1].toLowerCase()){
                case "true" -> {
                    if (moderationPlayer.isFreeze()) {
                        sendMessage(sender, Message.COMMAND_FREEZE_ALREADY_FREEZE, MessagesType.ERROR);
                    }else {
                        moderationPlayer.setFreeze(true);
                        player.closeInventory();
                        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Integer.MAX_VALUE, 1));
                        sendMessage(player, Message.COMMAND_FREEZE_FREEZE_TARGET, MessagesType.INFO);
                        sendMessage(sender, Message.COMMAND_FREEZE_FREEZE_AUTHOR, MessagesType.SUCCESS);
                    }

                }
                case "false" -> {
                    if (moderationPlayer.isFreeze()) {
                        moderationPlayer.setFreeze(false);
                        player.removePotionEffect(PotionEffectType.BLINDNESS);
                        sendMessage(sender, Message.COMMAND_FREEZE_UNFREEZE_AUTHOR, MessagesType.SUCCESS);
                        sendMessage(player, Message.COMMAND_FREEZE_UNFREEZE_TARGET, MessagesType.INFO);
                    }else{
                        sendMessage(sender, Message.COMMAND_FREEZE_ALREADY_UNFREEZE, MessagesType.ERROR);
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
