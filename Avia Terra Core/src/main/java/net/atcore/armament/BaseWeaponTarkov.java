package net.atcore.armament;

import lombok.Getter;
import lombok.Setter;
import net.atcore.AviaTerraCore;
import net.atcore.messages.Message;
import net.atcore.messages.MessagesManager;
import net.atcore.messages.MessagesType;
import net.atcore.utils.GlobalUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.entity.Player;
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

    protected BaseWeaponTarkov(List<Class<? extends BaseMagazine>> listMagazines,
                               int maxDistance,
                               String displayName,
                               double vague,
                               WeaponMode mode,
                               int cadence
    ) {
        super(new ItemStack(Material.IRON_HORSE_ARMOR), maxDistance, displayName, vague, mode, cadence);
        this.magazineList = listMagazines;
        GlobalUtils.setPersistentData(itemArmament, "magazineNameInside", PersistentDataType.STRING, "null");
        updateLore(null, null);
    }

    private final List<Class<? extends BaseMagazine>> magazineList;
    public static final HashMap<UUID, BukkitTask> IN_RELOAD = new HashMap<>();

    @Override
    public void processShoot(Player player) {
        if (IN_RELOAD.containsKey(player.getUniqueId())) return;
        ItemStack itemWeapon = player.getInventory().getItemInMainHand();
        String magazineName = (String) GlobalUtils.getPersistenData(itemWeapon, "magazineNameInside", PersistentDataType.STRING);
        BaseMagazine baseMagazine = ArmamentUtils.getMagazine(magazineName);
        if (baseMagazine != null){
            String stringAmmo = (String) GlobalUtils.getPersistenData(itemWeapon, "magazineAmmo", PersistentDataType.STRING);
            if (stringAmmo != null) {
                List<String> listAmmo = ArmamentUtils.stringToList(stringAmmo);
                if (!listAmmo.isEmpty()){//se vació el cargador
                    BaseAmmo ammon = ArmamentUtils.getAmmo(listAmmo.getFirst());
                    if (ammon != null) {
                        processRayShoot(player, ammon, baseMagazine);
                        listAmmo.removeFirst();//se elimina la bala del cargador
                        GlobalUtils.setPersistentData(itemWeapon, "magazineAmmo", PersistentDataType.STRING, ArmamentUtils.listToString(listAmmo));//guarda la munición actual
                        updateLore(itemWeapon, null);
                        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_SNOW_GOLEM_SHOOT, SoundCategory.PLAYERS, 1.1f, 0.8f);
                    }
                }else {
                    updateLore(itemWeapon, null);
                    MessagesManager.sendTitle(player, "", "sin munición", 0, 10, 30, MessagesType.ERROR);
                }
            }
        }else {
            MessagesManager.sendTitle(player, "", "sin cargador", 0, 10, 30, MessagesType.ERROR);
        }
    }


    @Override
    public boolean reload(Player player) {
        ItemStack itemWeapon = player.getInventory().getItemInMainHand();
        if (itemWeapon.getItemMeta() == null) return false;//por si acasó
        String magazineName = (String) GlobalUtils.getPersistenData(itemWeapon, "magazineNameInside", PersistentDataType.STRING);
        BaseMagazine charger = ArmamentUtils.getMagazine(magazineName);
        boolean b = false;//el jugador tiene un cargador con que cambiar o no
        for (ItemStack itemCharger : player.getInventory().getStorageContents()){// Busca un cargador con balas
            if (itemCharger == null) continue;
            String ammo = (String) GlobalUtils.getPersistenData(itemCharger, "magazineAmmo", PersistentDataType.STRING);//se obtiene la munición del cargador
            if (ammo == null) continue;
            List<String> ammoCharger = ArmamentUtils.stringToList(ammo);
            if (ammoCharger.isEmpty() || itemCharger.equals(itemWeapon))continue;// Si el cargador está vacío busca otro cargador o el cargador es la propia arma
            b = true;//sí hay un cargador
            break;
        }

        if (charger != null){// ¿Él arma tiene un cargador?
            if (b){// ¿Hay un cargador que se puede cambiar?
                if (IN_RELOAD.containsKey(player.getUniqueId())) return false;
                UUID uuid = player.getUniqueId();
                BukkitTask bukkitTask = new BukkitRunnable() {// Comienza el delay de la recarga
                    public void run() {
                        Player player = Bukkit.getPlayer(uuid);
                        IN_RELOAD.remove(uuid);
                        if (player == null) return;
                        ItemStack item = player.getInventory().getItemInMainHand();
                        if (item.getItemMeta() != null) onReload(item, player);
                    }
                }.runTaskLater(AviaTerraCore.getInstance(), charger.getReloadTime());
                new BukkitRunnable() {// Esto es para asegurar que el jugador tiene el amra en la mano
                    public void run() {
                        Player player = Bukkit.getPlayer(uuid);
                        if (player == null) return;
                        ItemStack item = player.getInventory().getItemInMainHand();
                        if (item.getItemMeta() != null){
                            String name = (String) GlobalUtils.getPersistenData(item, "weaponName", PersistentDataType.STRING);
                            if (name == null) {
                                cancel();

                            }
                        }else {
                            cancel();
                        }
                    }
                    @Override
                    public void cancel() {
                        super.cancel();
                        bukkitTask.cancel();
                        MessagesManager.sendTitle(player,"", "Recarga Cancelada", 10, charger.getReloadTime(),10, MessagesType.ERROR);
                        IN_RELOAD.remove(uuid);
                    }
                }.runTaskTimer(AviaTerraCore.getInstance(), 0, 1);
                MessagesManager.sendTitle(player,"", "Recargado...", 10, charger.getReloadTime(),10, MessagesType.INFO);
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, charger.getReloadTime(), 2, true, false, false));
                IN_RELOAD.put(player.getUniqueId(), bukkitTask);// Por si se guarda el delay por si se tiene que cancelar
                return true;
            }else {
                MessagesManager.sendTitle(player, "", "No tienes cargadores con munición", 0,20,60, MessagesType.ERROR);
            }
        }else {//en caso de que el arma no tiene cargador, cargaría de manera instantánea
            if (b){//hay un cargador que se puede cambiar?
                onReload(itemWeapon, player);
                MessagesManager.sendTitle(player,"", "Recargado", 0, 0,30, MessagesType.SUCCESS);
                return true;
            }else {
                MessagesManager.sendTitle(player, "", "No tienes cargadores con munición", 0,20,60, MessagesType.ERROR);
            }
        }
        return false;
    }

    private void onReload(ItemStack itemWeapon ,Player player) {
        String ammoInside = (String) GlobalUtils.getPersistenData(itemWeapon, "magazineAmmo", PersistentDataType.STRING);
        List<String> ammoWeapon;
        if (ammoInside != null) {
            ammoWeapon = ArmamentUtils.stringToList(ammoInside);//si no,lo obtiene la lista
        }else{
            ammoWeapon = new ArrayList<>();//crea una lista nueva por si da nulo
        }

        for (ItemStack itemCharger : player.getInventory().getStorageContents()) {
            //realiza todas las comprobaciones
            if (itemCharger == null) continue;

            String magazineNameExternal = (String) GlobalUtils.getPersistenData(itemCharger, "magazineName", PersistentDataType.STRING);
            if (magazineNameExternal == null) continue;

            String magazineNameInside = (String) GlobalUtils.getPersistenData(itemWeapon, "magazineNameInside", PersistentDataType.STRING);
            if (magazineNameInside == null) continue;

            if (!isCompatible(magazineNameExternal))continue;//es un cargador compatible?
            boolean hasCharger = !magazineNameInside.equals("null");//tiene un cargador el arma

            BaseMagazine baseMagazineExternal = ArmamentUtils.getMagazine(magazineNameExternal);
            if (baseMagazineExternal == null) continue;

            BaseMagazine baseMagazine = ArmamentUtils.getMagazine(hasCharger ? magazineNameInside : magazineNameExternal);
            if (baseMagazine == null) throw new IllegalArgumentException("baseCharger dio nulo cuando debe ser imposible");//creo que nunca va a suceder o eso creo
            String ammo = (String) GlobalUtils.getPersistenData(itemCharger, "magazineAmmo", PersistentDataType.STRING);//se obtiene la munición del cargador
            if (ammo == null) continue;

            List<String> ammoMagazine = ArmamentUtils.stringToList(ammo);
            if (ammoMagazine.isEmpty())continue;//si el cargador está vacío busca otro cargador

            itemCharger.setAmount(0);//lo que hace es desperecer del mundo
            ItemStack itemSwapMagazine = new ItemStack(baseMagazine.getItemArmament());

            GlobalUtils.setPersistentData(itemSwapMagazine, "magazineAmmo", PersistentDataType.STRING, ArmamentUtils.listToString(ammoWeapon));
            GlobalUtils.setPersistentData(itemWeapon, "magazineAmmo", PersistentDataType.STRING, ArmamentUtils.listToString(ammoMagazine));
            GlobalUtils.setPersistentData(itemWeapon, "magazineNameInside", PersistentDataType.STRING, magazineNameExternal);

            updateLore(itemWeapon, hasCharger ? itemSwapMagazine : itemCharger);
            if (hasCharger){
                GlobalUtils.addItemPlayer(itemSwapMagazine, player, true, true, false);
            }
            MessagesManager.sendTitle(player,"", "Recargado", 0, 0,30, MessagesType.SUCCESS);
            player.getWorld().playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_IRON, SoundCategory.PLAYERS, 1, 1);
            break;
        }
    }

    @Override
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
                Rango máximo: <|%sm|>
                Presión: <|%s|>
                """,
                maxDistance,
                (100 - vague) + "%"
        );
        if (charger != null){//en caso de que tenga un cargador
            if (charger.getItemMeta() != null) {
                String magazineName = (String) GlobalUtils.getPersistenData(charger, "magazineName", PersistentDataType.STRING);
                Objects.requireNonNull(ArmamentUtils.getMagazine(magazineName)).getProperties(charger, true);//se le asigna el lore al cargador
            }
        }
        if (weapon.getItemMeta() != null){//También se le asigna el lore al arma
            String chargerNow = (String) GlobalUtils.getPersistenData(weapon, "magazineNameInside", PersistentDataType.STRING);
            if (chargerNow != null && !chargerNow.equals("null")){//es técnicamente imposible que diera nulo
                s += Objects.requireNonNull(ArmamentUtils.getMagazine(chargerNow)).getProperties(weapon, false);//solo se obtiene el lore del cargador
            }else{
                s += """
                         \n
                        SIN CARGADOR""";
            }
        }
        List<Component> lore = GlobalUtils.stringToLoreComponent(s, true);
        lore.addAll(GlobalUtils.stringToLoreComponent(Message.MISC_WARING_ANTI_DUPE.getMessageLocateDefault(), false, MessagesType.WARNING.getMainColor()));
        itemMeta.lore(lore);
        weapon.setItemMeta(itemMeta);
    }

    public static boolean checkReload(Player player){
        if (BaseWeaponTarkov.IN_RELOAD.containsKey(player.getUniqueId())){
            BaseWeaponTarkov.IN_RELOAD.remove(player.getUniqueId()).cancel();
            MessagesManager.sendTitle(player, "", "Recarga Cancelada", 0, 20, 40, MessagesType.ERROR);
            player.removePotionEffect(PotionEffectType.SLOWNESS);
            return true;
        }
        return false;
    }

    @Override
    public boolean outCompartment(Player player, ItemStack itemWeapon){
        if (itemWeapon != null && itemWeapon.getItemMeta() != null){
            BaseWeapon baseWeapon = ArmamentUtils.getWeapon(itemWeapon);
            if (baseWeapon != null) {
                String magazineNameInside = (String) GlobalUtils.getPersistenData(itemWeapon, "magazineNameInside", PersistentDataType.STRING);
                if (magazineNameInside != null && !magazineNameInside.equals("null")) {
                    BaseMagazine charger = ArmamentUtils.getMagazine(magazineNameInside);
                    if (charger != null) {
                        ItemStack itemCharger = new ItemStack(charger.getItemArmament());
                        String stringAmmo = (String) GlobalUtils.getPersistenData(itemWeapon, "magazineAmmo", PersistentDataType.STRING);
                        GlobalUtils.setPersistentData(itemWeapon, "magazineNameInside", PersistentDataType.STRING, "null");
                        GlobalUtils.setPersistentData(itemWeapon, "magazineAmmo", PersistentDataType.STRING, "");
                        GlobalUtils.setPersistentData(itemCharger, "magazineAmmo", PersistentDataType.STRING, stringAmmo);
                        GlobalUtils.addProtectionAntiDupe(itemCharger, false);
                        updateLore(itemWeapon, itemCharger);
                        player.setItemOnCursor(itemCharger);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean isCompatible(String s){
        BaseMagazine charger = ArmamentUtils.getMagazine(s);
        if (charger == null) return false;
        return magazineList.contains(charger.getClass());
    }
}
