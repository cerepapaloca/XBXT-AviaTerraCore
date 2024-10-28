package net.atcore.command.Commnads;

import net.atcore.AviaTerraCore;
import net.atcore.command.BaseTabCommand;
import net.atcore.command.CommandUtils;
import net.atcore.messages.TypeMessages;
import net.atcore.moderation.Ban.ContextBan;
import net.atcore.moderation.ModerationSection;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.List;

import static net.atcore.messages.MessagesManager.sendMessage;

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
