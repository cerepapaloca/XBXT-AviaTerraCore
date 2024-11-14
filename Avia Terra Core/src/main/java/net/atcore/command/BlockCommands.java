package net.atcore.command;

import net.atcore.messages.MessagesManager;
import net.atcore.messages.TypeMessages;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class BlockCommands {//nose si poner en esta clase aquí la verdad

    public static final HashMap<String, String> COMMANDS = new HashMap<>();

    static {
        COMMANDS.put("r", "*");
        COMMANDS.put("w", "*");
        COMMANDS.put("tell", "*");
        COMMANDS.put("msg", "*");
    }

    public static boolean checkCommand(String command, Player player, boolean isSilent){
        if (player.isOp()) return false;

        if (COMMANDS.containsKey(command)){
            String permission = COMMANDS.get(command);
            if (permission == null){
                return false;
            }else{
                if (CommandUtils.hasPermission(permission, player)){
                    return false;
                }else{
                    if (!isSilent) MessagesManager.sendMessage(player, "No tienes permisos para ejecutar ese comando", TypeMessages.ERROR);
                    return true;
                }
            }
        }else{
            if (!isSilent) MessagesManager.sendMessage(player, "No tienes autorización para ejecutar ese comando", TypeMessages.ERROR);
            return true;
        }
    }
}
