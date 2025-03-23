package net.atcore.command.commnads;

import net.atcore.aviaterraplayer.AviaTerraPlayer;
import net.atcore.command.*;
import net.atcore.messages.Message;
import net.atcore.messages.MessagesManager;
import net.atcore.utils.GlobalUtils;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public class TpaCommand extends BaseTabCommand implements CommandAliase {

    public TpaCommand() {
        super("tpa",
                new ArgumentUse("tpa").addArgPlayer(ModeTabPlayers.NORMAL),
                CommandVisibility.PUBLIC,
                "Puedes enviás una solicitud de tp a un jugador"
        );
    }

    @Override
    public void execute(CommandSender sender, String... args) {
        if (sender instanceof Player p) {
            if (args.length > 0) {
                switch (args[0].toUpperCase()) {
                    case "Y" -> {
                        AviaTerraPlayer atp = AviaTerraPlayer.getPlayer(p);
                        if (atp.getListTpa().isEmpty()){
                            atp.sendMessage(Message.COMMAND_TPA_NO_FOUND);
                            return;
                        }
                        TpaRequest request = atp.getListTpa().getFirst();
                        Player player = Bukkit.getPlayer(request.uuid);
                        if (request.dateCreated < System.currentTimeMillis()) {
                            MessagesManager.sendMessage(sender, Message.COMMAND_TPA_EXPIRE);
                            atp.getListTpa().clear();
                            return;
                        }
                        if (player != null) {
                            player.getWorld().spawnParticle(Particle.PORTAL, player.getLocation(), 10, 0.5, 1,0.5);
                            player.teleport(p, PlayerTeleportEvent.TeleportCause.COMMAND);
                            player.getWorld().playSound(player, Sound.ENTITY_PLAYER_TELEPORT, SoundCategory.PLAYERS, 1, 1, -1);
                            player.getWorld().spawnParticle(Particle.REVERSE_PORTAL, player.getLocation(), 10, 0.5, 1,0.5);
                        }else {
                            MessagesManager.sendMessage(sender, Message.COMMAND_TPA_WAS_DISCONNECTED);
                        }
                        atp.getListTpa().clear();
                    }
                    case "N" -> {
                        AviaTerraPlayer atp = AviaTerraPlayer.getPlayer(p);
                        if (atp.getListTpa().isEmpty()){
                            atp.sendMessage(Message.COMMAND_TPA_NO_FOUND);
                            return;
                        }
                        TpaRequest request = atp.getListTpa().getFirst();
                        Player player = Bukkit.getPlayer(request.uuid);
                        if (player != null) {
                            MessagesManager.sendMessage(player, Message.COMMAND_TPA_CANCEL_RECEIVE);
                        }
                        MessagesManager.sendMessage(sender, Message.COMMAND_TPA_CANCEL_SELF);
                    }
                    default -> {
                        Player player = Bukkit.getPlayer(args[0]);
                        if (p.getName().equals(args[0])) {
                            MessagesManager.sendMessage(sender, Message.COMMAND_TPA_NO_FOUND);
                            return;
                        }
                        if (player != null) {
                            AviaTerraPlayer atp = AviaTerraPlayer.getPlayer(player);
                            MessagesManager.sendFormatMessage(sender, Message.COMMAND_TPA_SEND, player.getName());
                            if (atp.getPlayersBLock().contains(p.getName())) return;
                            MessagesManager.sendFormatMessage(atp.getPlayer(), Message.COMMAND_TPA_RECEIVE, p.getName());
                            atp.getListTpa().add(new TpaRequest(p.getUniqueId(), System.currentTimeMillis() + 1000*60*5));
                        }else {
                            MessagesManager.sendMessage(sender, Message.COMMAND_GENERIC_PLAYER_NOT_FOUND);
                        }
                    }
                }
            }
        }else {
            MessagesManager.sendMessage(sender, Message.COMMAND_GENERIC_NO_PLAYER);
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

    @Override
    public List<String> getCommandsAliases() {
        return List.of("tpaYes", "tpaNo");
    }

    @Override
    public List<BiConsumer<CommandSender, String[]>> getExecuteAliase() {
        var aliases = new ArrayList<BiConsumer<CommandSender, String[]>>();
        aliases.add((sender, args) -> execute(sender, "y"));
        aliases.add((sender, args) -> execute(sender, "n"));
        return aliases;
    }

    @Override
    public List<BiFunction<CommandSender, String[], List<String>>> getTabAliase() {
        var aliases = new ArrayList<BiFunction<CommandSender, String[], List<String>>>();
        aliases.add(((sender, strings) -> List.of()));
        aliases.add(((sender, strings) -> List.of()));
        return aliases;
    }

    public record TpaRequest(UUID uuid, long dateCreated){}
}
