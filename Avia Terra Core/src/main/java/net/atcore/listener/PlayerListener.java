package net.atcore.listener;

import net.atcore.AviaTerraCore;
import net.atcore.Config;
import net.atcore.armament.ArmamentActions;
import net.atcore.command.CommandManager;
import net.atcore.messages.CategoryMessages;
import net.atcore.messages.Message;
import net.atcore.messages.MessagesManager;
import net.atcore.messages.TypeMessages;
import net.atcore.misc.FrameDupe;
import net.atcore.moderation.Freeze;
import net.atcore.moderation.ban.ContextBan;
import net.atcore.security.Login.LoginManager;
import net.atcore.utils.GlobalConstantes;
import net.atcore.utils.GlobalUtils;
import net.atcore.utils.RangeType;
import net.dv8tion.jda.api.events.channel.update.ChannelUpdateArchivedEvent;
import net.luckperms.api.node.types.InheritanceNode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
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
        //LimitWorld.checkLimit(player);
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

    @EventHandler
    public void onPlayerJoin(@NotNull EntityDamageByEntityEvent event) {
        FrameDupe.dupeItem(event);
    }

    private final List<String> COMMANDS_PRE_LOGIN = List.of("login", "register", "log", "reg");

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onExecuteCommand(@NotNull PlayerCommandPreprocessEvent event) {
        String command = event.getMessage().split(" ")[0].substring(1);
        Player player = event.getPlayer();
        String s = "";
        TypeMessages type = TypeMessages.INFO;
        ContextBan.CHAT.onContext(player, event);
        if (event.isCancelled()) return;
        boolean isCancelled = CommandManager.checkCommand(command.toLowerCase(), player, false, true);
        if (isCancelled){
            s = " <red>(Cancelado)";
            type = TypeMessages.WARNING;
        }
        if (COMMANDS_PRE_LOGIN.contains(command.toLowerCase())) {
            String commandComplete = event.getMessage().replaceFirst("/" + command, "");
            String resultado = commandComplete.replaceAll("\\S", "*");
            MessagesManager.logConsole(String.format(Message.COMMAND_GENERIC_RUN_LOG.getMessage(player), player.getName(), "<gold>`/" + command + resultado + "`" + s), type, CategoryMessages.COMMANDS, false);
        }else {
            MessagesManager.logConsole(String.format(Message.COMMAND_GENERIC_RUN_LOG.getMessage(player), player.getName(), "<gold>`" + event.getMessage() + "`" + s), type, CategoryMessages.COMMANDS, false);
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

    private void addRange(@NotNull Player player){
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType().equals(Material.NAME_TAG)) {
            String range = (String) GlobalUtils.getPersistenData(item, "rangeName", PersistentDataType.STRING);
            Long time = (Long) GlobalUtils.getPersistenData(item, "durationRange", PersistentDataType.LONG);
            Long date = (Long) GlobalUtils.getPersistenData(item, "dateCreationRange", PersistentDataType.LONG);
            if (date == null) return;
            if (range == null) return;

            if (date < Config.getPurgeTagRange()) {//mira está dentro de la purga
                item.setAmount(0);
                return;
            }
            player.getInventory().getItemInMainHand().setAmount(0);
            if (time != null && time != GlobalConstantes.NUMERO_PERMA) {
                AviaTerraCore.getLp().getUserManager().modifyUser(player.getUniqueId(),
                        user -> user.data().add(InheritanceNode.builder(range).expiry(time, TimeUnit.MILLISECONDS).build()));
            } else {
                AviaTerraCore.getLp().getUserManager().modifyUser(player.getUniqueId(),
                        user -> user.data().remove(InheritanceNode.builder(range).build()));
            }
            item.setAmount(0);
            player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 0.8F, 1);
            RangeType rangeType = RangeType.valueOf(range.toUpperCase());
            String s = "<gradient:" +
                    GlobalUtils.modifyColorHexWithHLS(GlobalUtils.BukkitColorToStringHex(rangeType.getColor()), 0, 0.3f, -0.01f) +
                    ":" +
                    GlobalUtils.modifyColorHexWithHLS(GlobalUtils.BukkitColorToStringHex(rangeType.getColor()), 0, -0.1f, 0) +
                    ">" +
                    rangeType.getDisplayName() +
                    "</gradient>";
            MessagesManager.sendTitle(player,"Nuevo Rango", s, 20, 60, 40, TypeMessages.INFO);
        }
    }
}
