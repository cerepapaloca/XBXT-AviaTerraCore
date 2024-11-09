package net.atcore.moderation;

import net.atcore.messages.MessagesManager;
import net.atcore.messages.TypeMessages;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class BlockCommands {//nose si poner en esta clase aquí la verdad

    BlockCommands(){
        commands.put("login", null);
        commands.put("log", null);
        commands.put("register", null);
        commands.put("reg", null);
        commands.put("r", null);
        commands.put("w", null);
        commands.put("tell", null);
        commands.put("msg", null);
    }

    private static final HashMap<String, String> commands = new HashMap<>();


    public static boolean checkCommand(String command, Player player){
        if (player.isOp()) return false;

        if (commands.containsKey(command)){
            String permission = commands.get(command);
            if (permission == null){
                return false;
            }else{
                if (player.hasPermission(permission)){
                    MessagesManager.sendMessage(player, "No tienes permisos para ejecutar ese comando", TypeMessages.ERROR);
                    return true;
                }else{
                    return false;
                }
            }
        }else{
            MessagesManager.sendMessage(player, "No tienes permisos para ejecutar ese comando", TypeMessages.ERROR);
            return true;
        }
    }
}
