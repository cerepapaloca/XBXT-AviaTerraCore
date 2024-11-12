package net.atcore.armament;

import lombok.Getter;
import lombok.Setter;
import net.atcore.AviaTerraCore;
import net.atcore.messages.CategoryMessages;
import net.atcore.messages.MessagesManager;
import net.atcore.messages.TypeMessages;
import net.atcore.utils.GlobalUtils;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@Getter
@Setter
public abstract class BaseCharger extends BaseArmament implements Compartment {

    public BaseCharger(List<ListAmmo> compatibleCaliber, List<ListAmmo> defaultCaliber, int ammoMax, String displayName, int reloadTime) {
        super(displayName, new ItemStack(Material.SUGAR), "charger");
        this.displayName = displayName;
        this.DefaultammonList = listAmmoFill(defaultCaliber);
        this.compatibleAmmonList = listAmmoToBaseAmmo(compatibleCaliber);
        this.ammoMax = ammoMax;
        this.reloadTime = reloadTime;
        List<String> listAmmoName = new ArrayList<>();
        for (BaseAmmo ammo : listAmmoFill(defaultCaliber)) listAmmoName.add(ammo.getName());
        GlobalUtils.setPersistentDataItem(itemArmament, "chargerAmmo", PersistentDataType.STRING, ArmamentUtils.listToString(listAmmoName));
        getProperties(itemArmament, true);
    }

    private final String displayName;
    private final List<BaseAmmo> DefaultammonList;
    private final List<BaseAmmo> compatibleAmmonList;
    private final int ammoMax;
    private final int reloadTime;//en TICK


    public String getProperties(ItemStack item, boolean setLore){
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return "?";
        String stringAmmo = (String) GlobalUtils.getPersistenData(item, "chargerAmmo", PersistentDataType.STRING);
        List<BaseAmmo> AmmoBaseList = new ArrayList<>();
        if (stringAmmo != null) {
            for (String ammoName : ArmamentUtils.stringToList(stringAmmo)) AmmoBaseList.add(ArmamentUtils.getAmmo(ammoName));
            int amountAmmo = AmmoBaseList.size();
            String loreCargador = String.format("""
                    %s
                    CARGADOR
                    Nombre <|%s|>
                    Municion <|%s|>
                    Munición maxima <|%s|>
                    Velocidad de recarga <|%ss|>
                    """,
                    setLore ? "" : " \n",
                    displayName,
                    amountAmmo,
                    ammoMax,
                    (reloadTime/20)
            );
            StringBuilder loreAmmo = new StringBuilder();
            if (!AmmoBaseList.isEmpty()) {
                Set<BaseAmmo> uniqueAmmo = new HashSet<>(AmmoBaseList);
                for(BaseAmmo ammo : uniqueAmmo){
                    if (ammo == null) continue;
                    loreAmmo.append(ammo.getProperties());
                }
            }

            String finalLore = loreCargador + (amountAmmo > 0 ? loreAmmo.toString() : "");
            if (setLore){
                meta.setLore(GlobalUtils.StringToLoreString(MessagesManager.addProprieties(finalLore, null, CategoryMessages.PRIVATE, false), true));
                item.setItemMeta(meta);
            }
            return finalLore;
        }
        return "?";
    }

    private static HashMap<UUID, BukkitTask> reloadTask = new HashMap<>();

    @Override
    public void reload(Player player){
        ItemStack charger = player.getInventory().getItemInMainHand();
        if (charger.getItemMeta() == null) return;
        if (reloadTask.containsKey(player.getUniqueId())) return;
        boolean b = false;
        for (ItemStack item : player.getInventory().getContents()) {//busca si tienes al menos una bala en el inventario
            if (item == null) continue;
            String ammoName = (String) GlobalUtils.getPersistenData(item, "ammoName", PersistentDataType.STRING);
            if (ammoName == null) continue;
            BaseAmmo baseAmmo = ArmamentUtils.getAmmo(ammoName);
            if (baseAmmo == null) continue;
            b = true;//se confirma que tiene al menos un bala
            break;
        }
        if (b){
            BukkitTask task = new BukkitRunnable() {
                public void run() {
                    ItemStack charger = player.getInventory().getItemInMainHand();
                    if (charger.getItemMeta() != null){
                        String ammoNameList = (String) GlobalUtils.getPersistenData(charger, "chargerAmmo", PersistentDataType.STRING);
                        if (ammoNameList != null) {
                            if (ArmamentUtils.stringToList(ammoNameList).size() < ammoMax) {
                                onReload(player);
                            }else {
                                player.sendTitle("", ChatColor.GREEN + "Recargado Completada", 0, 0,30);
                                player.removePotionEffect(PotionEffectType.SLOWNESS);
                                reloadTask.remove(player.getUniqueId());
                                cancel();
                            }
                        }else{
                            cancel();
                        }
                    }else {
                        player.sendTitle("", ChatColor.RED + "Recargado Cancelada", 0, 0,30);
                        player.removePotionEffect(PotionEffectType.SLOWNESS);
                        reloadTask.remove(player.getUniqueId());
                        cancel();
                    }
                }
            }.runTaskTimer(AviaTerraCore.getInstance(), 3, 3);
            MessagesManager.sendTitle(player,"", "Recargando...", 0, 0,30, TypeMessages.INFO);
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, Integer.MAX_VALUE, 3, true, false, false));
            reloadTask.put(player.getUniqueId(), task);
        }else {
            MessagesManager.sendTitle(player,"", "No tiene balas en el inventario", 0, 0,30, TypeMessages.ERROR);
        }
    }

    private void onReload(@NotNull Player player){
        PlayerInventory inv = player.getInventory();
        ItemStack ItemCharger = inv.getItemInMainHand();
        String s = (String) GlobalUtils.getPersistenData(ItemCharger,"weaponName", PersistentDataType.STRING);
        if (ItemCharger.getItemMeta() != null && s == null) {//se mira que no sea un arma o que sea aire
            for (ItemStack ItemAmmo : inv.getStorageContents()) {
                if (ItemAmmo == null) continue;
                if (ItemAmmo.getItemMeta() == null) continue;
                String ammoName = (String) GlobalUtils.getPersistenData(ItemAmmo, "ammoName", PersistentDataType.STRING);
                BaseAmmo baseAmmo = ArmamentUtils.getAmmo(ammoName);
                if (baseAmmo == null) continue;
                if (!compatibleAmmonList.contains(baseAmmo)) continue;
                String ammoNameList = (String) GlobalUtils.getPersistenData(ItemCharger, "chargerAmmo", PersistentDataType.STRING);
                if (ammoNameList == null) continue;
                List<String> ammoList = ArmamentUtils.stringToList(ammoNameList);
                ammoList.add(baseAmmo.getName());
                ItemAmmo.setAmount(ItemAmmo.getAmount() - 1);
                GlobalUtils.setPersistentDataItem(ItemCharger, "chargerAmmo", PersistentDataType.STRING, ArmamentUtils.listToString(ammoList));
                getProperties(ItemCharger, true);
                return;
            }
            reloadTask.get(player.getUniqueId()).cancel();
            player.sendTitle("", ChatColor.GREEN + "Recargado Completada", 0, 0,30);
            player.removePotionEffect(PotionEffectType.SLOWNESS);
            reloadTask.remove(player.getUniqueId());
            player.getWorld().playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_IRON, SoundCategory.PLAYERS, 1, 1);
        }else{
            reloadTask.get(player.getUniqueId()).cancel();
            player.sendTitle("", ChatColor.RED + "Recargado Cancelada", 0, 0,30);
            player.removePotionEffect(PotionEffectType.SLOWNESS);
            reloadTask.remove(player.getUniqueId());
        }
    }

    @Override
    public boolean outCompartment(Player player, ItemStack ItemCharger){
        if (ItemCharger != null && itemArmament.getItemMeta() != null){
            BaseCharger baseCharger = ArmamentUtils.getCharger(ItemCharger);
            if (baseCharger != null) {
                String ammonName = (String) GlobalUtils.getPersistenData(ItemCharger, "chargerName", PersistentDataType.STRING);
                if (ammonName != null) {
                    String stringAmmo = (String) GlobalUtils.getPersistenData(ItemCharger, "chargerAmmo", PersistentDataType.STRING);
                    GlobalUtils.setPersistentDataItem(ItemCharger, "chargerAmmo", PersistentDataType.STRING, "");
                    if (stringAmmo == null)return false;
                    for (String name : ArmamentUtils.stringToList(stringAmmo)){
                        BaseAmmo baseAmmo = ArmamentUtils.getAmmo(name);
                        if (baseAmmo == null) continue;
                        GlobalUtils.addItemPlayer(baseAmmo.getItemArmament(), player, true, false);
                    }
                    baseCharger.getProperties(ItemCharger, true);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void updateLore(ItemStack itemStack, ItemStack Auxiliar){
        getProperties(itemArmament, true);
    }

    private List<BaseAmmo> listAmmoToBaseAmmo(List<ListAmmo> baseAmmoList){
        List<BaseAmmo> listAmmo = new ArrayList<>();
        for (ListAmmo ammo : baseAmmoList) listAmmo.add(ammo.getAmmo());
        return listAmmo;
    }
    
    private List<BaseAmmo> listAmmoFill(List<ListAmmo> ammoList){
        List<ListAmmo> listAmmonFill = new ArrayList<>();
        for (int i = 0; i < ammoMax; i++) {
            listAmmonFill.add(ammoList.get(i % ammoList.size()));
        }
        return listAmmoToBaseAmmo(listAmmonFill);
    }

    public abstract void onShoot(DataShoot dataShoot);
}
