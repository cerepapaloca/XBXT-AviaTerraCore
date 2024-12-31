package net.atcore.command.commnads;

import net.atcore.command.BaseTabCommand;
import net.atcore.command.CommandUtils;
import net.atcore.command.ModeTabPlayers;
import net.atcore.command.ArgumentUse;
import net.atcore.messages.CategoryMessages;
import net.atcore.messages.MessagesType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

import static net.atcore.messages.MessagesManager.sendMessage;

public class TellCommand extends BaseTabCommand {

    public TellCommand() {
        super("tell",
                new ArgumentUse("tell")
                        .addArgPlayer(ModeTabPlayers.ADVANCED)
                        .addArg("Mensaje"),
                "*",
                "Le envías un mensaje privado a un jugador o un grupo de estos"
        );
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        switch (args.length) {
            case 0 -> sendMessage(sender, "Tienes que poner el nombre del jugador", MessagesType.ERROR);
            case 1 -> sendMessage(sender, "Te falta el mensaje", MessagesType.ERROR);
            default -> {
                String message = "";
                for (int i = 1; i < args.length; i++){
                    message = message.concat(args[i] + " ");
                }
                String finalMessage = message;
                sendMessage(sender, "&ole haz susurrado a " + args[0], null);
                CommandUtils.executeForPlayer(sender, args[0], true, dataTemporalPlayer -> {
                    Player player = dataTemporalPlayer.player();
                    sendMessage(player,"&o" + sender.getName() + " -> " + finalMessage, null, CategoryMessages.PRIVATE, false);
                });
            }
        }
    }

    @Override
    public List<String> onTab(CommandSender sender, String[] args) {
        switch (args.length){
            case 1 -> {
                return CommandUtils.tabForPlayer(args[0]);
            }
            case 2 -> {
                return List.of("Mensaje...");
            }
            default -> {
                return List.of("");
            }
        }
    }
}
