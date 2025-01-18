package net.atcore.command.commnads;

import net.atcore.AviaTerraCore;
import net.atcore.aviaterraplayer.AviaTerraPlayer;
import net.atcore.command.ArgumentUse;
import net.atcore.command.BaseTabCommand;
import net.atcore.command.CommandUtils;
import net.atcore.data.DataSection;
import net.atcore.data.FileYaml;
import net.atcore.messages.Message;
import net.atcore.messages.MessagesManager;
import net.atcore.messages.MessagesType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.ArrayList;
import java.util.List;

public class HomeCommand extends BaseTabCommand {

    public HomeCommand() {
        super("home",
                new ArgumentUse("home").addNote("Nombre de tu Home").addArgOptional().addArg("add", "remove"),
                "*",
                "Crear un home donde te puedes hacer tp"
        );
    }

    private final Location spawnLocation = new Location(Bukkit.getWorlds().getFirst(), 0, 0, 0);

    @Override
    public void execute(CommandSender sender, String[] args) throws Exception {
        if (sender instanceof Player player){
            AviaTerraPlayer atp = AviaTerraPlayer.getPlayer(player);
            if (args.length == 0) {
                MessagesManager.sendMessage(sender, this.getUsage().toString(), MessagesType.ERROR);
                return;
            }
            switch (args.length) {
                case 1 -> {
                    if (atp.getHomes().containsKey(args[0])) {
                        Location loc = atp.getHomes().get(args[0]);
                        Location l = spawnLocation.clone();
                        l.setWorld(player.getWorld());
                        double distance = player.getLocation().distance(l);
                        // Esto quiere decir si está en el nether
                        if (player.getWorld().isUltraWarm()) distance *= 8;
                        int distanceMinTp = 100;
                        if (distance > distanceMinTp){
                            player.getWorld().playSound(player, Sound.ENTITY_PLAYER_TELEPORT, SoundCategory.PLAYERS, 1, 1);
                            player.teleport(loc, PlayerTeleportEvent.TeleportCause.COMMAND);
                        }else {
                            MessagesManager.sendMessage(sender, String.format(Message.COMMAND_HOME_CLOSE_SPAWN.toString(), Math.round(distanceMinTp - distance)), MessagesType.ERROR);
                        }

                    }else {
                        MessagesManager.sendMessage(sender, String.format(Message.COMMAND_HOME_NOT_FOUND.toString(), args[0]), MessagesType.ERROR);
                    }
                }
                case 2 -> {
                    FileYaml fileYaml = DataSection.getPlayersDataFiles().getConfigFile(atp.getUuid().toString(), true);
                    switch (args[1].toLowerCase()){
                        case "add" -> AviaTerraCore.enqueueTaskAsynchronously(() -> {
                            if (args[0].contains(".")){
                                MessagesManager.sendMessage(sender, Message.COMMAND_HOME_CONTAINS_POINT, MessagesType.ERROR);
                                return;
                            }
                            if (!Character.isAlphabetic(args[0].charAt(0))) {
                                MessagesManager.sendMessage(sender, Message.COMMAND_HOME_IS_NOT_ALPHABETICAL, MessagesType.ERROR);
                                return;
                            }
                            if (atp.getHomes().size() >= 5) {
                                MessagesManager.sendMessage(sender, Message.COMMAND_HOME_MAX_HOME, MessagesType.ERROR);
                                return;
                            }
                            atp.getHomes().put(args[0], player.getLocation());
                            fileYaml.saveData();
                            MessagesManager.sendMessage(sender, Message.COMMAND_HOME_ADD_SUCCESSFUL, MessagesType.SUCCESS);
                        });
                        case "remove" -> AviaTerraCore.enqueueTaskAsynchronously(() -> {
                            if (atp.getHomes().containsKey(args[0])) {
                                atp.getHomes().remove(args[0]);
                                fileYaml.saveData();
                                MessagesManager.sendMessage(sender, Message.COMMAND_HOME_REMOVE_SUCCESSFUL, MessagesType.SUCCESS);
                            }else {
                                MessagesManager.sendMessage(sender, Message.COMMAND_HOME_NOT_FOUND_REMOVE, MessagesType.ERROR);
                            }
                        });
                    }
                }
            }
        }else {
            MessagesManager.sendMessage(sender, Message.COMMAND_GENERIC_NO_PLAYER, MessagesType.ERROR);
        }
    }

    @Override
    public List<String> onTab(CommandSender sender, String[] args) {
        if (sender instanceof Player player) {
            AviaTerraPlayer atp = AviaTerraPlayer.getPlayer(player);
            switch (args.length) {
                case 1 -> {
                    return CommandUtils.listTab(args[0], new ArrayList<>(atp.getHomes().keySet()));
                }
                case 2 -> {
                    return CommandUtils.listTab(args[1], "add", "remove");
                }
            }
        }
        return new ArrayList<>();
    }
}
