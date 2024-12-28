package net.atcore.listener;

import net.atcore.AviaTerraCore;
import net.atcore.Config;
import net.atcore.armament.*;
import net.atcore.messages.CategoryMessages;
import net.atcore.messages.MessagesManager;
import net.atcore.messages.TypeMessages;
import net.atcore.command.CommandManager;
import net.atcore.misc.LimitWorld;
import net.atcore.moderation.Freeze;
import net.atcore.security.Login.LoginManager;
import net.atcore.utils.GlobalConstantes;
import net.atcore.utils.GlobalUtils;
import net.atcore.utils.RangeType;
import net.luckperms.api.node.types.InheritanceNode;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static net.atcore.armament.BaseWeaponTarkov.checkReload;

public class PlayerListener implements Listener {

    @EventHandler
    public void onMove(@NotNull PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (LoginManager.isLimboMode(player)) {
            event.setCancelled(true);
            return;
        }
        LimitWorld.checkLimit(player);
        event.setCancelled(Freeze.isFreeze(player));
    }

    @EventHandler
    public void onInteract(@NotNull PlayerInteractEvent event) {
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
    public void onExecuteCommand(@NotNull PlayerCommandPreprocessEvent event) {
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
            MessagesManager.sendMessageConsole(String.format("<|%s|> ejecutó -> %s", player.getName(), "&6*Comando De Login*" + s), type, CategoryMessages.COMMANDS, false);
        }else {
            MessagesManager.sendMessageConsole(String.format("<|%s|> ejecutó -> %s", player.getName(), "&6`" + event.getMessage() + "`" + s), type, CategoryMessages.COMMANDS, false);
        }
        event.setCancelled(isCancelled);
    }

    @EventHandler
    public void onCommand(@NotNull PlayerCommandSendEvent event) {
        List<String> commands = new ArrayList<>(event.getCommands());
        event.getCommands().clear();
        for (String command : commands){
            if (!CommandManager.checkCommand(command, event.getPlayer(), true, false)){
                event.getCommands().add(command);
            }
        }
    }

    @EventHandler
    public void onSwap(@NotNull PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();
        event.setCancelled(ArmamentActions.reloadAction(player, event.getOffHandItem()));
    }

    @EventHandler
    public void onDrop(@NotNull PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        event.setCancelled(checkReload(player));
    }

    public void addRange(@NotNull Player player){
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
                AviaTerraCore.getLp().getUserManager().modifyUser(player.getUniqueId(),
                        user -> user.data().add(InheritanceNode.builder(range).expiry(time, TimeUnit.MILLISECONDS).build()));
            }else {
                AviaTerraCore.getLp().getUserManager().modifyUser(player.getUniqueId(),
                        user -> user.data().remove(InheritanceNode.builder(range).build()));
            }
            item.setType(Material.AIR);
            player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 0.8F, 1);
            RangeType rangeType = RangeType.valueOf(range.toUpperCase());
            MessagesManager.sendTitle(player,"Nuevo Rango", GlobalUtils.applyGradient(
                    "<" + GlobalUtils.modifyColorHexWithHLS(GlobalUtils.BukkitColorToStringHex(rangeType.getColor()), 0f, 0.3f, -0.1f) + ">" +
                            rangeType.getDisplayName() +
                            "<" + GlobalUtils.modifyColorHexWithHLS(GlobalUtils.BukkitColorToStringHex(rangeType.getColor()), 0, -0.2f, 0.1f) + ">",
                    'l'
            ), 20, 60, 40, TypeMessages.INFO);

        }
    }
}
