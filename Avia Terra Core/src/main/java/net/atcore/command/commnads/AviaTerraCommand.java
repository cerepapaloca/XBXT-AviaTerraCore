package net.atcore.command.commnads;

import net.atcore.AviaTerraCore;
import net.atcore.Config;
import net.atcore.command.ArgumentUse;
import net.atcore.command.BaseTabCommand;
import net.atcore.command.CommandUtils;
import net.atcore.command.CommandVisibility;
import net.atcore.data.DataSection;
import net.atcore.data.yml.MapArtFile;
import net.atcore.messages.MessagesManager;
import net.atcore.messages.TypeMessages;
import net.atcore.security.login.ServerMode;
import net.atcore.utils.AviaTerraScheduler;
import net.atcore.utils.GlobalUtils;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class AviaTerraCommand extends BaseTabCommand {

    public AviaTerraCommand() {
        super("AviaTerra",
                new ArgumentUse("aviaTerra"),
                CommandVisibility.PRIVATE,
                "Se encarga de la configuración de algunos apartados del plugin"
        );
        addAlias("at");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {//TODO: mejorar el comando un poquito
        switch (args[0].toLowerCase().replace("_","")) {
            case "reload" -> {
                AviaTerraCore.getInstance().reloadConfig();
                AviaTerraScheduler.enqueueTaskAsynchronously(() -> MessagesManager.sendString(sender,"Reload Terminado", TypeMessages.SUCCESS));
            }
            case "servermode" -> {
                if (args.length >= 2) {
                    ServerMode mode;
                    try {
                        mode = ServerMode.valueOf(args[1].toUpperCase());
                    }catch (Exception e){
                        MessagesManager.sendString(sender, "Modo no valido", TypeMessages.ERROR);
                        return;
                    }
                    Config.setServerMode(mode);
                    MessagesManager.sendString(sender,"El servidor esta en " + Config.getServerMode().name().toLowerCase().replace("_"," "), TypeMessages.INFO);
                }else{
                    MessagesManager.sendString(sender,"El servidor esta <|" + Config.getServerMode().name().toLowerCase() + "|>", TypeMessages.INFO);
                }
                DataSection.getConfigFile().saveData();
            }
            case "purgarangos" -> {
                Config.setPurgeTagRange(System.currentTimeMillis());
                MessagesManager.sendString(sender,"Todas los tags de rango ya no son validas y comenzara su eliminación", TypeMessages.INFO);
                DataSection.getConfigFile().saveData();
            }
            case "tiempodesession" -> {
                if (args.length >= 2) {
                    try {
                        Config.setExpirationSession(CommandUtils.StringToMilliseconds(args[1], true));
                        MessagesManager.sendString(sender, "se cambio el la duración de la sesión", TypeMessages.SUCCESS);
                    }catch (RuntimeException e){
                        MessagesManager.sendString(sender, "formato de fecha incorrecto", TypeMessages.ERROR);
                    }
                }else{
                    MessagesManager.sendString(sender,"el tiempo de expiración de expiration esta en <|" +
                            GlobalUtils.timeToString(Config.getExpirationSession(), 2) + "|>", TypeMessages.INFO);
                }
                DataSection.getConfigFile().saveData();
            }
            case "levelmoderationchat" -> {
                if (args.length >= 2) {
                    try {
                        Config.setLevelModerationChat(Float.parseFloat(args[1]));
                        MessagesManager.sendString(sender, "se cambio el nivel de moderación", TypeMessages.SUCCESS);
                    }catch (RuntimeException e){
                        MessagesManager.sendString(sender, "solo números con decimales", TypeMessages.ERROR);
                    }
                }else{
                    MessagesManager.sendString(sender,"El nivel de moderación en el chat esta en <|" + Config.getLevelModerationChat() + "|>", TypeMessages.INFO);
                }
                DataSection.getConfigFile().saveData();
            }
            case "mapart" -> {
                if (args.length >= 2) {
                    switch (args[1]) {
                        case "add" -> {
                            List<Integer> list = new ArrayList<>();
                            for (int i = 4; i < args.length; i++){
                                list.add(Integer.parseInt(args[i]));
                            }
                            String[] size = args[2].split(",");
                            MapArtFile.MapData mapArt = new MapArtFile.MapData(args[3], list, Integer.parseInt(size[0]), Integer.parseInt(size[1]));
                            MapArtFile.MAP_DATA_LIST.add(mapArt);
                            AviaTerraScheduler.enqueueTaskAsynchronously(() -> DataSection.getMapArtsFiles().getConfigFile(mapArt.getId(), true).saveData());
                        }
                        case "remove" -> {
                            MapArtFile.MapData mapArt = MapArtFile.getMapData(args[2]);
                            if (mapArt == null) {
                                MessagesManager.sendString(sender, "El art no existe", TypeMessages.ERROR);
                                return;
                            }
                            MapArtFile.MAP_DATA_LIST.remove(mapArt);
                            DataSection.getMapArtsFiles().deleteConfigFile(mapArt.getId());
                        }
                    }
                }
            }
            case "thread" -> AviaTerraScheduler.enqueueTaskAsynchronously(() -> {
                ArrayList<Double> sorted = new ArrayList<>(AviaTerraScheduler.telemetryTasks.stream().map(AviaTerraScheduler.telemetryTask::elapsedProcess).toList());
                Collections.sort(sorted);

                double min = sorted.getFirst();
                double max = sorted.getLast();
                double sum = 0;
                for (double time : sorted) {
                    sum += time;
                }

                double average = sum / sorted.size();
                double p95 = sorted.get((int) (sorted.size() * 0.95) - 1); // percentil 95+
                String s1 = String.format("Avg: <|%.2f|>ms | P95: <|%.2f|>ms | Min: <|%.2f|>ms | Max: <|%.2f|>ms",
                        average, p95, min, max);

                LinkedList<Integer> max10m = new LinkedList<>();
                LinkedList<Integer> max1m = new LinkedList<>();
                LinkedList<Integer> max10s = new LinkedList<>();

                long currentTime = System.currentTimeMillis();
                for (AviaTerraScheduler.telemetryTask telemetry : AviaTerraScheduler.telemetryTasks) {
                    if (currentTime - 1000*10 < telemetry.currentTime()) max10s.add(telemetry.queue());
                    if (currentTime - 1000*60 <  telemetry.currentTime()) max1m.add(telemetry.queue());
                    if (currentTime - 1000*600 < telemetry.currentTime()) max10m.add(telemetry.queue());
                }
                Collections.sort(max10m);
                Collections.sort(max1m);
                Collections.sort(max10s);
                StringBuilder sb = new StringBuilder();
                for (int i = AviaTerraScheduler.amountTask.size(); i > 1; i--) {
                    sb.append(" | ").append(((AviaTerraScheduler.amountTask.size() - i) +1) * 2).append("m: <|").append(AviaTerraScheduler.amountTask.get(i - 1)).append("|>");
                }
                String s2 = String.format("queue: <|%s|> " + "actual: <|%s|>" + sb,
                        AviaTerraScheduler.taskQueue.size(), AviaTerraScheduler.currentAoumt.get());
                String s3 = String.format("10s: <|%s|> | 1m: <|%s|> | 10m: <|%s|>"
                        , max10s.isEmpty() ? 0 : max10m.getLast(),max1m.isEmpty() ? 0 : max1m.getLast(),max10m.isEmpty() ? 0 : max10m.getLast());
                MessagesManager.sendString(sender, "Ultimas 200 Tareas:\n" + s1 + "\n" + s2 + "\n" + s3, TypeMessages.INFO);
            });
        }
    }

    @Override
    public List<String> onTab(CommandSender sender, String[] args) {
        String[] argsRoot = new String[]{"antiBot","reload", "mapArt", "serverMode", "purgaRangos","tiempoDeSession", "levelModerationChat", "thread"};
        if (args.length >= 2) {
            switch (args[0].toLowerCase().replace("_","")) {
                case "antiop", "antiilegalitems", "checkbanporip" -> {
                    return CommandUtils.listTab(args[1], "true", "false");
                }
                case "servermode" -> {
                    return CommandUtils.listTab(args[1], CommandUtils.enumsToStrings(ServerMode.values()));
                }
                case "tiempodesesion" -> {
                    return CommandUtils.listTabTime(args[1], false);
                }
                case "levelmoderationchat" -> {
                    return List.of("#.#");
                }
                case "mapart" -> {
                    if (args.length >= 3) {
                        switch (args[1].toLowerCase()) {
                            case "add" -> {
                                switch (args.length) {
                                    case 3 -> {
                                        return List.of("X,Y");
                                    }
                                    case 4 -> {
                                        return null;
                                    }
                                    default -> {
                                        return List.of("#");
                                    }
                                }
                            }
                            case "remove" -> {
                                return MapArtFile.MAP_DATA_LIST.stream().map(MapArtFile.MapData::getId).collect(Collectors.toList());
                            }
                        }
                    }else {
                        return CommandUtils.listTab(args[1], "add", "remove");
                    }
                }
            }
        }
        return CommandUtils.listTab(args[0], argsRoot);
    }
}
