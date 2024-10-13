package net.atcore.BaseCommand.Commnads;

import net.atcore.BaseCommand.BaseTabCommand;
import net.atcore.Messages.TypeMessages;
import net.atcore.Moderation.Ban.BanManager;
import net.atcore.Moderation.Ban.ContextBan;
import net.atcore.Utils.GlobalUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

import static net.atcore.AviaTerraCore.PLUGIN;
import static net.atcore.Messages.MessagesManager.*;

public class CommandBan extends BaseTabCommand {

    public CommandBan() {
        super("ban",
                "/ban <jugador> <Contexto> <Tiempo> <Ranzón...",
                "aviaterra.command.ban",
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
                if (args[2].equalsIgnoreCase("perma")){
                    time = 0;
                }else{
                    time = GlobalUtils.StringToMilliseconds(args[2]);
                }

                String reason = "";
                for (int i = 3; i < args.length; i++){
                    reason = reason.concat(args[i] + " ");
                }
                String finalReason = reason;
                Player player = Bukkit.getPlayer(args[0]);
                Bukkit.getScheduler().runTaskAsynchronously(PLUGIN, () -> {//hilo aparte por si no Boom!!
                    if (player != null) {
                        BanManager.banPlayer(player, finalReason, time, contextBan, sender.getName());
                    }else {
                        BanManager.banPlayer(args[0], null, null, finalReason, time, contextBan, sender.getName());
                    }
                    sendMessage(sender, "El jugador fue baneado", TypeMessages.SUCCESS);
                });
            }
        }
    }

    @Override
    public List<String> onTab(CommandSender sender, String[] args) {
        switch (args.length) {
            case 2 -> {
                return GlobalUtils.listTab(args[1], GlobalUtils.EnumsToStrings(ContextBan.values()));
            }
            case 3 -> {
                return GlobalUtils.listTabTime(args[2], "perma");
            }
            case 4 -> {
                return List.of("razón del baneo...");
            }
        }
        return null;
    }
}
