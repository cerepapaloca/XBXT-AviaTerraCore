package net.atcore.listener;

import net.atcore.messages.MessagesManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.UUID;

public class DeathListener implements Listener {

    private final HashMap<UUID, UUID> MapLastDamagerEntity = new HashMap<>();

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        Player player = e.getEntity();
        if (player.getLastDamageCause() != null){
            e.deathMessage(null);

            LivingEntity killer;
            if (e.getEntity().getKiller() != null) {
                killer = e.getEntity().getKiller();
            } else {
                // En caso de que no tenga un killer se usara la última entidad que le hizo daño
                Entity entity = Bukkit.getEntity(MapLastDamagerEntity.get(player.getUniqueId()));
                if (entity instanceof LivingEntity le) {
                    killer = le;
                }else {
                    killer = null;
                }
            }

            ItemStack item = getItemStack(killer);
            MessagesManager.deathMessage(player, killer, item, player.getLastDamageCause().getCause());
        }
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
