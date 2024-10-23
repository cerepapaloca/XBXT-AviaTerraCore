package net.atcore.BaseCommand.Commnads;

import net.atcore.AviaTerraCore;
import net.atcore.BaseCommand.BaseCommand;
import net.atcore.Messages.TypeMessages;
import net.atcore.Moderation.Ban.ContextBan;
import net.atcore.Moderation.ModerationSection;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static net.atcore.Messages.MessagesManager.sendMessage;

public class CommandUnban extends BaseCommand {

    public CommandUnban() {
        super("unban",
                "/unban <Jugador>",
                "aviaterra.command.unban",
                true,
                "desbanea a al jugador que le caes bien"
        );
    }

    @Override
    public void execute(CommandSender sender, String[] args) {//
        ContextBan contextBan;
        try {
            contextBan = ContextBan.valueOf(args[1].toUpperCase());
        }catch (Exception ignored) {
            sendMessage(sender, "contexto no valido", TypeMessages.ERROR);
            return;
        }
        //en un hilo aparte por qué explota el servidor
        Bukkit.getScheduler().runTaskAsynchronously(AviaTerraCore.getInstance(), () -> ModerationSection.getBanManager().removeBanPlayer(args[0], contextBan, sender.getName()));
        sendMessage(sender, "El jugador fue desbaneado", TypeMessages.SUCCESS);
    }
}
