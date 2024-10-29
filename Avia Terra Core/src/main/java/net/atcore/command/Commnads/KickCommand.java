package net.atcore.command.Commnads;

import net.atcore.command.BaseTabCommand;
import net.atcore.messages.TypeMessages;
import net.atcore.utils.GlobalUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

import static net.atcore.messages.MessagesManager.sendMessage;

public class KickCommand extends BaseTabCommand {

    public KickCommand() {
        super("kick",
                "/kick <player> <razón>",
                true,
                "echas al jugador del servidor por actividad sospechosa"
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
                    for (int i = 2; i < args.length; i++){
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
