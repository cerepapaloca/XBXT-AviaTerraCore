package net.atcore.armament;

import lombok.Getter;
import lombok.Setter;
import net.atcore.AviaTerraCore;
import net.atcore.aviaterraplayer.ArmamentPlayer;
import net.atcore.aviaterraplayer.AviaTerraPlayer;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Realiza el funcionamiento del arma de fuego especialmente el disparo final, Las subClases se encarga de otros procesos
 * <p>
 * <H3>Secuencia De Disparo<H/>
 * <ul>
 * Comienza en {@link ArmamentActions#shootAction(Action, Player) shootAction()} realizando comprobaciones básicas, en caso de
 * que el jugador tenga un arma sigue con {@link #preProcessShoot(Player) preProcessShoot()} comprueba si el arma es automática
 * o semi, para saber cuantas veces tiene que llamar {@link #processShoot(Player) processShoot()} hay se hace el descuento de la bala
 * y otros procesos que se encarga la subClase y termina en {@link  #processRayShoot(Player, BaseAmmo, BaseMagazine) processRayShoot()}
 * creando un rayo donde golpeara a la entidad y iniciando {@link #onShoot(List) onShoot()} para que cada arma pueda dar una modificación
 * al disparo o para añadir propiedades especiales al disparo
 */

@Getter
@Setter
public abstract class BaseWeapon extends BaseArmament implements ShootWeapon{
    public BaseWeapon(ItemStack item,
                      int MaxDistance,
                      String displayName,
                      double vague,
                      WeaponMode mode,
                      int coolDown
    ) {
        super(displayName, item);
        this.maxDistance = MaxDistance;
        this.vague = vague;
        this.mode = mode;
        this.coolDown = coolDown;
    }

    protected final int maxDistance;
    protected final double vague;
    protected final WeaponMode mode;
    protected final int coolDown;
    private long lastShoot;

    /**
     * Realiza la cantidad de llamadas que tiene que hacer en caso
     * de que sea un arma automática
     */

    public void preProcessShoot(Player player){
        ArmamentPlayer armamentPlayer = AviaTerraPlayer.getPlayer(player).getArmamentPlayer();
        switch(mode){
            case AUTOMATIC -> {

                if (armamentPlayer.getShootTask() == null || armamentPlayer.getShootTask().isCancelled()){
                    BukkitTask task = new BukkitRunnable() {
                        private int i;
                        public void run() {
                            if (i > (8/coolDown) +((coolDown%2) != 0 ? 1 : 0)) cancel();
                            i++;
                            processShoot(player);
                        }
                    }.runTaskTimer(AviaTerraCore.getInstance(), 0, coolDown);
                    armamentPlayer.setShootTask(task);
                }
            }
            case SEMI -> {
                if (armamentPlayer.getLastShoot() < System.currentTimeMillis()){
                    armamentPlayer.setLastShoot(System.currentTimeMillis() + coolDown*20L);
                    processShoot(player);
                }
            }
        }
    }

    /**
     * Realiza el procedimiento de efectos del disparo como de daño
     * a la entidad en caso de ser golpeado.
     */

    protected void processRayShoot(Player player, BaseAmmo ammo, @Nullable BaseMagazine charger) {
        List<ShootData> data = new ArrayList<>();
        int projectiles;
        if (ammo instanceof Shot shot){
            projectiles = shot.getAmount();
        }else if (charger instanceof Shot shot){
            projectiles = shot.getAmount();
        }else if (this instanceof Shot shot){
            projectiles = shot.getAmount();
        }else {
            projectiles = 1;
        }

        for (int i = 0 ; i < projectiles; i++){
            Vector direction = player.getEyeLocation().getDirection();
            Location location = player.getEyeLocation();
            // Añade la imprecision del arma
            Vector directionRandom = direction.add(new Vector(
                    (Math.random() - 0.5) * vague * 0.01,
                    (Math.random() - 0.5) * vague * 0.01,
                    (Math.random() - 0.5) * vague * 0.01
            ));
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
            int j = 0;
            while (blockIterator.hasNext() && j < maxDistance*2) {
                j++;//esto es una mini protección con los bucles
                Block block = blockIterator.next();
                f += block.getType().getHardness();//suma la resistencia de todos los bloques que se encuentra
                if (block.getType() == Material.VOID_AIR) break;
                if ((f < ammo.getPenetration() || lastBlock == null) && block.getType() != Material.AIR) {
                    lastBlock = block;
                }
            }
            ShootData shootData = new ShootData(livingEntity, player, this, charger, ammo, distance);//se crea los datos del disparo
            shootData.setDamage(ammo.getDamage());
            shootData.setHardnessPenetration(f);
            data.add(shootData);

            Location finalLocation;
            if (lastBlock != null) {
                lastBlock.getWorld().playSound(lastBlock.getLocation(), Sound.BLOCK_ANVIL_HIT, SoundCategory.BLOCKS, 0.5f,1);
                finalLocation = ArmamentUtils.getLookLocation(directionRandom, location, location.distance(lastBlock.getLocation()), 0.25);
            }else {
                finalLocation = ArmamentUtils.getLookLocation(directionRandom, location, distance, 0.25);
            }
            if (ammo instanceof Trace trace){
                ArmamentUtils.drawParticleLine(player.getEyeLocation(), finalLocation, trace.getColorTrace(), trace.getDensityTrace());
            }
        }

        onShoot(data);
        ammo.onShoot(data);
        if (charger != null) charger.onShoot(data);

        HashMap<UUID, Double> victims = new HashMap<>();
        for (ShootData sd : data) {
            if (sd.isCancelled()) continue;
            LivingEntity entity = sd.getVictim();
            if (sd.getHardnessPenetration() < ammo.getPenetration() && entity != null) {
                victims.put(entity.getUniqueId(), victims.getOrDefault(entity.getUniqueId(), 0D) + sd.getDamage());
                entity.getWorld().playSound(entity.getLocation(), Sound.ITEM_TRIDENT_HIT, SoundCategory.PLAYERS, 1 ,1);
                entity.getWorld().spawnParticle(Particle.CRIT, entity.getLocation(), 4, 0.3, 0.3, 0.3, 0.2, null, true);
            }
        }

        for (UUID uuid : victims.keySet()) {
            LivingEntity entity = (LivingEntity) Bukkit.getEntity(uuid);
            if (entity == null) continue;
            entity.damage(victims.get(uuid), player);
        }
    }

    public abstract void processShoot(Player player);

    @Override
    public void onShoot(List<ShootData> shootData) {}
}
