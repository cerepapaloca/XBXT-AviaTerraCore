package net.atcore.command.commnads;

import net.atcore.command.BaseTabCommand;
import net.atcore.command.CommandUtils;
import net.atcore.command.ArgumentUse;
import net.atcore.messages.Message;
import net.atcore.messages.TypeMessages;
import net.atcore.moderation.ban.IsBan;
import net.atcore.moderation.ban.BanManager;
import net.atcore.moderation.ban.ContextBan;
import net.atcore.moderation.ban.DataBan;
import net.atcore.utils.GlobalConstantes;
import net.atcore.utils.GlobalUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

import static net.atcore.messages.MessagesManager.sendFormatMessage;
import static net.atcore.messages.MessagesManager.sendMessage;

public class CheckBanCommand extends BaseTabCommand {

    public CheckBanCommand() {
        super("checkban",
                new ArgumentUse("checkBan").addArg("?","!").addArg("Contexto"),
                "Compruebas que el jugador este baneado usando ? o !. con ? solo miras que contexto esta baneado y no requiere especificar " +
                        "el contexto mientras el ! se usa cuando quieres echar un jugador baneado de un contexto especificado (esto por si se llega a " +
                        "colar en un modo de juego o si hay bug)",
                false);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 1) {
            sendMessage(sender, Message.COMMAND_CHECK_BAN_MISSING_ARGUMENT_CONTEXT);
            return;
        }
        if (args.length >= 2) {
            boolean isChecking;
            switch (args[1]){
                case "?" -> isChecking = true;
                case "!" -> isChecking = false;
                default -> {
                    return;
                }
            }

            if (isChecking) {
                if (BanManager.getDataBan(args[0]) == null) {
                    sendMessage(sender, Message.COMMAND_CHECK_BAN_NOT_FOUND_BAN);
                    return;
                }
                for (DataBan dataBan : BanManager.getDataBan(args[0]).values()) {
                    sendDataBan(sender, dataBan);
                }
            }else {
                if (args.length >= 3) {
                    ContextBan contextBan;

                    try {
                        contextBan = ContextBan.valueOf(args[2].toUpperCase());
                    }catch (Exception ignored) {
                        sendMessage(sender, Message.COMMAND_CHECK_BAN_NOT_FOUND_CONTEXT);
                        return;
                    }

                    Player player = Bukkit.getPlayer(args[0]);
                    if (player != null) {
                        IsBan reason = BanManager.checkBan(player, contextBan);
                        switch (reason) {
                            case NOT -> sendMessage(sender, Message.COMMAND_CHECK_BAN_NOT_FOUND_BAN_IN_CONTEXT);
                            case NOT_THIS_CONTEXT -> {
                                sendMessage(sender, Message.COMMAND_CHECK_BAN_FOUND_AND_KICK);
                                if (BanManager.getDataBan(player.getName()) == null) {
                                    sendMessage(sender, Message.COMMAND_CHECK_BAN_NOT_FOUND_BAN);
                                    return;
                                }
                                for (DataBan dataBan : BanManager.getDataBan(player.getName()).values()){
                                    sendDataBan(sender, dataBan);
                                }
                            }
                            case YES -> sendFormatMessage(sender, Message.COMMAND_CHECK_BAN_FOUND_AND_KICK, player.getName(), contextBan);
                            case UNKNOWN -> sendFormatMessage(sender, Message.COMMAND_CHECK_BAN_ERROR, player.getName());

                        }
                    }else {
                        sendMessage(sender, Message.COMMAND_GENERIC_PLAYER_NOT_FOUND);
                    }
                }else {
                    sendMessage(sender, Message.COMMAND_CHECK_BAN_MISSING_ARGUMENT_CONTEXT);
                }
            }
        }
    }

    private void sendDataBan(CommandSender sender, DataBan dataBan) {
        String time;
        if (dataBan.getUnbanDate() == GlobalConstantes.NUMERO_PERMA){
            time = "Perma";
        }else if (dataBan.getUnbanDate() < System.currentTimeMillis()){
            time = "Ya expiro";
        }else {
            time = GlobalUtils.timeToString(dataBan.getUnbanDate(), 1, true);
        }
        sendFormatMessage(sender, Message.COMMAND_CHECK_BAN_FOUND, dataBan.getContext(), time, dataBan.getReason());
    }

    @Override
    public List<String> onTab(CommandSender sender, String[] args) {
        switch (args.length) {
            case 1 -> {
                return null;
            }
            case 2 -> {
                return CommandUtils.listTab(args[1], "?","!");
            }
            case 3 -> {
                if (args[1].equals("!")){
                    return CommandUtils.listTab(args[2], CommandUtils.enumsToStrings(ContextBan.values()));
                }else {
                    return List.of("");
                }
            }
        }
        return List.of("");
    }
}
