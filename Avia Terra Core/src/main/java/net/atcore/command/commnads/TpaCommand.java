package net.atcore.command.commnads;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.atcore.aviaterraplayer.AviaTerraPlayer;
import net.atcore.command.ArgumentUse;
import net.atcore.command.BaseTabCommand;
import net.atcore.command.ModeTabPlayers;
import net.atcore.messages.Message;
import net.atcore.messages.MessagesManager;
import net.atcore.messages.MessagesType;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TpaCommand extends BaseTabCommand {

    public TpaCommand() {
        super("tpa",
                new ArgumentUse("tpa").addArgPlayer(ModeTabPlayers.NORMAL),
                "*",
                "Puedes enviar una solicitud de tp a un jugador"
        );
    }

    @Override
    public void execute(CommandSender sender, String[] args) throws Exception {
        if (sender instanceof Player p) {
            if (args.length > 0) {
                switch (args[0].toUpperCase()) {
                    case "Y" -> {
                        AviaTerraPlayer atp = AviaTerraPlayer.getPlayer(p);
                        if (atp.getListTpa().isEmpty()){
                            atp.sendMessage(Message.COMMAND_TPA_NO_FOUND.getMessage(), MessagesType.ERROR);
                            return;
                        }
                        TpaRequest request = atp.getListTpa().getFirst();
                        Player player = Bukkit.getPlayer(request.uuid);
                        if (request.dateCreated < System.currentTimeMillis()) {
                            MessagesManager.sendMessage(sender, Message.COMMAND_TPA_EXPIRE, MessagesType.ERROR);
                            atp.getListTpa().clear();
                            return;
                        }
                        if (player != null) {
                            player.teleport(p, PlayerTeleportEvent.TeleportCause.COMMAND);
                            player.getWorld().playSound(player, Sound.ENTITY_PLAYER_TELEPORT, SoundCategory.PLAYERS, 1, 1);
                        }else {
                            MessagesManager.sendMessage(sender, Message.COMMAND_TPA_WAS_DISCONNECTED, MessagesType.ERROR);
                        }
                        atp.getListTpa().clear();
                    }
                    case "N" -> {
                        AviaTerraPlayer atp = AviaTerraPlayer.getPlayer(p);
                        atp.getListTpa().clear();
                    }
                    default -> {
                        Player player = Bukkit.getPlayer(args[0]);
                        if (p.getName().equals(args[0])) {
                            MessagesManager.sendMessage(sender, Message.COMMAND_TPA_NO_FOUND, MessagesType.ERROR);
                            return;
                        }
                        if (player != null) {
                            AviaTerraPlayer atp = AviaTerraPlayer.getPlayer(player);
                            MessagesManager.sendMessage(sender, String.format(Message.COMMAND_TPA_SEND.getMessage(), p.displayName()), MessagesType.SUCCESS);
                            atp.sendMessage(String.format(Message.COMMAND_TPA_RECEIVE.getMessage(), p.displayName()), MessagesType.INFO);
                            atp.getListTpa().add(new TpaRequest(p.getUniqueId(), System.currentTimeMillis() + 1000*60*5));
                        }else {
                            MessagesManager.sendMessage(sender, Message.COMMAND_GENERIC_PLAYER_NOT_FOUND, MessagesType.ERROR);
                        }
                    }
                }
            }
        }else {
            MessagesManager.sendMessage(sender, Message.COMMAND_GENERIC_NO_PLAYER, MessagesType.ERROR);
        }
    }

    @Override
    public List<String> onTab(CommandSender sender, String[] args) {
        if (args.length == 1) {
            List<String> names = new ArrayList<>();
            names.add("Y");
            names.add("N");
            for (Player p : Bukkit.getOnlinePlayers()) names.add(p.getName());
            return names;
        }
        return List.of();
    }
    public record TpaRequest(UUID uuid, long dateCreated){}
}
