package net.atcore.BaseCommand.Commnads;

import net.atcore.BaseCommand.BaseTabCommand;
import net.atcore.BaseCommand.CommandUtils;
import net.atcore.Messages.TypeMessages;
import net.atcore.Moderation.Freeze;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import static net.atcore.Messages.MessagesManager.sendMessage;

public class CommandFreeze extends BaseTabCommand {
    public CommandFreeze() {
        super("freeze",
                "/freeze <player> <on | off>",
                "aviaterra.command.freeze",
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
        Player player = Bukkit.getPlayer(args[0]);
        if (args.length == 1) {
            sendMessage(sender, "tienes que poner on o off", TypeMessages.ERROR);
            return;
        }
        if (player != null) {
            HashSet<UUID> listFreeze = Freeze.getPlayerFreeze();
            if (sender instanceof Player p) {
                if (p == player) {
                    sendMessage(sender, "No puedes congelarte a tí mismo", TypeMessages.ERROR);
                    return;
                }
            }
            switch (args[1].toLowerCase()){
                case "on" -> {
                    if (listFreeze.contains(player.getUniqueId())) {
                        sendMessage(sender, "El jugador ya esta congelado", TypeMessages.ERROR);
                    }else {
                        Freeze.getPlayerFreeze().add(player.getUniqueId());
                        player.closeInventory();
                        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Integer.MAX_VALUE, 1));
                        sendMessage(player, "Te an congelado, por favor habla con el staff", TypeMessages.INFO);
                        sendMessage(sender, "El jugador ya fue congelado", TypeMessages.SUCCESS);
                    }

                }
                case "off" -> {
                    if (listFreeze.contains(player.getUniqueId())) {
                        Freeze.getPlayerFreeze().remove(player.getUniqueId());
                        player.removePotionEffect(PotionEffectType.BLINDNESS);
                        sendMessage(sender, "El jugador ya fue descongelado", TypeMessages.SUCCESS);
                        sendMessage(player, "Te descongelado", TypeMessages.INFO);
                    }else{
                        sendMessage(sender, "Ese jugador no esta congelado", TypeMessages.ERROR);
                    }
                }
            }
        }else {
            sendMessage(sender, "El jugador No existe o no esta conectado", TypeMessages.ERROR);
        }
    }

    @Override
    public List<String> onTab(CommandSender sender, String[] args) {
        if (args.length == 2) {
            return CommandUtils.listTab(args[1], new String[]{"on", "off"});
        }
        return null;
    }
}
