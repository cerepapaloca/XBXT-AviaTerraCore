package net.atcore.armament;

import lombok.Getter;
import lombok.Setter;
import net.atcore.AviaTerraPlayer;
import net.atcore.messages.MessagesManager;
import net.atcore.messages.TypeMessages;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

@Getter
@Setter
public abstract class BaseWeaponUltraKill extends BaseWeapon {

    public BaseWeaponUltraKill(ListWeaponUltraKill weaponType, String displayName, int maxDistance, Color color, double damage, int manaCost) {
        super(displayName, new ItemStack(Material.GOLDEN_HORSE_ARMOR), maxDistance, weaponType.name());
        this.color = color;
        this.damage = damage;
        this.manaCost = manaCost;
        this.weaponType = weaponType;
        ItemMeta meta = itemArmament.getItemMeta();
        assert meta != null;
        meta.setDisplayName(displayName);
        itemArmament.setItemMeta(meta);
    }

    private final Color color;
    private final double damage;
    private final int manaCost;
    private final ListWeaponUltraKill weaponType;

    @Override
    public void shoot(Player player) {
        AviaTerraPlayer atp = new AviaTerraPlayer(player);
        double manaPlayer = atp.getMana();
        if (manaCost < manaPlayer){
            atp.setMana(manaPlayer - manaCost);
            Vector direction = player.getLocation().getDirection();
            Location location = player.getEyeLocation();

            RayTraceResult result = player.getWorld().rayTraceEntities(//se crea un rayTrace
                    location,
                    direction,
                    maxDistance,
                    0.5,//el margen para detectar a las entidades
                    entity -> entity != player//se descarta el protio jugador
            );
            if (result != null) {
                Entity entity = result.getHitEntity();
                double distance = player.getEyeLocation().distance(result.getHitPosition().toLocation(player.getWorld()));
                if (entity != null) {
                    if (!(entity instanceof Player)) {
                        if (entity instanceof LivingEntity livingEntity) {//se tiene que hacer instance por qué no la variable entity no sirve en este caso
                            DataShoot dataShoot = new DataShoot(livingEntity, player, this, null, null, distance, damage);//se crea los datos del disparo
                            onShoot(dataShoot);
                            livingEntity.damage(dataShoot.getDamage());//se aplica el daño
                            ArmamentUtils.drawParticleLine(player.getEyeLocation(), ArmamentUtils.getPlayerLookLocation(player, distance, damage),
                                    color, true,1);
                        }
                    }
                }
            }
            ArmamentUtils.drawParticleLine(player.getEyeLocation(), ArmamentUtils.getPlayerLookLocation(player, maxDistance, damage),
                    color, false, 1);
        }else{
            MessagesManager.sendMessage(player, "No tiene mana suficiente", TypeMessages.ERROR);
        }
    }

    public abstract void onShoot(DataShoot dataShoot);
}
