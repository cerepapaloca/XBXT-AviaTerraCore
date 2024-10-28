package net.atcore.command.Commnads;

import net.atcore.command.BaseTabCommand;
import net.atcore.command.CommandUtils;
import net.atcore.messages.TypeMessages;
import net.atcore.moderation.Ban.ContextBan;
import net.atcore.moderation.ModerationSection;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

import static net.atcore.messages.MessagesManager.*;

public class CommandBan extends BaseTabCommand {

    public CommandBan() {
        super("ban",
                "/ban <jugador> <Contexto> <Tiempo> <Ranzón...",
                true,
                "Baneas a los jugadores que te miran feo"
        );
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        switch (args.length) {
            case 1 -> sendMessage(sender, this.getUsage(), TypeMessages.ERROR);
            case 2 -> sendMessage(sender, "Tiene que por un tiempo de baneo", TypeMessages.ERROR);
            case 3 -> sendMessage(sender, "Tienes dar una razón de baneo", TypeMessages.ERROR);
            default -> {
                ContextBan contextBan;
                try {
                    contextBan = ContextBan.valueOf(args[1].toUpperCase());
                }catch (Exception ignored) {
                    sendMessage(sender, "contexto no valido", TypeMessages.ERROR);
                    return;
                }
                long time;
                try {
                    time = CommandUtils.StringToMilliseconds(args[2], true);
                }catch (RuntimeException e){
                    sendMessage(sender, "formato de fecha incorrecto", TypeMessages.ERROR);
                    return;
                }

                String reason = "";
                for (int i = 3; i < args.length; i++){
                    reason = reason.concat(args[i] + " ");
                }
                String finalReason = reason;
                Player player = Bukkit.getPlayer(args[0]);
                try {
                    if (player != null) {
                        ModerationSection.getBanManager().banPlayer(player, finalReason, time, contextBan, sender.getName());
                    }else {
                        ModerationSection.getBanManager().banPlayer(args[0], null, null, finalReason, time, contextBan, sender.getName());
                    }
                }catch (Exception ignored) {
                    sendMessage(sender, "Hubo un problema con las base de datos vuelva a ejecutar el comando", TypeMessages.ERROR);
                }

                sendMessage(sender, "El jugador sera baneado mira los logs para confirmar", TypeMessages.INFO);

            }
        }
    }

    @Override
    public List<String> onTab(CommandSender sender, String[] args) {
        switch (args.length) {
            case 2 -> {
                return CommandUtils.listTab(args[1], CommandUtils.EnumsToStrings(ContextBan.values()));
            }
            case 3 -> {
                return CommandUtils.listTabTime(args[2], true);
            }
            case 4 -> {
                return List.of("razón del baneo...");
            }
        }
        return null;
    }
}
