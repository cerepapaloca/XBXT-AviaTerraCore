package net.atcore.command.Commnads;

import net.atcore.AviaTerraPlayer;
import net.atcore.command.BaseTabCommand;
import net.atcore.command.CommandUtils;
import net.atcore.messages.TypeMessages;
import org.apache.commons.collections4.BagUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

import static net.atcore.messages.MessagesManager.sendMessage;

public class FreezeCommand extends BaseTabCommand {
    public FreezeCommand() {
        super("freeze",
                "/freeze <player> <on | off>",
                true,
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
            sendMessage(sender, "tienes que poner true o false", TypeMessages.ERROR);
            return;
        }
        CommandUtils.excuteForPlayer(sender, args[0], true, dataTemporalPlayer -> {
            AviaTerraPlayer aviaTerraPlayer = AviaTerraPlayer.getPlayer(dataTemporalPlayer.player());
            Player player = dataTemporalPlayer.player();
            switch (args[1].toLowerCase()){
                case "true" -> {
                    if (aviaTerraPlayer.isFreeze()) {
                        sendMessage(sender, "El jugador ya esta congelado", TypeMessages.ERROR);
                    }else {
                        aviaTerraPlayer.setFreeze(true);
                        player.closeInventory();
                        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Integer.MAX_VALUE, 1));
                        sendMessage(player, "Te an congelado, por favor habla con el staff", TypeMessages.INFO);
                        sendMessage(sender, "El jugador ya fue congelado", TypeMessages.SUCCESS);
                    }

                }
                case "false" -> {
                    if (aviaTerraPlayer.isFreeze()) {
                        aviaTerraPlayer.setFreeze(false);
                        player.removePotionEffect(PotionEffectType.BLINDNESS);
                        sendMessage(sender, "El jugador ya fue descongelado", TypeMessages.SUCCESS);
                        sendMessage(player, "Te descongelado", TypeMessages.INFO);
                    }else{
                        sendMessage(sender, "Ese jugador no esta congelado", TypeMessages.ERROR);
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
