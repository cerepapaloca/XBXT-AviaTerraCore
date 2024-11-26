package net.atcore.command;

import net.atcore.AviaTerraCore;
import net.atcore.messages.CategoryMessages;
import net.atcore.messages.MessagesManager;
import net.atcore.messages.TypeMessages;
import net.atcore.security.Login.LoginManager;
import net.atcore.utils.RangeType;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.Member;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

    public static void processCommandFromDiscord(Message command, Member member){
        boolean hasPermission = false;
        List<RangeType> rolesHasMember = new ArrayList<>();
        List<String> rolesId = new ArrayList<>();// Se crea las listas
        for (Role rangeList : member.getRoles()) rolesId.add(rangeList.getId());// Crea añade las Ids de los roles
        for (RangeType range : RangeType.values()) {
            if (rolesId.contains(range.getRolId())) {// Mira si tiene ese rol
                rolesHasMember.add(range);// Añade el rol que pertenece
            }
        }
        if (rolesHasMember.isEmpty()){
            command.reply("No tienes autorización para ejecutar comandos en la consola").queue();
            return;
        }
        for (RangeType range : rolesHasMember) {
            String commandRaw = command.getContentRaw().split(" ")[0].toLowerCase();
            if (COMMANDS.containsKey(commandRaw)){
                String permission = COMMANDS.get(commandRaw.toLowerCase());
                hasPermission = hasPermission || CommandUtils.hasPermission(permission, range);
            }else {
                if (range.isOp()){
                    hasPermission = true;
                }else {
                    command.reply("No tienes permisos para ejecutar ese comando en la consola").queue();
                    return;
                }
            }
        }
        if (hasPermission){
            Bukkit.getScheduler().runTask(AviaTerraCore.getInstance(), () -> {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.getContentRaw());
                MessagesManager.sendMessageConsole(String.format("<|%s|> ejecutó -> %s"
                        , member.getUser().getGlobalName() + "(" + member.getId() + ")" ,"`&6/" + command.getContentRaw() + "`"), TypeMessages.INFO, CategoryMessages.COMMANDS, false);
            });
        }else {
            command.reply("No tienes permisos para ejecutar ese comando en la consola").queue();
        }
    }
}
