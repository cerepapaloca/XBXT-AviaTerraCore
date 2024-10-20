package net.atcore.ListenerManager;

import net.atcore.Messages.TypeMessages;
import net.atcore.Moderation.Freeze;
import net.atcore.Security.Login.LoginManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.List;

import static net.atcore.Messages.MessagesManager.sendMessage;

public class PlayerListener implements Listener {

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (!LoginManager.getListPlayerLoginIn().contains(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
            return;
        }
        event.setCancelled(Freeze.isFreeze(event.getPlayer()));
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (!LoginManager.getListPlayerLoginIn().contains(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
            return;
        }
        event.setCancelled(Freeze.isFreeze(event.getPlayer()));
    }

    private final List<String> COMMANDS_PRE_LOGIN = List.of("login", "register", "log", "reg");

    @EventHandler
    public void onExecuteCommand(PlayerCommandPreprocessEvent event) {
        String command = event.getMessage().split(" ")[0].substring(1).toLowerCase();
        Player player = event.getPlayer();

        if (COMMANDS_PRE_LOGIN.contains(command)) {
            return;
        }

        if (!LoginManager.checkLoginIn(player, true)) {
            sendMessage(player,"Primero inicia sessión usando /login", TypeMessages.ERROR);
            event.setCancelled(true);
        }
    }
}
