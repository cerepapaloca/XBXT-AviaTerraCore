package net.atcore.Moderation;

import net.atcore.AviaTerraCore;
import net.atcore.Messages.CategoryMessages;
import net.atcore.Messages.TypeMessages;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.UUID;

import static net.atcore.Messages.MessagesManager.sendMessage;
import static net.atcore.Messages.MessagesManager.sendMessageConsole;

public class ChatModeration {

    private static final HashSet<UUID> listCooldown = new HashSet<>();

    public static boolean CheckChatModeration(Player player, String message, String prefix) {
        if (listCooldown.contains(player.getUniqueId())) {
            sendMessageConsole( "&r" + prefix + "    " + player.getName() + " » &7" + message + "&c [ELIMINADO: Spam]", TypeMessages.INFO, CategoryMessages.MODERATION);
            sendMessage(player, "mensaje eliminado por Spam espera <|4|> segundos", TypeMessages.ERROR);
            return true;
        }else {
            listCooldown.add(player.getUniqueId());
            coolDown(player.getUniqueId());
            return false;
        }
    }

    private static void coolDown(UUID uuid){
        new BukkitRunnable() {
            public void run() {
                listCooldown.remove(uuid);
            }
        }.runTaskLater(AviaTerraCore.getInstance(), 20*4);//4 segundos de cooldown
    }
}
