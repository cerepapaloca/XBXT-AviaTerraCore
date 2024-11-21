package net.atcore.listenerManager;

import net.atcore.AviaTerraCore;
import net.atcore.Config;
import net.atcore.armament.*;
import net.atcore.messages.CategoryMessages;
import net.atcore.messages.MessagesManager;
import net.atcore.messages.TypeMessages;
import net.atcore.command.CommandManager;
import net.atcore.moderation.Freeze;
import net.atcore.security.Login.LoginManager;
import net.atcore.utils.GlobalConstantes;
import net.atcore.utils.GlobalUtils;
import net.atcore.utils.RangeList;
import net.luckperms.api.node.types.InheritanceNode;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static net.atcore.armament.BaseWeaponTarkov.checkReload;

public class PlayerListener implements Listener {

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (LoginManager.isLimboMode(event.getPlayer())) {
            event.setCancelled(true);
            return;
        }
        event.setCancelled(Freeze.isFreeze(event.getPlayer()));
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (LoginManager.isLimboMode(event.getPlayer())) {
            event.setCancelled(true);
            return;
        }

        if (Freeze.isFreeze(player)){
            event.setCancelled(true);
            return;
        }
        event.setCancelled(ArmamentActions.shootAction(event.getAction(), player));
        addRange(player);
    }

    private final List<String> COMMANDS_PRE_LOGIN = List.of("login", "register", "log", "reg");

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onExecuteCommand(PlayerCommandPreprocessEvent event) {
        String command = event.getMessage().split(" ")[0].substring(1).toLowerCase();
        Player player = event.getPlayer();
        String s = "";
        TypeMessages type = TypeMessages.INFO;
        boolean isCancelled = CommandManager.checkCommand(command, player, false, true);
        if (isCancelled){
            s = " &c(Cancelado)";
            type = TypeMessages.WARNING;
        }
        if (COMMANDS_PRE_LOGIN.contains(command)) {
            MessagesManager.sendMessageConsole(String.format("<|%s|> ejecutó -> %s", player.getName(), "&6*Comando De Login*" + s), type, CategoryMessages.COMMANDS, true);
        }else {
            MessagesManager.sendMessageConsole(String.format("<|%s|> ejecutó -> %s", player.getName(), "&6" + event.getMessage() + s), type, CategoryMessages.COMMANDS, true);
        }
        event.setCancelled(isCancelled);
    }

    @EventHandler
    public void onCommand(PlayerCommandSendEvent event) {
        List<String> commands = new ArrayList<>(event.getCommands());
        event.getCommands().clear();
        for (String command : commands){
            if (!CommandManager.checkCommand(command, event.getPlayer(), true, false)){
                event.getCommands().add(command);
            }
        }
    }

    @EventHandler
    public void onSwap(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();
        event.setCancelled(ArmamentActions.reloadAction(player, event.getOffHandItem()));
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        event.setCancelled(checkReload(player));
    }

    public void addRange(Player player){
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType().equals(Material.NAME_TAG)){
            String range = (String) GlobalUtils.getPersistenData(item, "rangeName", PersistentDataType.STRING);
            Long time = (Long) GlobalUtils.getPersistenData(item, "durationRange", PersistentDataType.LONG);
            Long date = (Long) GlobalUtils.getPersistenData(item, "dateCreationRange", PersistentDataType.LONG);
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
            RangeList rangeList = RangeList.valueOf(range.toUpperCase());
            MessagesManager.sendTitle(player,"Nuevo Rango", GlobalUtils.applyGradient(
                    "<" + GlobalUtils.modifyColorHexWithHLS(GlobalUtils.colorToStringHex(rangeList.getColor()), 0f, 0.3f, -0.1f) + ">" +
                            rangeList.getDisplayName() +
                            "<" + GlobalUtils.modifyColorHexWithHLS(GlobalUtils.colorToStringHex(rangeList.getColor()), 0, -0.2f, 0.1f) + ">",
                    'l'
            ), 20, 60, 40, TypeMessages.INFO);

        }
    }
}
