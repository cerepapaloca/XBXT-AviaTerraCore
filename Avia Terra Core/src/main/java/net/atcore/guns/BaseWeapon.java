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

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
public abstract class BaseWeapon {

    protected BaseWeapon(ListWeapon type, List<ListCharger> listChargers, float maxDistance, String displayName) {
        this.MAX_DISTANCE = maxDistance;
        this.CHARGERS_TYPE = listChargers;
        this.weaponType = type;
        itemWeapon = new ItemStack(Material.NAME_TAG);
        //GlobalUtils.setPersistentDataItem(itemWeapon, "chargerAmmo", PersistentDataType.STRING, "");
        GlobalUtils.setPersistentDataItem(itemWeapon, "weaponName", PersistentDataType.STRING, type.name());
        GlobalUtils.setPersistentDataItem(itemWeapon, "chargerTypeNow", PersistentDataType.STRING, "null");
        ItemMeta meta = itemWeapon.getItemMeta();
        if (meta == null) return;
        meta.setDisplayName(displayName);
        itemWeapon.setItemMeta(meta);
        updateLore(null, null);
    }

    protected BaseWeapon(ListWeapon type, ListCharger chargerTypes, float maxDistance, String displayName) {
        this(type, List.of(chargerTypes), maxDistance, displayName);
    }

    private final ItemStack itemWeapon;
    private final List<ListCharger> CHARGERS_TYPE;
    private final float MAX_DISTANCE;
    private final ListWeapon weaponType;

    public void shoot(Player player) {
        ItemStack itemWeapon = player.getInventory().getItemInMainHand();
        String chargerName = (String) GlobalUtils.getPersistenData(itemWeapon, "chargerTypeNow", PersistentDataType.STRING);
        if (chargerName == null || Objects.equals(chargerName, "null")) return;//en caso que no tenga un cargador
        ListCharger listCharger = ListCharger.valueOf(chargerName);
        if (!CHARGERS_TYPE.contains(listCharger))return;//el cargador es compatible (técnicamente siempre tiene que sé compatible)
        BaseCharger baseCharger = GunsSection.dataChargers.get(listCharger);
        String stringAmmo = (String) GlobalUtils.getPersistenData(itemWeapon, "chargerAmmo", PersistentDataType.STRING);
        if (stringAmmo != null) {
            List<String> listAmmo = GunsSection.stringToList(stringAmmo);

            //Bukkit.getLogger().warning(listAmmo.getFirst());/////////////////////////////
            if (listAmmo.isEmpty()){
                updateLore(player, null);
                return;
            }
            if (listAmmo.getFirst().isBlank())return;
            BaseAmmo ammon = GunsSection.baseAmmo.get(ListAmmo.valueOf(listAmmo.getFirst()));
            listAmmo.removeFirst();
            //Bukkit.getLogger().warning(listAmmo.size() + " cantidad de balas");

            if (listAmmo.isEmpty()) {
                //Bukkit.getLogger().warning("Borrado");

                //GlobalUtils.setPersistentDataItem(itemWeapon, "chargerAmmo", PersistentDataType.STRING, "");
                //GlobalUtils.setPersistentDataItem(itemWeapon, "chargerTypeNow", PersistentDataType.STRING, "null");
            }
            GlobalUtils.setPersistentDataItem(itemWeapon, "chargerAmmo", PersistentDataType.STRING, GunsSection.listToString(listAmmo));//guarda la munición actual
            updateLore(player, null);
            Vector direction = player.getLocation().getDirection();
            Location location = player.getEyeLocation();

            RayTraceResult result = player.getWorld().rayTraceEntities(//se crea un rayTrace
                    location,
                    direction,
                    MAX_DISTANCE,
                    0.5,//el margen para detectar a las entidades
                    entity -> entity != player//se descarta el protio jugador
            );
            if (result != null) {
                Entity entity = result.getHitEntity();
                if (entity != null) {
                    if (!(entity instanceof Player)) {
                        double distance = player.getEyeLocation().distance(entity.getLocation());
                        if (entity instanceof LivingEntity livingEntity) {//se tiene que hacer instance por qué no la variable entity no sirve en esta caso
                            livingEntity.damage(ammon.getDamage());//se aplica el daño
                            DataShoot dataShoot = new DataShoot(livingEntity, player, this, baseCharger, distance);//se crea los datos del disparo
                            onShoot(dataShoot);
                            baseCharger.onShoot(dataShoot);
                            ammon.onShoot(dataShoot);
                        }
                        drawParticleLine(player.getEyeLocation(), getPlayerLookLocation(player, distance, ammon.getDamage()),
                                ammon.getColor(), true, ammon.getDensityTrace());
                    }
                }
            }else{//en caso qué no halla impactado con algo
                drawParticleLine(player.getEyeLocation(), getPlayerLookLocation(player, MAX_DISTANCE, ammon.getDensityTrace()),
                        ammon.getColor(), false, ammon.getDensityTrace());
            }
        }
    }

    public void reload(Player player) {
        onReloading(player);
        ItemStack itemWeapon = player.getInventory().getItemInMainHand();
        String ammo1 = (String) GlobalUtils.getPersistenData(itemWeapon, "chargerAmmo", PersistentDataType.STRING);
        List<String> ammoWeapon;
        if (ammo1 == null || ammo1.isBlank()) {
            ammoWeapon = new ArrayList<>();
        }else{
            ammoWeapon = GunsSection.stringToList(ammo1);
        }

        for (ItemStack itemCharger : player.getInventory().getStorageContents()) {
            //realiza todas las comprobaciones
            if (itemCharger == null) continue;

            String chargerName = (String) GlobalUtils.getPersistenData(itemCharger, "chargerType", PersistentDataType.STRING);
            if (chargerName == null) continue;
            ListCharger chargerType = ListCharger.valueOf(chargerName);
            if (!CHARGERS_TYPE.contains(chargerType))continue;//es un cargador compatible?
            ////////
            String chargerNameNow = (String) GlobalUtils.getPersistenData(itemWeapon, "chargerTypeNow", PersistentDataType.STRING);
            if (chargerNameNow == null) continue;
            boolean b;
            BaseCharger baseCharger = GunsSection.dataChargers.get(chargerType);
            if (!ammoWeapon.isEmpty()){
                ListCharger chargerNow = ListCharger.valueOf(chargerNameNow);
                b = GunsSection.dataChargers.get(ListCharger.valueOf(chargerName)).getAmmoMax() < baseCharger.getAmmoMax();
                baseCharger = GunsSection.dataChargers.get(chargerNow);
            }else{
                b = true;
            }
            Bukkit.getLogger().warning(GunsSection.dataChargers.get(ListCharger.valueOf(chargerName)).getAmmoMax() + " now");
            Bukkit.getLogger().warning(baseCharger.getAmmoMax() + " other");
            int delta = baseCharger.getAmmoMax() - ammoWeapon.size();//la diferencia de munición entre cantidad de munición actual y la maxima
            String ammo2 = (String) GlobalUtils.getPersistenData(itemCharger, "chargerAmmo", PersistentDataType.STRING);
            if (ammo2 == null) continue;
            List<String> ammoCharger = GunsSection.stringToList(ammo2);
            int result = Math.min(ammoCharger.size(), delta);

            for (int i = 0; i < result; i++) {
                ammoWeapon.add(ammoCharger.removeFirst());
            }
            Bukkit.getLogger().warning("Transference:" + result);
            if (ammoCharger.isEmpty()) {
                itemCharger.setAmount(0);
            }else{
                GlobalUtils.setPersistentDataItem(itemCharger, "chargerAmmo", PersistentDataType.STRING, GunsSection.listToString(ammoCharger));
            }
            GlobalUtils.setPersistentDataItem(itemWeapon, "chargerAmmo", PersistentDataType.STRING, GunsSection.listToString(ammoWeapon));
            GlobalUtils.setPersistentDataItem(itemWeapon, "chargerTypeNow", PersistentDataType.STRING,
                    b ?  chargerName : chargerNameNow);
            Bukkit.getLogger().warning( b ?  chargerName : chargerNameNow);
            updateLore(player, itemCharger);
            break;
        }
    }

    private void updateLore(Player player, ItemStack charger) {
        String s;
        ItemMeta itemMeta;
        ItemStack weapon;

        if (player != null){
            weapon = player.getInventory().getItemInMainHand();
        }else {
            weapon = itemWeapon;
        }

        itemMeta = weapon.getItemMeta();
        if (itemMeta == null) return;

        s = "ARMA\n" +
            "Rango máximo: " + Math.round(MAX_DISTANCE) + "\n" +
            "Candencia: ? \n \n";

        if (charger != null){
            if (charger.getItemMeta() != null) {
                String chargerType = (String) GlobalUtils.getPersistenData(charger, "chargerType", PersistentDataType.STRING);
                GunsSection.dataChargers.get(ListCharger.valueOf(chargerType)).getProperties(charger, true);
            }
        }
        if (weapon.getItemMeta() != null){
            String chargerNow = (String) GlobalUtils.getPersistenData(weapon, "chargerTypeNow", PersistentDataType.STRING);
            if (chargerNow != null && !chargerNow.equals("null")){
                s += GunsSection.dataChargers.get(ListCharger.valueOf(chargerNow)).getProperties(weapon, false);
            }else{
                s += "SIN CARGADOR";
            }
        }

        itemMeta.setLore(GlobalUtils.StringToLoreString(s, true));
        weapon.setItemMeta(itemMeta);
    }

    private void drawParticleLine(Location start, Location end, Color color, boolean impacted, double density) {
        World world = start.getWorld();
        if (world == null || !world.equals(end.getWorld())) {
            return;
        }

        Vector direction = end.toVector().subtract(start.toVector()).normalize();
        double distance = start.distance(end);

        Particle.DustOptions dustOptions = new Particle.DustOptions(color, 1);
        Location point = start;
        for (double d = 0; d < distance; d += density) {
            point = start.clone().add(direction.clone().multiply(d));
            world.spawnParticle(Particle.DUST, point, 1, dustOptions);
        }
        if (impacted)world.spawnParticle(Particle.CRIT, point, 5, 0.1, 0.1, 0.1, 0.1);
    }

    private Location getPlayerLookLocation(Player player, double maxDistance, double stepSize) {

        Location eyeLocation = player.getEyeLocation();
        Vector direction = eyeLocation.getDirection().normalize();

        Location currentLocation = eyeLocation.clone();
        if (stepSize <= 0) throw new IllegalArgumentException("No puede ser menor que cero");
        for (double i = 0; i < maxDistance; i += stepSize) {
            currentLocation.add(direction.clone().multiply(stepSize));

            if (currentLocation.getBlock().getType().isSolid()) {
                return currentLocation;
            }
        }

        return eyeLocation.add(direction.multiply(maxDistance));
    }

    public abstract void onShoot(DataShoot dataShoot);

    public abstract void onReloading(Player player);

}
