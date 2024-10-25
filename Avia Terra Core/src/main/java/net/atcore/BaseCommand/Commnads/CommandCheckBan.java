package net.atcore.BaseCommand.Commnads;

import net.atcore.BaseCommand.BaseTabCommand;
import net.atcore.BaseCommand.CommandUtils;
import net.atcore.Messages.TypeMessages;
import net.atcore.Moderation.Ban.IsBan;
import net.atcore.Moderation.Ban.ManagerBan;
import net.atcore.Moderation.Ban.ContextBan;
import net.atcore.Moderation.Ban.DataBan;
import net.atcore.Utils.GlobalConstantes;
import net.atcore.Utils.GlobalUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

import static net.atcore.Messages.MessagesManager.sendMessage;

public class CommandCheckBan extends BaseTabCommand {

    public CommandCheckBan() {
        super("checkban",
                "/CheckBan <jugador> <! | ?> <Contexto>",
                true,
                "Compruebas que el jugador este baneado usando ? o !. con ? solo miras que contexto esta baneado y no requiere especificar " +
                        "el contexto mientras el ! se usa cuando quieres echar un jugador baneado de un contexto especificado (esto por si se llega a " +
                        "colar en un modo de juego o si hay bug)"
        );
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 1) {
            sendMessage(sender, "falta el contexto", TypeMessages.ERROR);
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
                if (ManagerBan.getDataBan(args[0]) == null) {
                    sendMessage(sender, "No esta baneado", TypeMessages.INFO);
                    return;
                }
                for (DataBan dataBan : ManagerBan.getDataBan(args[0])){
                    sendDataBan(sender, dataBan);
                }
            }else {
                if (args.length >= 3) {
                    ContextBan contextBan;

                    try {
                        contextBan = ContextBan.valueOf(args[2].toUpperCase());
                    }catch (Exception ignored) {
                        sendMessage(sender, "contexto no valido", TypeMessages.ERROR);
                        return;
                    }

                    Player player = Bukkit.getPlayer(args[0]);
                    if (player != null) {
                        IsBan reason = ManagerBan.checkBan(player, contextBan);
                        switch (reason) {
                            case NOT -> sendMessage(sender, "el jugador no esta banedo de ningún contexto", TypeMessages.SUCCESS);
                            case NOT_THIS_CONTEXT -> {
                                sendMessage(sender, "el jugador esta baneado pero no del contexto seleccionado pero esta baneado de:", TypeMessages.SUCCESS);
                                if (ManagerBan.getDataBan(player.getName()) == null) {
                                    sendMessage(sender, "No esta baneado", TypeMessages.INFO);
                                    return;
                                }
                                for (DataBan dataBan : ManagerBan.getDataBan(player.getName())){
                                    sendDataBan(sender, dataBan);
                                }
                            }
                            case YES -> sendMessage(sender, "El jugador <|" + player.getName() + "|> fue echo del contexto <| " + contextBan +
                                    "|> seleccionado", TypeMessages.SUCCESS);
                            case UNKNOWN -> sendMessage(sender, "Hubo un problema al encontrar la información del jugador <|" + player.getName()
                                    , TypeMessages.ERROR);

                        }
                    }else {
                        sendMessage(sender, "el jugador no existe o esta desconectado", TypeMessages.ERROR);
                    }
                }else {
                    sendMessage(sender, "Tiene que incluir el contexto", TypeMessages.ERROR);
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
        sendMessage(sender, "&f-|!> Esta baneado de <|" + dataBan.getContext() + "|> y expira <|" + time + "|> la razón <|" + dataBan.getReason() + "|>", TypeMessages.INFO);
    }

    @Override
    public List<String> onTab(CommandSender sender, String[] args) {
        switch (args.length) {
            case 1 -> {
                return null;
            }
            case 2 -> {
                return CommandUtils.listTab(args[1], new String[]{"?","!"});
            }
            case 3 -> {
                return CommandUtils.listTab(args[2], GlobalUtils.EnumsToStrings(ContextBan.values()));
            }
        }
        return List.of("");
    }
}
