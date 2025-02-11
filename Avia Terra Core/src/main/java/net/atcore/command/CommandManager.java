package net.atcore.command;

import lombok.experimental.UtilityClass;
import net.atcore.AviaTerraCore;
import net.atcore.messages.CategoryMessages;
import net.atcore.messages.MessagesManager;
import net.atcore.messages.TypeMessages;
import net.atcore.security.Login.LoginManager;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Member;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Objects;

import static net.atcore.messages.MessagesManager.sendMessage;

@UtilityClass
public class CommandManager {//nose si poner en esta clase aquí la verdad

    public final HashMap<String, String> COMMANDS = new HashMap<>();
    public final HashMap<String, String> COMMANDS_AVIA_TERRA = new HashMap<>();

    public boolean checkCommand(String command, Player player, boolean isSilent, boolean b){
        if (COMMANDS.containsKey(command.toLowerCase()) || COMMANDS_AVIA_TERRA.containsKey(command.toLowerCase())) {
            String permission = Objects.requireNonNullElse(COMMANDS.get(command.toLowerCase()), COMMANDS_AVIA_TERRA.get(command.toLowerCase()));
            if (CommandUtils.hasPermission(permission, player, b)) {
                return false;
            } else {
                if (!isSilent) {
                    if (LoginManager.checkLogin(player, true, b)) {
                        sendMessage(player, net.atcore.messages.Message.COMMAND_GENERIC_NO_PERMISSION);
                    } else {
                        sendMessage(player, net.atcore.messages.Message.COMMAND_GENERIC_NO_LOGIN);
                    }
                }
                return true;
            }
        }else{
            if (LoginManager.checkLogin(player, true, b)){
                if (player.isOp()){
                    return false;
                }else {
                    if (!isSilent) MessagesManager.sendMessage(player, net.atcore.messages.Message.COMMAND_GENERIC_NO_PERMISSION);
                }
            }else {
                if (!isSilent)sendMessage(player, net.atcore.messages.Message.COMMAND_GENERIC_NO_LOGIN);
            }
            return true;
        }
    }

    public void processCommandFromDiscord(Message command, Member member){

        Bukkit.getScheduler().runTask(AviaTerraCore.getInstance(), () -> {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.getContentRaw().substring(1));
            MessagesManager.logConsole(String.format(net.atcore.messages.Message.COMMAND_GENERIC_RUN_LOG.getMessageLocatePrivate()
                    , member.getUser().getGlobalName() + "(" + member.getId() + ")" ,"<gold>`" + command.getContentRaw() + "`"), TypeMessages.INFO, CategoryMessages.COMMANDS, false);
        });
        /*
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
            command.reply(net.atcore.messages.Message.COMMAND_GENERIC_NO_PERMISSION_CONSOLE.getMessage()).queue();
            return;
        }
        for (RangeType range : rolesHasMember) {
            String commandRaw = command.getContentRaw().substring(1).split(" ")[0].toLowerCase();
            if (COMMANDS.containsKey(commandRaw)){
                String permission = COMMANDS.get(commandRaw.toLowerCase());
                hasPermission = hasPermission || CommandUtils.hasPermission(permission, range);
            }else {
                if (range.isOp()){
                    hasPermission = true;
                }else {
                    command.reply(net.atcore.messages.Message.COMMAND_GENERIC_NO_PERMISSION_CONSOLE.getMessage()).queue();
                    return;
                }
            }
        }
        if (hasPermission){

        }else {
            command.reply(net.atcore.messages.Message.COMMAND_GENERIC_NO_PERMISSION_CONSOLE.getMessage()).queue();
        }
        */
    }
}
