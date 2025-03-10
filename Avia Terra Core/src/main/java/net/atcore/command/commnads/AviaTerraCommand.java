package net.atcore.command.commnads;

import net.atcore.AviaTerraCore;
import net.atcore.Config;
import net.atcore.command.ArgumentUse;
import net.atcore.command.BaseTabCommand;
import net.atcore.command.CommandUtils;
import net.atcore.command.CommandVisibility;
import net.atcore.data.DataSection;
import net.atcore.data.yml.MapArtFile;
import net.atcore.messages.TypeMessages;
import net.atcore.security.Login.ServerMode;
import net.atcore.utils.GlobalUtils;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static net.atcore.messages.MessagesManager.sendString;

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
    public void execute(CommandSender sender, String[] args) {
        switch (args[0].toLowerCase().replace("_","")) {
            case "reload" -> {
                AviaTerraCore.getInstance().reloadConfig();
                AviaTerraCore.enqueueTaskAsynchronously(() -> sendString(sender,"Reload Terminado", TypeMessages.SUCCESS));
            }
            case "servermode" -> {
                if (args.length >= 2) {
                    ServerMode mode;
                    try {
                        mode = ServerMode.valueOf(args[1].toUpperCase());
                    }catch (Exception e){
                        sendString(sender, "Modo no valido", TypeMessages.ERROR);
                        return;
                    }
                    Config.setServerMode(mode);
                    sendString(sender,"El modo del servidor esta en " + Config.getServerMode().name().toLowerCase().replace("_"," "), TypeMessages.INFO);
                }else{
                    sendString(sender,"El modo mixto esta <|" + Config.getServerMode().name().toLowerCase() + "|>", TypeMessages.INFO);
                }
                DataSection.getConfigFile().saveData();
            }
            case "checkbanporip" -> {
                if (args.length >= 2) {
                    if (CommandUtils.isTrueOrFalse(args[1])){
                        Config.setCheckBanByIp(true);
                        sendString(sender,"El check de baneo por ip <|Activado|>", TypeMessages.INFO);
                    }else{
                        Config.setCheckBanByIp(false);
                        sendString(sender,"El check de baneo por ip <|Desactivado|>", TypeMessages.INFO);
                    }

                }else{
                    sendString(sender,"El check de baneo por ip esta <|" + CommandUtils.booleanToString(Config.isCheckBanByIp()) + "|>", TypeMessages.INFO);
                }
                DataSection.getConfigFile().saveData();
            }
            case "purgarangos" -> {
                Config.setPurgeTagRange(System.currentTimeMillis());
                sendString(sender,"Todas los tags de rango ya no son validas y comenzara su eliminación", TypeMessages.INFO);
                DataSection.getConfigFile().saveData();
            }
            case "tiempodesession" -> {
                if (args.length >= 2) {
                    try {
                        Config.setExpirationSession(CommandUtils.StringToMilliseconds(args[1], true));
                        sendString(sender, "se cambio el la duración de la sesión", TypeMessages.SUCCESS);
                    }catch (RuntimeException e){
                        sendString(sender, "formato de fecha incorrecto", TypeMessages.ERROR);
                    }
                }else{
                    sendString(sender,"el tiempo de expiración de expiration esta en <|" +
                            GlobalUtils.timeToString(Config.getExpirationSession(), 2) + "|>", TypeMessages.INFO);
                }
                DataSection.getConfigFile().saveData();
            }
            case "levelmoderationchat" -> {
                if (args.length >= 2) {
                    try {
                        Config.setLevelModerationChat(Float.parseFloat(args[1]));
                        sendString(sender, "se cambio el nivel de moderación", TypeMessages.SUCCESS);
                    }catch (RuntimeException e){
                        sendString(sender, "solo números con decimales", TypeMessages.ERROR);
                    }
                }else{
                    sendString(sender,"El nivel de moderación en el chat esta en <|" + Config.getLevelModerationChat() + "|>", TypeMessages.INFO);
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
                            AviaTerraCore.enqueueTaskAsynchronously(() -> DataSection.getMapArtsFiles().getConfigFile(mapArt.getId(), true).saveData());
                        }
                        case "remove" -> {
                            MapArtFile.MapData mapArt = MapArtFile.getMapData(args[2]);
                            if (mapArt == null) {
                                sendString(sender, "El art no existe", TypeMessages.ERROR);
                                return;
                            }
                            MapArtFile.MAP_DATA_LIST.remove(mapArt);
                            DataSection.getMapArtsFiles().deleteConfigFile(mapArt.getId());
                        }
                    }
                }
            }
        }
    }

    @Override
    public List<String> onTab(CommandSender sender, String[] args) {
        String[] argsRoot = new String[]{"antiBot","reload", "mapArt", "serverMode", "checkBanPorIp", "purgaRangos","tiempoDeSession", "levelModerationChat"};
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
