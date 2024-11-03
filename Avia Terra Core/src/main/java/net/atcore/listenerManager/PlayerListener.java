package net.atcore.listenerManager;

import net.atcore.AviaTerraCore;
import net.atcore.Config;
import net.atcore.armament.ArmamentUtils;
import net.atcore.armament.BaseWeapon;
import net.atcore.armament.Compartment;
import net.atcore.armament.BaseWeaponTarkov;
import net.atcore.messages.TypeMessages;
import net.atcore.moderation.Freeze;
import net.atcore.security.Login.LoginManager;
import net.atcore.utils.GlobalConstantes;
import net.atcore.utils.GlobalUtils;
import net.luckperms.api.node.types.InheritanceNode;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static net.atcore.armament.BaseWeaponTarkov.checkReload;
import static net.atcore.messages.MessagesManager.*;

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

        if (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            BaseWeapon weapon = ArmamentUtils.getWeapon(player);
            if (weapon == null) return;
            weapon.shoot(player);
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

    @EventHandler
    public void onSwap(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();
        Compartment compartment = ArmamentUtils.getCompartment(event.getOffHandItem());
        if (compartment != null){
            compartment.reload(player);
            event.setCancelled(true);
        }
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
            player.sendTitle(ChatColor.translateAlternateColorCodes('&',COLOR_ESPECIAL + "Se te dio el rango " + range)
                    ,"", 20, 60, 40);

        }
    }
}
