package net.atcore.listener;

import net.atcore.messages.MessagesManager;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

public class DeathListener implements Listener {

    private LivingEntity livingEntity;

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        Player player = e.getEntity();
        LivingEntity killer = e.getEntity().getKiller();
        if (player.getLastDamageCause() != null){
            ItemStack item = null;

            if (killer != null) {
                item = ((Player) killer).getInventory().getItemInMainHand();
            }else {
                killer = livingEntity;
            }
            if (item == null || item.getType() == Material.AIR) item = null;
            MessagesManager.deathMessage(player, killer, item, player.getLastDamageCause().getCause());
            e.deathMessage(null);
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player) {
            Entity damager = event.getDamager();
            if (damager instanceof Projectile projectile) {
                if (projectile.getShooter() instanceof LivingEntity shooter) livingEntity = shooter;
            }else if (damager instanceof LivingEntity le) livingEntity = le;
        }
    }
}
