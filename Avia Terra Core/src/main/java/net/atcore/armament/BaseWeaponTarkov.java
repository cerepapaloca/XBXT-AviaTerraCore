package net.atcore.armament;

import lombok.Getter;
import lombok.Setter;
import net.atcore.AviaTerraCore;
import net.atcore.messages.CategoryMessages;
import net.atcore.messages.MessagesManager;
import net.atcore.utils.GlobalUtils;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

/**
 * Las propiedades comunes de las armas todas las armas tiene que extender de esta clase
 */

@Getter
@Setter
public abstract class BaseWeaponTarkov extends BaseWeapon implements Compartment {

    protected BaseWeaponTarkov(ListWeaponTarvok type, List<ListCharger> listChargers, int maxDistance, String displayName, double precision) {
        super(displayName, new ItemStack(Material.IRON_HORSE_ARMOR), maxDistance, type.name(), precision);
        this.CHARGERS_TYPE = listChargers;
        this.weaponType = type;
        GlobalUtils.setPersistentDataItem(itemArmament, "chargerTypeInside", PersistentDataType.STRING, "null");
        GlobalUtils.setPersistentDataItem(itemArmament, "chargerTypeOutside", PersistentDataType.STRING, "null");
        ItemMeta meta = itemArmament.getItemMeta();
        if (meta == null) return;
        meta.setDisplayName(displayName);
        meta.removeItemFlags(ItemFlag.HIDE_ATTRIBUTES);//se oculta datos del item para que no se vea feo
        meta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.removeItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        meta.removeItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        itemArmament.setItemMeta(meta);
        updateLore(null, null);
    }

    protected BaseWeaponTarkov(ListWeaponTarvok type, ListCharger chargerTypes, int maxDistance, String displayName, double precision) {
        this(type, List.of(chargerTypes), maxDistance, displayName, precision);
    }


    private final List<ListCharger> CHARGERS_TYPE;
    private final ListWeaponTarvok weaponType;
    public static final HashMap<UUID, BukkitTask> inReload = new HashMap<>();

    @Override
    public void shoot(Player player) {
        if (inReload.containsKey(player.getUniqueId())) return;
        ItemStack itemWeapon = player.getInventory().getItemInMainHand();
        String chargerName = (String) GlobalUtils.getPersistenData(itemWeapon, "chargerTypeInside", PersistentDataType.STRING);
        BaseCharger baseCharger = ArmamentUtils.getCharger(chargerName);
        if (baseCharger == null) return;
        String stringAmmo = (String) GlobalUtils.getPersistenData(itemWeapon, "chargerAmmo", PersistentDataType.STRING);
        if (stringAmmo != null) {
            List<String> listAmmo = ArmamentUtils.stringToList(stringAmmo);
            if (listAmmo.isEmpty()){//se vació el cargador
                updateLore(itemWeapon, null);
                return;
            }
            BaseAmmo ammon = ArmamentUtils.getAmmo(listAmmo.getFirst());
            if (ammon == null) return;
            listAmmo.removeFirst();//se elimina la bala del cargador
            GlobalUtils.setPersistentDataItem(itemWeapon, "chargerAmmo", PersistentDataType.STRING, ArmamentUtils.listToString(listAmmo));//guarda la munición actual
            updateLore(itemWeapon, null);
            /*
            Vector direction = player.getLocation().getDirection();
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
            BlockIterator blockIterator = new BlockIterator(
                    player.getWorld(),
                    player.getEyeLocation().toVector(),
                    direction,
                    0,
                    (int) distance
            );
            while (blockIterator.hasNext() ) {
                Block block = blockIterator.next();
                f += block.getType().getHardness();
                Bukkit.getLogger().warning(block.getType().name() + " | " + block.getType().getHardness());
                if (f < ammon.getPenetration() && block.getType() != Material.AIR) {
                    lastBlock = block;
                }
            }
            boolean b = false;
            if (ammon == null) throw new IllegalArgumentException("Las balas dio nulo!?");
            if (result != null) {
                Entity entity = result.getHitEntity();
                distance = player.getEyeLocation().distance(result.getHitPosition().toLocation(player.getWorld()));
                if (entity != null) {
                    if (!(entity instanceof Player)) {
                        if (entity instanceof LivingEntity livingEntity) {//se tiene que hacer instance por qué no la variable entity no sirve en este caso
                            DataShoot dataShoot = new DataShoot(livingEntity, player, this, baseCharger, ammon, distance, ammon.getDamage());//se crea los datos del disparo
                            if (f < ammon.getPenetration()){
                                onShoot(dataShoot);
                                baseCharger.onShoot(dataShoot);
                                ammon.onShoot(dataShoot);
                                livingEntity.damage(dataShoot.getDamage());//se aplica el daño
                                b = true;
                            }
                        }
                    }
                }
            }
            Location finalLocation =  ArmamentUtils.getPlayerLookLocation(directionRandom, player, distance, 0.25);
            if (lastBlock != null) {
                finalLocation = lastBlock.getLocation();
            }
            ArmamentUtils.drawParticleLine(player.getEyeLocation(),finalLocation,
                    ammon.getColor(), b, ammon.getDensityTrace());
             */
            DataShoot dataShoot = executeShoot(player, ammon, baseCharger);
            onShoot(dataShoot);
            baseCharger.onShoot(dataShoot);
            ammon.onShoot(dataShoot);
        }
    }


    @Override
    public void reload(Player player) {
        ItemStack itemWeapon = player.getInventory().getItemInMainHand();
        if (itemWeapon.getItemMeta() == null) return;
        String chargerName = (String) GlobalUtils.getPersistenData(itemWeapon, "chargerTypeInside", PersistentDataType.STRING);
        BaseCharger charger = ArmamentUtils.getCharger(chargerName);
        if (charger != null){
            if (inReload.containsKey(player.getUniqueId())) return;
            BukkitTask bukkitTask = new BukkitRunnable() {
                @Override
                public void run() {
                    if (player.isOnline() && itemWeapon.getItemMeta() != null) onReload(itemWeapon, player);
                    inReload.remove(player.getUniqueId());
                }
            }.runTaskLater(AviaTerraCore.getInstance(), charger.getReloadTime());
            player.sendTitle("", ChatColor.RED + "Recargando...", 0, charger.getReloadTime(),0);
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, charger.getReloadTime(), 2, true, false, false));
            inReload.put(player.getUniqueId(), bukkitTask);
        }else {
            onReload(itemWeapon, player);
            player.sendTitle("", ChatColor.RED + "Recargado", 0, 0,30);
        }
    }

    private void onReload(ItemStack itemWeapon ,Player player) {
        onReloading(player);
        String ammoInside = (String) GlobalUtils.getPersistenData(itemWeapon, "chargerAmmo", PersistentDataType.STRING);
        List<String> ammoWeapon;
        if (ammoInside != null) {
            ammoWeapon = ArmamentUtils.stringToList(ammoInside);//si no,lo obtiene la lista
        }else{
            ammoWeapon = new ArrayList<>();//crea una lista nueva por si da nulo
        }

        for (ItemStack itemCharger : player.getInventory().getStorageContents()) {
            //realiza todas las comprobaciones
            if (itemCharger == null) continue;
            String chargerNameExternal = (String) GlobalUtils.getPersistenData(itemCharger, "chargerType", PersistentDataType.STRING);
            String chargerNameInside = (String) GlobalUtils.getPersistenData(itemWeapon, "chargerTypeInside", PersistentDataType.STRING);
            if (chargerNameExternal == null) continue;
            if (chargerNameInside == null) continue;
            if (!isCompatible(chargerNameExternal))continue;//es un cargador compatible?
            boolean hasCharger = !chargerNameInside.equals("null");
            BaseCharger baseCharger = ArmamentUtils.getCharger(hasCharger ? chargerNameInside : chargerNameExternal);//el cargador interno-
            if (baseCharger == null) throw new IllegalArgumentException("baseCharger dio nulo cuando debe ser imposible");//creo que nunca va a suceder o eso creo
            // -puede ser el mismo cargador o el externo en caso de que no tenga uno asignado
            BaseCharger baseChargerInside = ArmamentUtils.getCharger(chargerNameInside);
            boolean b;
            if (!ammoWeapon.isEmpty() && baseChargerInside != null){//es cargador está vacío?
                b = baseChargerInside.getAmmoMax() >= baseCharger.getAmmoMax();//obtiene cuál es el cargador más grande
                if (b){//en caso de que sea el más grnade
                    baseCharger = baseChargerInside;//se usa el cargador interno para calcular la capacidad
                }
            }else{
                b = true;//se usa el cargador externo
            }
            int delta = baseCharger.getAmmoMax() - ammoWeapon.size();//la diferencia de munición entre cantidad de munición actual y la maxima
            String ammo = (String) GlobalUtils.getPersistenData(itemCharger, "chargerAmmo", PersistentDataType.STRING);
            if (ammo == null) continue;
            List<String> ammoCharger = ArmamentUtils.stringToList(ammo);
            int result = Math.min(ammoCharger.size(), delta);//la cantidad de balas que se tiene que mover

            for (int i = 0; i < result; i++) {
                ammoWeapon.addFirst(ammoCharger.removeFirst());//mueve las balas de una lista a la otra
            }
            if (!hasCharger) {//si el cargador está vacío se elimina del mundo
                itemCharger.setAmount(0);
            }
            GlobalUtils.setPersistentDataItem(itemCharger, "chargerAmmo", PersistentDataType.STRING, ArmamentUtils.listToString(ammoCharger));
            GlobalUtils.setPersistentDataItem(itemWeapon, "chargerAmmo", PersistentDataType.STRING, ArmamentUtils.listToString(ammoWeapon));
            GlobalUtils.setPersistentDataItem(itemWeapon, "chargerTypeInside", PersistentDataType.STRING,
                    b ?  baseCharger.getChargerType().name() : chargerNameExternal);//se decide que cargador se va a usar el mismo o él ultimó que se usó para recargar
            updateLore(itemWeapon, itemCharger);
            break;
        }
    }

    public void updateLore(ItemStack weapon, ItemStack charger) {
        String s;
        ItemMeta itemMeta;
        if (weapon == null){
            weapon = itemArmament;
        }

        itemMeta = weapon.getItemMeta();
        if (itemMeta == null) return;
        //si o si tiene que tener este lore el arma
        s = String.format("""
                ARMA
                Rango máximo: <|%s|>m
                Presión: <|%s|>
                """,
                Math.round(maxDistance),
                (100 - precision) + "%"
        );
        if (charger != null){//en caso de que tenga un cargador
            if (charger.getItemMeta() != null) {
                String chargerType = (String) GlobalUtils.getPersistenData(charger, "chargerType", PersistentDataType.STRING);
                Objects.requireNonNull(ArmamentUtils.getCharger(chargerType)).getProperties(charger, true);//se le asigna el lore al cargador
            }
        }
        if (weapon.getItemMeta() != null){//También se le asigna el lore al arma
            String chargerNow = (String) GlobalUtils.getPersistenData(weapon, "chargerTypeInside", PersistentDataType.STRING);
            if (chargerNow != null && !chargerNow.equals("null")){//es técnicamente imposible que diera nulo
                s += Objects.requireNonNull(ArmamentUtils.getCharger(chargerNow)).getProperties(weapon, false);//solo se obtiene el lore del cargador
            }else{
                s += """
                         \n
                        SIN CARGADOR""";
            }
        }

        itemMeta.setLore(GlobalUtils.StringToLoreString(MessagesManager.addProprieties(s, null, CategoryMessages.PRIVATE), true));
        weapon.setItemMeta(itemMeta);
    }

    public static boolean checkReload(Player player){
        if (BaseWeaponTarkov.inReload.containsKey(player.getUniqueId())){
            BaseWeaponTarkov.inReload.remove(player.getUniqueId()).cancel();
            player.sendTitle("", ChatColor.RED + "Recarga Cancelada", 0, 20, 40);
            player.removePotionEffect(PotionEffectType.SLOWNESS);
            return true;
        }
        return false;
    }

    @Override
    public boolean outCompartment(Player player, ItemStack ItemWeapon){
        if (ItemWeapon != null && ItemWeapon.getItemMeta() != null){
            BaseWeapon baseWeapon = ArmamentUtils.getWeapon(ItemWeapon);
            if (baseWeapon != null) {
                String chargerNameInside = (String) GlobalUtils.getPersistenData(ItemWeapon, "chargerTypeInside", PersistentDataType.STRING);
                if (chargerNameInside != null && !chargerNameInside.equals("null")) {
                    BaseCharger charger = ArmamentUtils.getCharger(chargerNameInside);
                    if (charger != null) {
                        ItemStack itemCarger = new ItemStack(charger.getItemArmament());
                        String stringAmmo = (String) GlobalUtils.getPersistenData(ItemWeapon, "chargerAmmo", PersistentDataType.STRING);
                        GlobalUtils.setPersistentDataItem(ItemWeapon, "chargerTypeInside", PersistentDataType.STRING, "null");
                        GlobalUtils.setPersistentDataItem(ItemWeapon, "chargerAmmo", PersistentDataType.STRING, "");
                        GlobalUtils.setPersistentDataItem(itemCarger, "chargerAmmo", PersistentDataType.STRING, stringAmmo);
                        GlobalUtils.addProtectionAntiDupe(itemCarger);
                        updateLore(ItemWeapon, itemCarger);
                        player.setItemOnCursor(itemCarger);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean isCompatible(String s){
        BaseCharger charger = ArmamentUtils.getCharger(s);
        if (charger == null) return false;
        return CHARGERS_TYPE.contains(charger.getChargerType());
    }

    public abstract void onShoot(DataShoot dataShoot);

    public abstract void onReloading(Player player);

}
