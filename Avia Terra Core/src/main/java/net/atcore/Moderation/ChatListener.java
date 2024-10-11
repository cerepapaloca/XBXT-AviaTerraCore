package net.atcore.Moderation;

import net.atcore.AviaTerraCore;
import net.atcore.Messages.CategoryMessages;
import net.atcore.Messages.TypeMessages;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.UUID;

import static net.atcore.Messages.MessagesManager.sendMessage;
import static net.atcore.Messages.MessagesManager.sendMessageConsole;

public class ChatListener implements Listener {

    private final HashSet<UUID> listCooldown = new HashSet<>();

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        if (listCooldown.contains(event.getPlayer().getUniqueId())) {
            sendMessageConsole(event.getPlayer().getName() + " » " +  event.getMessage() + "&c [MENSAJE ELIMINADO]", null, CategoryMessages.MODERATION);
            sendMessage(event.getPlayer(), "mensaje eliminado por Spam espera <|4|> segundos", TypeMessages.ERROR);
            event.setCancelled(true);
        }else {
            listCooldown.add(event.getPlayer().getUniqueId());
            coolDown(event.getPlayer().getUniqueId());
        }
    }

    private void coolDown(UUID uuid){
        new BukkitRunnable() {
            public void run() {
                listCooldown.remove(uuid);
            }
        }.runTaskLater(AviaTerraCore.PLUGIN, 20*4);//4 segundos de cooldown
    }
}
