package net.atcore.BaseCommand.Commnads;

import net.atcore.BaseCommand.BaseTabCommand;
import net.atcore.Messages.TypeMessages;
import net.atcore.Utils.GlobalUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

import static net.atcore.Messages.MessagesManager.sendMessage;

public class CommandKick extends BaseTabCommand {

    public CommandKick() {
        super("kick",
                "/kick <player> <razón>",
                true,
                "Este comando congelas a un jugador por actividad sospechosa"
        );
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        switch (args.length){
            case 0, 1 -> sendMessage(sender, this.getUsage(), TypeMessages.ERROR);
            default -> {
                Player player = Bukkit.getPlayer(args[0]);
                if (player != null) {
                    String reason = "";
                    for (int i = 3; i < args.length; i++){
                        reason = reason.concat(args[i] + " ");
                    }

                    GlobalUtils.kickPlayer(player, reason.isEmpty() ? null : reason);
                }else{
                    sendMessage(sender, "El jugador no existe o no esta conectado", TypeMessages.ERROR);
                }
            }
        }
    }

    @Override
    public List<String> onTab(CommandSender sender, String[] args) {
        return List.of();
    }
}
