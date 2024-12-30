package net.atcore.command.commnads;

import net.atcore.command.BaseTabCommand;
import net.atcore.command.CommandUtils;
import net.atcore.command.ModeTabPlayers;
import net.atcore.command.UseArgs;
import net.atcore.messages.Message;
import net.atcore.messages.TypeMessages;
import net.atcore.moderation.ban.ContextBan;
import net.atcore.moderation.ModerationSection;
import net.atcore.utils.GlobalUtils;
import org.bukkit.command.CommandSender;

import java.util.List;

import static net.atcore.messages.MessagesManager.*;

public class BanCommand extends BaseTabCommand {

    public BanCommand() {
        super("ban",
                new UseArgs("jugador").addArg("Contexto").addTime(true).addNote("Ranzón"),
                "Baneas a los jugador de un contexto por un tiempo determinado"
        );
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        switch (args.length) {
            case 1 -> sendMessage(sender, this.getUsage().toString(), TypeMessages.ERROR);
            case 2 -> sendMessage(sender, Message.COMMAND_BAN_MISSING_ARGUMENT_TIME, TypeMessages.ERROR);
            case 3 -> sendMessage(sender, Message.COMMAND_BAN_MISSING_ARGUMENT_REASON, TypeMessages.ERROR);
            default -> {
                ContextBan contextBan;
                try {
                    contextBan = ContextBan.valueOf(args[1].toUpperCase());
                }catch (Exception ignored) {
                    sendMessage(sender, Message.COMMAND_BAN_NOT_FOUND_CONTEXT, TypeMessages.ERROR);
                    return;
                }
                long time;
                try {
                    time = CommandUtils.StringToMilliseconds(args[2], true);
                }catch (RuntimeException e){
                    sendMessage(sender, Message.COMMAND_GENERIC_FORMAT_DATE_ERROR, TypeMessages.ERROR);
                    return;
                }

                String reason = "";
                for (int i = 3; i < args.length; i++){
                    reason = reason.concat(args[i] + " ");
                }
                String finalReason = reason;
                CommandUtils.executeForPlayer(sender, args[0], false, player1 -> {
                    try {
                        if (player1.player() != null) {
                            ModerationSection.getBanManager().banPlayer(player1.player(), finalReason, time, contextBan, sender.getName());
                        }else {
                            ModerationSection.getBanManager().banPlayer(player1.name(), GlobalUtils.getUUIDByName(player1.name()), null, finalReason, time, contextBan, sender.getName());
                        }
                    }catch (Exception ignored) {
                        sendMessage(sender, Message.COMMAND_BAN_DATA_BASE_ERROR, TypeMessages.ERROR);
                    }
                });

                sendMessage(sender, Message.COMMAND_BAN_SUCCESSFUL, TypeMessages.INFO);
            }
        }
    }

    @Override
    public List<String> onTab(CommandSender sender, String[] args) {
        switch (args.length) {
            case 1 -> {
                return CommandUtils.tabForPlayer(args[0]);
            }
            case 2 -> {
                return CommandUtils.listTab(args[1], CommandUtils.enumsToStrings(ContextBan.values()));
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
