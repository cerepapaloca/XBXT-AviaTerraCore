package net.atcore.guns;

import lombok.Getter;
import lombok.Setter;
import net.atcore.utils.GlobalUtils;
import org.bukkit.*;
import org.bukkit.Color;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.List;

@Getter
@Setter
public class BaseWeapon {

    public BaseWeapon(WeaponList list, List<ChargerList> chargerLists, float maxDistance, String displayName) {
        this.MAX_DISTANCE = maxDistance;
        this.CHARGERS_TYPE = chargerLists;
        itemWeapon = new ItemStack(Material.NAME_TAG);
        GlobalUtils.addProtectionAntiDupe(itemWeapon);
        GlobalUtils.setPersistentDataItem(itemWeapon, "weaponAmmo", PersistentDataType.INTEGER, 0);
        GlobalUtils.setPersistentDataItem(itemWeapon, "weaponName", PersistentDataType.STRING, list.name());
        GlobalUtils.setPersistentDataItem(itemWeapon, "chargerTypeNow", PersistentDataType.STRING, "null");
        ItemMeta meta = itemWeapon.getItemMeta();
        if (meta == null) return;
        meta.setDisplayName(displayName);
        itemWeapon.setItemMeta(meta);
    }

    public BaseWeapon(WeaponList list, ChargerList chargerTypes, float maxDistance, String displayName) {
        this(list, List.of(chargerTypes), maxDistance, displayName);
    }

    private final ItemStack itemWeapon;
    private final List<ChargerList> CHARGERS_TYPE;
    private final float MAX_DISTANCE;

    public void shoot(Player player) {
        ItemStack itemWeapon = player.getInventory().getItemInMainHand();

        ChargerList chargerList = ChargerList.valueOf((String) GlobalUtils.getPersistenData(itemWeapon, "chargerTypeNow", PersistentDataType.STRING));
        if (!CHARGERS_TYPE.contains(chargerList))return;
        DataCharger dataCharger = GunsSection.dataChargers.get(chargerList);

        Integer ammo = (Integer) GlobalUtils.getPersistenData(itemWeapon, "weaponAmmo", PersistentDataType.INTEGER);
        if (ammo != null && ammo > 0) {
            ammo--;
            Bukkit.getLogger().warning("ammo: " + ammo);
            GlobalUtils.setPersistentDataItem(itemWeapon, "weaponAmmo", PersistentDataType.INTEGER, ammo);
            Vector direction = player.getLocation().getDirection();
            Location location = player.getEyeLocation();

            RayTraceResult result = player.getWorld().rayTraceEntities(
                    location,
                    direction,
                    MAX_DISTANCE,
                    0.5,
                    entity -> entity != player
            );
            if (result != null) {
                Entity entity = result.getHitEntity();
                if (entity != null) {
                    if (!(entity instanceof Player)) {
                        if (entity instanceof LivingEntity livingEntity) {
                            livingEntity.damage(dataCharger.getDamage());
                        }
                        double distance = player.getEyeLocation().distance(entity.getLocation());
                        drawParticleLine(player.getEyeLocation(), getPlayerLookLocation(player, distance, dataCharger.getDensityTrace()),
                                Color.fromRGB(40, 240, 40), true);
                    }
                }
            }else{
                drawParticleLine(player.getEyeLocation(), getPlayerLookLocation(player, MAX_DISTANCE, dataCharger.getDensityTrace()),
                        Color.fromRGB(40, 240, 40), false);
            }
        }
    }

    public void reload(Player player) {
        ItemStack itemWeapon = player.getInventory().getItemInMainHand();
        Integer ammo = (Integer) GlobalUtils.getPersistenData(itemWeapon, "weaponAmmo", PersistentDataType.INTEGER);
        if (ammo == null)return;

        for (ItemStack itemStack : player.getInventory().getStorageContents()) {//hay que remplasar el itemWeapon por qué se obtiene una copia pero no el itemWeapon real
            if (itemStack == null) continue;
            ChargerList chargerType = ChargerList.valueOf((String) GlobalUtils.getPersistenData(itemWeapon, "chargerType", PersistentDataType.STRING));
            if (!CHARGERS_TYPE.contains(chargerType))continue;
            Integer ammoCharger = (Integer) GlobalUtils.getPersistenData(itemStack, "chargerAmmo", PersistentDataType.INTEGER);
            if (ammoCharger == null) continue;
            DataCharger dataCharger = GunsSection.dataChargers.get(chargerType);
            int delta = dataCharger.getAmmoMax() - ammo;
            int result = ammoCharger - delta;
            if (result > 0){
                GlobalUtils.setPersistentDataItem(itemStack, "chargerAmmo", PersistentDataType.INTEGER, result);
                GlobalUtils.setPersistentDataItem(itemWeapon, "weaponAmmo", PersistentDataType.INTEGER, delta + ammo);
            }else{
                GlobalUtils.setPersistentDataItem(itemWeapon, "weaponAmmo", PersistentDataType.INTEGER, delta + ammo + result);
                itemStack.setAmount(0);
            }

            break;
        }
    }

    private void drawParticleLine(Location start, Location end, Color color, boolean impacted) {
        World world = start.getWorld();
        if (world == null || !world.equals(end.getWorld())) {
            return;
        }

        Vector direction = end.toVector().subtract(start.toVector()).normalize();
        double distance = start.distance(end);
        double particleSpacing = 0.2;

        Particle.DustOptions dustOptions = new Particle.DustOptions(color, 1);
        Location point = start;
        for (double d = 0; d < distance; d += particleSpacing) {
            point = start.clone().add(direction.clone().multiply(d));
            world.spawnParticle(Particle.DUST, point, 1, dustOptions);
        }
        if (impacted)world.spawnParticle(Particle.CRIT, point, 5, 0.1, 0.1, 0.1, 0.1);
    }

    private Location getPlayerLookLocation(Player player, double maxDistance, double stepSize) {

        Location eyeLocation = player.getEyeLocation();
        Vector direction = eyeLocation.getDirection().normalize();

        Location currentLocation = eyeLocation.clone();

        for (double i = 0; i < maxDistance; i += stepSize) {
            currentLocation.add(direction.clone().multiply(stepSize));

            if (currentLocation.getBlock().getType().isSolid()) {
                return currentLocation;
            }
        }

        return eyeLocation.add(direction.multiply(maxDistance));
    }

}
