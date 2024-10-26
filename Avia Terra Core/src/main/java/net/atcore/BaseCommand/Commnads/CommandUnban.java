package net.atcore.BaseCommand.Commnads;

import net.atcore.AviaTerraCore;
import net.atcore.BaseCommand.BaseCommand;
import net.atcore.BaseCommand.BaseTabCommand;
import net.atcore.BaseCommand.CommandUtils;
import net.atcore.Messages.TypeMessages;
import net.atcore.Moderation.Ban.ContextBan;
import net.atcore.Moderation.ModerationSection;
import net.atcore.Utils.GlobalUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

import static net.atcore.Messages.MessagesManager.sendMessage;

public class CommandUnban extends BaseTabCommand {

    public CommandUnban() {
        super("unban",
                "/unban <Jugador> <Contexto>",
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
        sendMessage(sender, "El jugador va ser desbaneado mira la los logs para confirmar", TypeMessages.INFO);
    }

    @Override
    public List<String> onTab(CommandSender sender, String[] args) {
        if (args.length == 2){
            return CommandUtils.listTab(args[1], CommandUtils.EnumsToStrings(ContextBan.values()));
        }
        return null;
    }
}
