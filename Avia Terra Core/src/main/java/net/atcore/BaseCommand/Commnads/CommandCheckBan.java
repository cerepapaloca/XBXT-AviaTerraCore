package net.atcore.BaseCommand.Commnads;

import net.atcore.BaseCommand.BaseTabCommand;
import net.atcore.Messages.TypeMessages;
import net.atcore.Moderation.Ban.BanManager;
import net.atcore.Moderation.Ban.ContextBan;
import net.atcore.Moderation.Ban.DataBan;
import net.atcore.Utils.GlobalUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

import static net.atcore.Messages.MessagesManager.sendMessage;

public class CommandCheckBan extends BaseTabCommand {

    public CommandCheckBan() {
        super("checkban",
                "/CheckBan <jugador> <Contexto>",
                "aviaterra.command.checkban",
                true,
                "Compruebas que el jugador este baneado del contexto seleccionado"
        );
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 1) {
            sendMessage(sender, "falta el contexto", TypeMessages.ERROR);
            return;
        }
        if (args.length >= 2) {
            Player player = Bukkit.getPlayer(args[0]);
            if (player != null) {
                ContextBan contextBan;
                try {
                    contextBan = ContextBan.valueOf(args[1].toUpperCase());
                }catch (Exception ignored) {
                    sendMessage(sender, "contexto no valido", TypeMessages.ERROR);
                    return;
                }
                if (BanManager.checkBan(player, contextBan) == null){
                    sendMessage(sender, "el jugador no esta banedo de ningún contexto", TypeMessages.SUCCESS);;
                }else if (BanManager.checkBan(player, contextBan).isEmpty()){
                    sendMessage(sender, "el jugador esta baneado pero no del contexto seleccionado pero esta baneado de:", TypeMessages.SUCCESS);
                    for (DataBan dataBan : BanManager.getDataBan(player.getName())){
                        sendMessage(sender, "&f-|> Esta baneado de " + dataBan.getContext() + " y expira " + GlobalUtils.TimeToString(dataBan.getUnbanDate(), 1), TypeMessages.INFO);
                    }
                }else {
                    sendMessage(sender, "El jugador <|" + player.getName() + "|> fue echo del contexto <| " + contextBan +
                            "|> seleccionado", TypeMessages.SUCCESS);
                }
            }else {
                sendMessage(sender, "el jugador no existe o esta desconectado", TypeMessages.ERROR);
            }
        }
    }

    @Override
    public List<String> onTab(CommandSender sender, String[] args) {
        if (args.length == 2) {
            return GlobalUtils.listTab(args[1], GlobalUtils.EnumsToStrings(ContextBan.values()));
        }
        return List.of("");
    }
}
