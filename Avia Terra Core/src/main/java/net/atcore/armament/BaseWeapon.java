package net.atcore.armament;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

@Getter
@Setter
public abstract class BaseWeapon extends BaseArmament implements ShootWeapon{
    public BaseWeapon(ItemStack item, int MaxDistance, String displayName, double precision) {
        super(displayName, item, "weapon");
        this.maxDistance = MaxDistance;
        this.precision = precision;

    }

    protected final int maxDistance;
    protected final double precision;

    protected DataShoot executeShoot(Player player, BaseAmmo ammo, BaseMagazine charger) {
        Vector direction = player.getEyeLocation().getDirection();
        Location location = player.getEyeLocation();
        Vector directionRandom = direction.add(new Vector((Math.random() - 0.5)*precision*0.002, (Math.random() - 0.5)*precision*0.002, (Math.random() - 0.5)*precision*0.002));//añade la imprecision del arma

        RayTraceResult result = player.getWorld().rayTraceEntities(//se crea un rayTrace
                location,
                directionRandom,
                maxDistance,
                0.5,//el margen para detectar a las entidades
                entity -> entity != player//se descarta el protio jugador
        );
        Block lastBlock = null;
        float f = 0;
        double distance = maxDistance;


        boolean b = false;
        LivingEntity livingEntity = null;
        if (result != null) {
            Entity entity = result.getHitEntity();
            distance = location.distance(result.getHitPosition().toLocation(player.getWorld()));
            if (entity != null) {
                if (entity instanceof LivingEntity) {//se tiene que hacer instance por qué no la variable entity no sirve en este caso
                    livingEntity = (LivingEntity) entity;
                }
            }
        }
        BlockIterator blockIterator = new BlockIterator(
                player.getWorld(),
                location.toVector(),
                directionRandom,
                0,
                (int) distance
        );
        int i = 0;
        while (blockIterator.hasNext() && i < maxDistance*2) {
            i++;//esto es una mini protección con los bucles
            Block block = blockIterator.next();
            f += block.getType().getHardness();//suma la resistencia de todos los bloques que se encuentra
            if (block.getType() == Material.VOID_AIR) break;
            if ((f < ammo.getPenetration() || lastBlock == null) && block.getType() != Material.AIR) {
                lastBlock = block;
            }
        }
        DataShoot dataShoot = new DataShoot(livingEntity, player, this, charger, ammo, distance);//se crea los datos del disparo
        dataShoot.setDamage(ammo.getDamage());
        onShoot(dataShoot);
        if (dataShoot.isCancelled()) return dataShoot;
        if (f < ammo.getPenetration() && livingEntity != null) {
            livingEntity.damage(dataShoot.getDamage(), player);
            livingEntity.getWorld().playSound(livingEntity.getLocation(), Sound.ITEM_TRIDENT_HIT, SoundCategory.PLAYERS, 1 ,1);
            b = true;
        }
        Location finalLocation;
        if (lastBlock != null) {
            lastBlock.getWorld().playSound(lastBlock.getLocation(), Sound.BLOCK_ANVIL_HIT, SoundCategory.BLOCKS, 0.5f,1);
            finalLocation = ArmamentUtils.getLookLocation(directionRandom, location, location.distance(lastBlock.getLocation()), 0.25);
        }else {
            finalLocation = ArmamentUtils.getLookLocation(directionRandom, location, distance, 0.25);
        }
        ArmamentUtils.drawParticleLine(player.getEyeLocation(),finalLocation,
                ammo.getColor(), b, ammo.getDensityTrace());

        return dataShoot;
    }

    @Override
    public abstract void shoot(Player player);

    public abstract void onShoot(DataShoot dataShoot);
}
