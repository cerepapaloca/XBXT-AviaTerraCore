package net.atcore.command;

import net.atcore.messages.MessagesManager;
import net.atcore.messages.TypeMessages;
import net.atcore.security.Login.LoginManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.BufferedReader;
import java.util.HashMap;

import static net.atcore.messages.MessagesManager.sendMessage;

public class CommandManager {//nose si poner en esta clase aquí la verdad

    public static final HashMap<String, String> COMMANDS = new HashMap<>();
    public static final HashMap<String, String> COMMANDS_AVIA_TERRA = new HashMap<>();

    public static boolean checkCommand(String command, Player player, boolean isSilent, boolean b){
        if (COMMANDS.containsKey(command.toLowerCase())){

            String permission = COMMANDS.get(command.toLowerCase());
            if (permission == null){
                return false;
            }else{
                if (CommandUtils.hasPermission(permission, player, b)){
                    return false;
                }else{
                    if (!isSilent){
                        if (LoginManager.checkLoginIn(player, true, b)){
                            sendMessage(player, "No tienes permisos para ejecutar ese comando", TypeMessages.ERROR);
                        }else {
                            sendMessage(player, "Primero inicia sessión usando /login", TypeMessages.ERROR);
                        }
                    }
                    return true;
                }
            }
        }else{
            if (LoginManager.checkLoginIn(player, true, b)){
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
