package net.atcore.command.commnads;

import net.atcore.AviaTerraCore;
import net.atcore.aviaterraplayer.AviaTerraPlayer;
import net.atcore.command.*;
import net.atcore.data.DataSection;
import net.atcore.data.FileYaml;
import net.atcore.messages.Message;
import net.atcore.messages.MessagesManager;
import net.atcore.messages.TypeMessages;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public class HomeCommandAliase extends BaseTabCommand implements CommandAliase {

    public HomeCommandAliase() {
        super("home",
                new ArgumentUse("home").addNote("Nombre de tu Home").addArgOptional().addArg("add", "remove"),
                "*",
                "Crear un home donde te puedes hacer tp",
                false);
        addAlias("h");
    }

    private final Location spawnLocation = new Location(Bukkit.getWorlds().getFirst(), 0, 0, 0);

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof Player player){
            AviaTerraPlayer atp = AviaTerraPlayer.getPlayer(player);
            if (args.length == 0) {
                MessagesManager.sendArgument(sender, this.getAviaTerraUsage(), TypeMessages.ERROR);
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
                            MessagesManager.sendFormatMessage(sender, Message.COMMAND_HOME_CLOSE_SPAWN, Math.round(distanceMinTp - distance));
                        }

                    }else {
                        MessagesManager.sendFormatMessage(sender, Message.COMMAND_HOME_NOT_FOUND, args[0]);
                    }
                }
                case 2 -> {
                    FileYaml fileYaml = DataSection.getPlayersDataFiles().getConfigFile(atp.getUuid().toString(), true);
                    switch (args[1].toLowerCase()){
                        case "add" -> AviaTerraCore.enqueueTaskAsynchronously(() -> {
                            if (args[0].contains(".")){
                                MessagesManager.sendMessage(sender, Message.COMMAND_HOME_CONTAINS_POINT);
                                return;
                            }
                            int maxHome = AviaTerraPlayer.getPlayer(player).getMaxHome();
                            if (atp.getHomes().size() >= maxHome) {
                                MessagesManager.sendFormatMessage(sender, Message.COMMAND_HOME_MAX_HOME, maxHome);
                                return;
                            }
                            atp.getHomes().put(args[0], player.getLocation());
                            fileYaml.saveData();
                            MessagesManager.sendMessage(sender, Message.COMMAND_HOME_ADD_SUCCESSFUL);
                        });
                        case "remove" -> AviaTerraCore.enqueueTaskAsynchronously(() -> {
                            if (atp.getHomes().containsKey(args[0])) {
                                atp.getHomes().remove(args[0]);
                                fileYaml.saveData();
                                MessagesManager.sendMessage(sender, Message.COMMAND_HOME_REMOVE_SUCCESSFUL);
                            }else {
                                MessagesManager.sendMessage(sender, Message.COMMAND_HOME_NOT_FOUND_REMOVE);
                            }
                        });
                    }
                }
            }
        }else {
            MessagesManager.sendMessage(sender, Message.COMMAND_GENERIC_NO_PLAYER);
        }
    }

    @Override
    public List<String> onTab(CommandSender sender, String[] args) {
        if (sender instanceof Player player) {
            AviaTerraPlayer atp = AviaTerraPlayer.getPlayer(player);
            switch (args.length) {
                case 1 -> {
                    List<String> names = new ArrayList<>(atp.getHomes().keySet());
                    if (names.isEmpty()) {
                        return List.of("nombre");
                    }else {
                        return CommandUtils.listTab(args[0], names);
                    }
                }
                case 2 -> {
                    return CommandUtils.listTab(args[1], "add", "remove");
                }
            }
        }
        return new ArrayList<>();
    }

    @Override
    public @NotNull List<String> getCommandsAliases() {
        return List.of("setHome", "delHome");
    }

    @Override
    public List<BiConsumer<CommandSender, String[]>> getExecuteAliase() {
        var list = new ArrayList<BiConsumer<CommandSender, String[]>>();
        list.add((sender, args) -> {
            if (args.length > 0) {
                this.execute(sender, String.format("%s add", args[0]).split(" "));
            }else {
                MessagesManager.sendArgument(sender, this.getAviaTerraUsage(), TypeMessages.ERROR);
            }
        });
        list.add((sender, args) -> {
            if (args.length > 0) {
                this.execute(sender, String.format("%s remove", args[0]).split(" "));
            }else {
                MessagesManager.sendArgument(sender, this.getAviaTerraUsage(), TypeMessages.ERROR);
            }
        });
        return list;
    }

    @Override
    public List<BiFunction<CommandSender, String[], List<String>>> getTabAliase() {
        var list = new ArrayList<BiFunction<CommandSender, String[], List<String>>>();
        list.add((sender, args) -> new ArgumentUse("sethome").addNote("nombre").onTab(args));
        list.add(((sender, args) -> {
            if (sender instanceof Player player && args.length == 1) {
                AviaTerraPlayer atp = AviaTerraPlayer.getPlayer(player);
                return CommandUtils.listTab(args[0], new ArrayList<>(atp.getHomes().keySet()));
            }
            return List.of();
        }));
        return list;
    }
}
