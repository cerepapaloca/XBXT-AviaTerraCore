package net.atcore.command;

import net.atcore.messages.MessagesManager;
import net.atcore.messages.TypeMessages;
import net.atcore.security.Login.LoginManager;
import net.atcore.utils.RangeList;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;

import static net.atcore.messages.MessagesManager.sendMessage;

public class CommandManager {//nose si poner en esta clase aquí la verdad

    public static final HashMap<String, String> COMMANDS = new HashMap<>();

    static {
        COMMANDS.put("!", RangeList.BUILDER.getPermission());
        COMMANDS.put(",", RangeList.BUILDER.getPermission());
        COMMANDS.put("/", RangeList.BUILDER.getPermission());
        COMMANDS.put("/br", RangeList.BUILDER.getPermission());
        COMMANDS.put("/brush", RangeList.BUILDER.getPermission());
        COMMANDS.put("/calc", RangeList.BUILDER.getPermission());
        COMMANDS.put("/calcule", RangeList.BUILDER.getPermission());
        COMMANDS.put("/center", RangeList.BUILDER.getPermission());
        COMMANDS.put("/chunk", RangeList.BUILDER.getPermission());
        COMMANDS.put("/clearhistory", RangeList.BUILDER.getPermission());
        COMMANDS.put("/cone", RangeList.BUILDER.getPermission());
        COMMANDS.put("/contract", RangeList.BUILDER.getPermission());
        COMMANDS.put("/copy", RangeList.BUILDER.getPermission());
        COMMANDS.put("/count", RangeList.BUILDER.getPermission());
        COMMANDS.put("/curve", RangeList.BUILDER.getPermission());
        COMMANDS.put("/cut", RangeList.BUILDER.getPermission());
        COMMANDS.put("/cyl", RangeList.BUILDER.getPermission());
        COMMANDS.put("/deform", RangeList.BUILDER.getPermission());
        COMMANDS.put("/desel", RangeList.BUILDER.getPermission());
        COMMANDS.put("/deselect", RangeList.BUILDER.getPermission());
        COMMANDS.put("/distr", RangeList.BUILDER.getPermission());
        COMMANDS.put("/drain", RangeList.BUILDER.getPermission());
        COMMANDS.put("/drawsel", RangeList.BUILDER.getPermission());
        COMMANDS.put("/eval", RangeList.BUILDER.getPermission());
        COMMANDS.put("/evaluate", RangeList.BUILDER.getPermission());
        COMMANDS.put("/ex", RangeList.BUILDER.getPermission());
        COMMANDS.put("/expand", RangeList.BUILDER.getPermission());
        COMMANDS.put("/extinguish", RangeList.BUILDER.getPermission());
        COMMANDS.put("/faces", RangeList.BUILDER.getPermission());
        COMMANDS.put("/fast", RangeList.BUILDER.getPermission());
        COMMANDS.put("/feature", RangeList.BUILDER.getPermission());
        COMMANDS.put("/fill", RangeList.BUILDER.getPermission());
        COMMANDS.put("/fillr", RangeList.BUILDER.getPermission());
        COMMANDS.put("/fixwater", RangeList.BUILDER.getPermission());
        COMMANDS.put("/flip", RangeList.BUILDER.getPermission());
        COMMANDS.put("/flora", RangeList.BUILDER.getPermission());
        COMMANDS.put("/forest", RangeList.BUILDER.getPermission());
        COMMANDS.put("/g", RangeList.BUILDER.getPermission());
        COMMANDS.put("/gb", RangeList.BUILDER.getPermission());
        COMMANDS.put("/gen", RangeList.BUILDER.getPermission());
        COMMANDS.put("/genbiome", RangeList.BUILDER.getPermission());
        COMMANDS.put("/generate", RangeList.BUILDER.getPermission());
        COMMANDS.put("/generatebiome", RangeList.BUILDER.getPermission());
        COMMANDS.put("/gmask", RangeList.BUILDER.getPermission());
        COMMANDS.put("/green", RangeList.BUILDER.getPermission());
        COMMANDS.put("/hcyl", RangeList.BUILDER.getPermission());
        COMMANDS.put("/help", RangeList.BUILDER.getPermission());
        COMMANDS.put("/hollow", RangeList.BUILDER.getPermission());
        COMMANDS.put("/hpos1", RangeList.BUILDER.getPermission());
        COMMANDS.put("/hpos2", RangeList.BUILDER.getPermission());
        COMMANDS.put("/hpyramid", RangeList.BUILDER.getPermission());
        COMMANDS.put("/hsphere", RangeList.BUILDER.getPermission());
        COMMANDS.put("/inset", RangeList.BUILDER.getPermission());
        COMMANDS.put("/l", RangeList.BUILDER.getPermission());
        COMMANDS.put("/limit", RangeList.BUILDER.getPermission());
        COMMANDS.put("/line", RangeList.BUILDER.getPermission());
        COMMANDS.put("/lrbuild", RangeList.BUILDER.getPermission());
        COMMANDS.put("/material", RangeList.BUILDER.getPermission());
        COMMANDS.put("/middle", RangeList.BUILDER.getPermission());
        COMMANDS.put("/move", RangeList.BUILDER.getPermission());
        COMMANDS.put("/naturalize", RangeList.BUILDER.getPermission());
        COMMANDS.put("/navwand", RangeList.BUILDER.getPermission());
        COMMANDS.put("/outline", RangeList.BUILDER.getPermission());
        COMMANDS.put("/outset", RangeList.BUILDER.getPermission());
        COMMANDS.put("/overlay", RangeList.BUILDER.getPermission());
        COMMANDS.put("/paste", RangeList.BUILDER.getPermission());
        COMMANDS.put("/perf", RangeList.BUILDER.getPermission());
        COMMANDS.put("/placement", RangeList.BUILDER.getPermission());
        COMMANDS.put("/pos", RangeList.BUILDER.getPermission());
        COMMANDS.put("/pos1", RangeList.BUILDER.getPermission());
        COMMANDS.put("/pos2", RangeList.BUILDER.getPermission());
        COMMANDS.put("/pyranmid", RangeList.BUILDER.getPermission());
        COMMANDS.put("/re", RangeList.BUILDER.getPermission());
        COMMANDS.put("/redo", RangeList.BUILDER.getPermission());
        COMMANDS.put("/regen", RangeList.BUILDER.getPermission());
        COMMANDS.put("/removeabove", RangeList.BUILDER.getPermission());
        COMMANDS.put("/removebelow", RangeList.BUILDER.getPermission());
        COMMANDS.put("/removenear", RangeList.BUILDER.getPermission());
        COMMANDS.put("/reorder", RangeList.BUILDER.getPermission());
        COMMANDS.put("/rep", RangeList.BUILDER.getPermission());
        COMMANDS.put("/replace", RangeList.BUILDER.getPermission());
        COMMANDS.put("/replacenear", RangeList.BUILDER.getPermission());
        COMMANDS.put("/restore", RangeList.BUILDER.getPermission());
        COMMANDS.put("/rotate", RangeList.BUILDER.getPermission());
        COMMANDS.put("/schem", RangeList.BUILDER.getPermission());
        COMMANDS.put("/schematic", RangeList.BUILDER.getPermission());
        COMMANDS.put("/search", RangeList.BUILDER.getPermission());
        COMMANDS.put("/searchitem", RangeList.BUILDER.getPermission());
        COMMANDS.put("/sel", RangeList.BUILDER.getPermission());
        COMMANDS.put("/selwand", RangeList.BUILDER.getPermission());
        COMMANDS.put("/set", RangeList.BUILDER.getPermission());
        COMMANDS.put("/setbiome", RangeList.BUILDER.getPermission());
        COMMANDS.put("/shift", RangeList.BUILDER.getPermission());
        COMMANDS.put("/size", RangeList.BUILDER.getPermission());
        COMMANDS.put("/smooth", RangeList.BUILDER.getPermission());
        COMMANDS.put("/snow", RangeList.BUILDER.getPermission());
        COMMANDS.put("/snowsmooth", RangeList.BUILDER.getPermission());
        COMMANDS.put("/solve", RangeList.BUILDER.getPermission());
        COMMANDS.put("/sphere", RangeList.BUILDER.getPermission());
        COMMANDS.put("/stack", RangeList.BUILDER.getPermission());
        COMMANDS.put("/structure", RangeList.BUILDER.getPermission());
        COMMANDS.put("/thaw", RangeList.BUILDER.getPermission());
        COMMANDS.put("/timeout", RangeList.BUILDER.getPermission());
        COMMANDS.put("/toggleplace", RangeList.BUILDER.getPermission());
        COMMANDS.put("/trim", RangeList.BUILDER.getPermission());
        COMMANDS.put("/undo", RangeList.BUILDER.getPermission());
        COMMANDS.put("/update", RangeList.BUILDER.getPermission());
        COMMANDS.put("/walls", RangeList.BUILDER.getPermission());
        COMMANDS.put("/wand", RangeList.BUILDER.getPermission());
        COMMANDS.put("/watchdog", RangeList.BUILDER.getPermission());
        COMMANDS.put("/world", RangeList.BUILDER.getPermission());
        COMMANDS.put(";", RangeList.BUILDER.getPermission());
        COMMANDS.put("asc", RangeList.BUILDER.getPermission());
        COMMANDS.put("ascend", RangeList.BUILDER.getPermission());
        COMMANDS.put("biomeinfo", RangeList.BUILDER.getPermission());
        COMMANDS.put("biomelist", RangeList.BUILDER.getPermission());
        COMMANDS.put("biomels", RangeList.BUILDER.getPermission());
        COMMANDS.put("br", RangeList.BUILDER.getPermission());
        COMMANDS.put("brush", RangeList.BUILDER.getPermission());
        COMMANDS.put("butcher", RangeList.BUILDER.getPermission());
        COMMANDS.put("ceil", RangeList.BUILDER.getPermission());
        COMMANDS.put("chunkinfo", RangeList.BUILDER.getPermission());
        COMMANDS.put("clearclipboard", RangeList.BUILDER.getPermission());
        COMMANDS.put("clearhistory", RangeList.BUILDER.getPermission());
        COMMANDS.put("cs", RangeList.BUILDER.getPermission());
        COMMANDS.put("cycler", RangeList.BUILDER.getPermission());
        COMMANDS.put("delchuncks", RangeList.BUILDER.getPermission());
        COMMANDS.put("deltree", RangeList.BUILDER.getPermission());
        COMMANDS.put("desc", RangeList.BUILDER.getPermission());
        COMMANDS.put("descend", RangeList.BUILDER.getPermission());
        COMMANDS.put("ex", RangeList.BUILDER.getPermission());
        COMMANDS.put("ext", RangeList.BUILDER.getPermission());
        COMMANDS.put("extinguish", RangeList.BUILDER.getPermission());
        COMMANDS.put("farwand", RangeList.BUILDER.getPermission());
        COMMANDS.put("fixlava", RangeList.BUILDER.getPermission());
        COMMANDS.put("fixwater", RangeList.BUILDER.getPermission());
        COMMANDS.put("flood", RangeList.BUILDER.getPermission());
        COMMANDS.put("floodfill", RangeList.BUILDER.getPermission());
        COMMANDS.put("forestgen", RangeList.BUILDER.getPermission());
        COMMANDS.put("gmask", RangeList.BUILDER.getPermission());
        COMMANDS.put("green", RangeList.BUILDER.getPermission());
        COMMANDS.put("info", RangeList.BUILDER.getPermission());
        COMMANDS.put("j", RangeList.BUILDER.getPermission());
        COMMANDS.put("jumpto", RangeList.BUILDER.getPermission());
        COMMANDS.put("listchunks", RangeList.BUILDER.getPermission());
        COMMANDS.put("lrbuild", RangeList.BUILDER.getPermission());
        COMMANDS.put("mask", RangeList.BUILDER.getPermission());
        COMMANDS.put("material", RangeList.BUILDER.getPermission());
        COMMANDS.put("navwand", RangeList.BUILDER.getPermission());
        COMMANDS.put("none", RangeList.BUILDER.getPermission());
        COMMANDS.put("pickaxe", RangeList.BUILDER.getPermission());
        COMMANDS.put("placement", RangeList.BUILDER.getPermission());
        COMMANDS.put("pumpkins", RangeList.BUILDER.getPermission());
        COMMANDS.put("range", RangeList.BUILDER.getPermission());
        COMMANDS.put("redo", RangeList.BUILDER.getPermission());
        COMMANDS.put("rement", RangeList.BUILDER.getPermission());
        COMMANDS.put("remove", RangeList.BUILDER.getPermission());
        COMMANDS.put("removeabove", RangeList.BUILDER.getPermission());
        COMMANDS.put("removevebelow", RangeList.BUILDER.getPermission());
        COMMANDS.put("removenear", RangeList.BUILDER.getPermission());
        COMMANDS.put("repl", RangeList.BUILDER.getPermission());
        COMMANDS.put("replacenear", RangeList.BUILDER.getPermission());
        COMMANDS.put("restore", RangeList.BUILDER.getPermission());
        COMMANDS.put("schem", RangeList.BUILDER.getPermission());
        COMMANDS.put("schematic", RangeList.BUILDER.getPermission());
        COMMANDS.put("searchitem", RangeList.BUILDER.getPermission());
        COMMANDS.put("selwand", RangeList.BUILDER.getPermission());
        COMMANDS.put("size", RangeList.BUILDER.getPermission());
        COMMANDS.put("snap", RangeList.BUILDER.getPermission());
        COMMANDS.put("snapshot", RangeList.BUILDER.getPermission());
        COMMANDS.put("snow", RangeList.BUILDER.getPermission());
        COMMANDS.put("sp", RangeList.BUILDER.getPermission());
        COMMANDS.put("superpickaxe", RangeList.BUILDER.getPermission());
        COMMANDS.put("thaw", RangeList.BUILDER.getPermission());
        COMMANDS.put("thru", RangeList.BUILDER.getPermission());
        COMMANDS.put("toggleplace", RangeList.BUILDER.getPermission());
        COMMANDS.put("tool", RangeList.BUILDER.getPermission());
        COMMANDS.put("tracemask", RangeList.BUILDER.getPermission());
        COMMANDS.put("tree", RangeList.BUILDER.getPermission());
        COMMANDS.put("undo", RangeList.BUILDER.getPermission());
        COMMANDS.put("unstuck", RangeList.BUILDER.getPermission());
        COMMANDS.put("up", RangeList.BUILDER.getPermission());
        COMMANDS.put("we", RangeList.BUILDER.getPermission());
        COMMANDS.put("worldedit", RangeList.BUILDER.getPermission());
        COMMANDS.put("gm", RangeList.BUILDER.getPermission());
        COMMANDS.put("gamemode", RangeList.BUILDER.getPermission());
        COMMANDS.put("time", RangeList.BUILDER.getPermission());
        COMMANDS.put("tp", RangeList.BUILDER.getPermission());
        COMMANDS.put("weather", RangeList.BUILDER.getPermission());
        COMMANDS.put("whitelist", "!*");
        COMMANDS.put("r", "*");
        COMMANDS.put("msg", "*");
        COMMANDS.put("w", "*");
    }

    public static boolean checkCommand(String command, Player player, boolean isSilent){
        Bukkit.getLogger().warning("A |" + command);
        if (COMMANDS.containsKey(command.toLowerCase())){
            String permission = COMMANDS.get(command.toLowerCase());
            Bukkit.getLogger().warning("B |" + permission);
            if (permission == null){

                return false;
            }else{
                if (CommandUtils.hasPermission(permission, player)){
                    return false;
                }else{
                    if (!isSilent){
                        if (LoginManager.checkLoginIn(player, true)){
                            sendMessage(player, "No tienes permisos para ejecutar ese comando", TypeMessages.ERROR);
                        }else {
                            sendMessage(player,"Primero inicia sessión usando /login", TypeMessages.ERROR);
                        }
                    }
                    return true;
                }
            }
        }else{
            if (LoginManager.checkLoginIn(player, true)){
                if (player.isOp()){
                    return false;
                }else {
                    if (!isSilent) MessagesManager.sendMessage(player, "No tienes autorización para ejecutar ese comando", TypeMessages.ERROR);
                }
            }else {
                if (!isSilent)sendMessage(player,"Primero inicia sessión usando /login", TypeMessages.ERROR);
            }
            return true;
        }
    }
}
