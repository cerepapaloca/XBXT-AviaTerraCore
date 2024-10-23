package net.atcore.ListenerManager;

import net.atcore.AviaTerraCore;
import net.atcore.Config;
import net.atcore.Messages.TypeMessages;
import net.atcore.Moderation.Freeze;
import net.atcore.Security.Login.LoginManager;
import net.atcore.Utils.GlobalConstantes;
import net.atcore.Utils.GlobalUtils;
import net.luckperms.api.node.types.InheritanceNode;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static net.atcore.Messages.MessagesManager.COLOR_ESPECIAL;
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
        Player player = event.getPlayer();
        if (!LoginManager.getListPlayerLoginIn().contains(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
            return;
        }
        if (Freeze.isFreeze(player)){
            event.setCancelled(true);
            return;
        }
        addRange(player);
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

    public void addRange(Player player){
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType().equals(Material.NAME_TAG)){
            String range = (String) GlobalUtils.getPersistenData(item, "range", PersistentDataType.STRING);
            Long time = (Long) GlobalUtils.getPersistenData(item, "duration", PersistentDataType.LONG);
            Long date = (Long) GlobalUtils.getPersistenData(item, "dateCreation", PersistentDataType.LONG);
            if (date == null)return;
            if (range == null)return;

            if (date < Config.getPurgeTagRange()){//mira está dentro de la purga
                item.setType(Material.AIR);
                return;
            }
            player.getInventory().getItemInMainHand().setAmount(0);
            if (time != null && time != GlobalConstantes.NUMERO_PERMA){
                AviaTerraCore.getLP().getUserManager().modifyUser(player.getUniqueId(),
                        user -> user.data().add(InheritanceNode.builder(range).expiry(time, TimeUnit.MILLISECONDS).build()));
            }else {
                AviaTerraCore.getLP().getUserManager().modifyUser(player.getUniqueId(),
                        user -> user.data().remove(InheritanceNode.builder(range).build()));
            }
            item.setType(Material.AIR);
            player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 0.8F, 1);
            player.sendTitle(ChatColor.translateAlternateColorCodes('&',COLOR_ESPECIAL + "Se te dio el rango " + range)
                    ,"", 20, 60, 40);

        }
    }

}
