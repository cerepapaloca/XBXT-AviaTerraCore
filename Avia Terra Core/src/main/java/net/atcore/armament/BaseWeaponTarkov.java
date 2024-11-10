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

    protected BaseWeaponTarkov(String name, List<ListCharger> listChargers, int maxDistance, String displayName, double precision) {
        super(displayName, new ItemStack(Material.IRON_HORSE_ARMOR), maxDistance, name, precision);
        this.CHARGERS_TYPE = listChargers;
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

    private final List<ListCharger> CHARGERS_TYPE;
    public static final HashMap<UUID, BukkitTask> inReload = new HashMap<>();

    @Override
    public void shoot(Player player) {
        if (inReload.containsKey(player.getUniqueId())) return;
        ItemStack itemWeapon = player.getInventory().getItemInMainHand();
        String chargerName = (String) GlobalUtils.getPersistenData(itemWeapon, "chargerTypeInside", PersistentDataType.STRING);
        BaseCharger baseCharger = ArmamentUtils.getCharger(chargerName);
        if (baseCharger != null){
            String stringAmmo = (String) GlobalUtils.getPersistenData(itemWeapon, "chargerAmmo", PersistentDataType.STRING);
            if (stringAmmo != null) {
                List<String> listAmmo = ArmamentUtils.stringToList(stringAmmo);
                if (!listAmmo.isEmpty()){//se vació el cargador
                    BaseAmmo ammon = ArmamentUtils.getAmmo(listAmmo.getFirst());
                    if (ammon != null) {
                        DataShoot dataShoot = executeShoot(player, ammon, baseCharger);
                        if (dataShoot.isCancelled()) return;
                        listAmmo.removeFirst();//se elimina la bala del cargador
                        GlobalUtils.setPersistentDataItem(itemWeapon, "chargerAmmo", PersistentDataType.STRING, ArmamentUtils.listToString(listAmmo));//guarda la munición actual
                        updateLore(itemWeapon, null);
                        baseCharger.onShoot(dataShoot);
                        ammon.onShoot(dataShoot);
                        player.getWorld().playSound(player.getLocation(),Sound.BLOCK_NETHERITE_BLOCK_HIT, SoundCategory.PLAYERS, 1, 1.3f);
                    }
                }else {
                    updateLore(itemWeapon, null);
                    player.sendTitle("", ChatColor.RED + "sin munición", 0, 10, 30);
                }
            }
        }else {
            player.sendTitle("", ChatColor.RED + "sin cargador", 0, 10, 30);
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
            boolean hasCharger = !chargerNameInside.equals("null");//tiene un cargador el arma
            BaseCharger baseCharger = ArmamentUtils.getCharger(hasCharger ? chargerNameInside : chargerNameExternal);//el cargador interno-
            // -puede ser el mismo cargador o el externo en caso de que no tenga uno asignado
            if (baseCharger == null) throw new IllegalArgumentException("baseCharger dio nulo cuando debe ser imposible");//creo que nunca va a suceder o eso creo
            BaseCharger baseChargerInside = ArmamentUtils.getCharger(chargerNameInside);
            boolean b;
            if (!ammoWeapon.isEmpty() && baseChargerInside != null){//el cargador del arma está vacío?
                b = baseChargerInside.getAmmoMax() >= baseCharger.getAmmoMax();//obtiene cuál es el cargador más grande
                if (b){//en caso de que sea el más grande
                    baseCharger = baseChargerInside;//se usa el cargador interno para calcular la capacidad
                }
            }else{
                b = true;//se usa el cargador externo
            }
            int delta = baseCharger.getAmmoMax() - ammoWeapon.size();//la diferencia de munición entre cantidad de munición actual y la maxima
            String ammo = (String) GlobalUtils.getPersistenData(itemCharger, "chargerAmmo", PersistentDataType.STRING);//se obtiene la munición del cargador
            if (ammo == null) continue;
            List<String> ammoCharger = ArmamentUtils.stringToList(ammo);
            if (ammoCharger.isEmpty())continue;//si el cargador está vacío busca otro cargador
            int result = Math.min(ammoCharger.size(), delta);//la cantidad de balas que se tiene que mover

            for (int i = 0; i < result; i++) {
                ammoWeapon.addFirst(ammoCharger.removeFirst());//mueve las balas de una lista a la otra
            }
            if (!hasCharger) {//si el arma no tiene cargador lo "guarda" dentro del arma
                itemCharger.setAmount(0);//lo que hace es desperecer del mundo
            }
            player.getWorld().playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_IRON, SoundCategory.PLAYERS, 1, 1);
            GlobalUtils.setPersistentDataItem(itemCharger, "chargerAmmo", PersistentDataType.STRING, ArmamentUtils.listToString(ammoCharger));
            GlobalUtils.setPersistentDataItem(itemWeapon, "chargerAmmo", PersistentDataType.STRING, ArmamentUtils.listToString(ammoWeapon));
            GlobalUtils.setPersistentDataItem(itemWeapon, "chargerTypeInside", PersistentDataType.STRING,
                    b ?  baseCharger.getName() : chargerNameExternal);//se decide que cargador se va a usar el mismo o él ultimó que se usó para recargar
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
                maxDistance,
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

        itemMeta.setLore(GlobalUtils.StringToLoreString(MessagesManager.addProprieties(s, null, CategoryMessages.PRIVATE, false), true));
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
                        ItemStack itemCharger = new ItemStack(charger.getItemArmament());
                        String stringAmmo = (String) GlobalUtils.getPersistenData(ItemWeapon, "chargerAmmo", PersistentDataType.STRING);
                        GlobalUtils.setPersistentDataItem(ItemWeapon, "chargerTypeInside", PersistentDataType.STRING, "null");
                        GlobalUtils.setPersistentDataItem(ItemWeapon, "chargerAmmo", PersistentDataType.STRING, "");
                        GlobalUtils.setPersistentDataItem(itemCharger, "chargerAmmo", PersistentDataType.STRING, stringAmmo);
                        GlobalUtils.addProtectionAntiDupe(itemCharger);
                        updateLore(ItemWeapon, itemCharger);
                        player.setItemOnCursor(itemCharger);
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
        return CHARGERS_TYPE.contains(ListCharger.valueOf(charger.getName()));
    }

    public abstract void onReloading(Player player);

}
