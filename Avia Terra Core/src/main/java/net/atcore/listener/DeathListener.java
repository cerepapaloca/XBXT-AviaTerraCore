package net.atcore.listener;

import net.atcore.messages.CategoryMessages;
import net.atcore.messages.MessagesManager;
import net.atcore.messages.TypeMessages;
import net.atcore.utils.GlobalUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.UUID;

public class DeathListener implements Listener {

    private static final HashMap<UUID, UUID> MapLastDamagerEntity = new HashMap<>();

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        Player player = e.getEntity();

        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        meta.setOwningPlayer(player);
        skull.setItemMeta(meta);
        player.getWorld().dropItemNaturally(player.getLocation(), skull);

        if (player.getLastDamageCause() != null){
            try {
                LivingEntity killer;
                if (player.getKiller() != null) {
                    killer = player.getKiller();
                } else {
                    // En caso de que no tenga un killer se usara la última entidad que le hizo daño
                    killer = getKillerByDamage(player);
                }

                ItemStack item = getItemStack(killer);
                MessagesManager.logConsole(String.format("Jugador: %s murió en %s", player.getName(), GlobalUtils.locationToString(player.getLocation())), TypeMessages.INFO, CategoryMessages.PLAY);
                MessagesManager.deathMessage(player, killer, item, player.getLastDamageCause().getCause());
                e.deathMessage(null);
            }catch (Exception ex) {
                MessagesManager.sendWaringException("Error al modificar el mensaje de muerte", ex);
            }
        }
    }

    public static @Nullable LivingEntity getKillerByDamage(Player player) {
        LivingEntity killer;
        UUID uuid = MapLastDamagerEntity.get(player.getUniqueId());
        if (uuid != null){
            Entity entity = Bukkit.getEntity(uuid);
            if (entity instanceof LivingEntity le) {
                killer = le;
            }else {
                killer = null;
            }
        }else {
            killer = null;
        }
        return killer;
    }

    private @Nullable ItemStack getItemStack(LivingEntity killer) {
        ItemStack item = null;
        if (killer != null) {// Si es nulo no tiene item con que matarlo
            EntityEquipment equipment = killer.getEquipment();
            // Se asegura que esta entidad tenga slots de equipamiento
            if (equipment != null) {
                // Se obtiene el item
                item = equipment.getItemInMainHand();
            }
        }
        // Si el item es aire se trabajara como nulo
        if (item == null || item.getType() == Material.AIR) item = null;
        return item;
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player player) {
            Entity damager = event.getDamager();
            if (damager instanceof Projectile projectile) {
                if (projectile.getShooter() instanceof LivingEntity shooter) MapLastDamagerEntity.put(player.getUniqueId(), shooter.getUniqueId());
            }else if (damager instanceof LivingEntity le) MapLastDamagerEntity.put(player.getUniqueId(), le.getUniqueId());
        }
    }
}
