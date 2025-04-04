package net.atcore.armament;

import lombok.Getter;
import lombok.Setter;
import net.atcore.AviaTerraCore;
import net.atcore.messages.Message;
import net.atcore.messages.MessagesManager;
import net.atcore.messages.TypeMessages;
import net.atcore.utils.GlobalUtils;
import net.kyori.adventure.text.Component;
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
public abstract class BaseMagazine extends BaseArmament implements Compartment {

    public BaseMagazine(List<Class<? extends BaseAmmo>> compatibleCaliber, List<Class<? extends BaseAmmo>> defaultCaliber, int ammoMax, String displayName, int reloadTime) {
        super(displayName, new ItemStack(Material.BRICK));
        this.displayName = displayName;
        this.DefaultammonList = listAmmoFill(defaultCaliber);
        this.compatibleAmmonList = listAmmoClassToListBaseAmmo(compatibleCaliber);
        this.ammoMax = ammoMax;
        this.reloadTime = reloadTime;
        List<String> listAmmoName = new ArrayList<>();
        for (BaseAmmo ammo : listAmmoFill(defaultCaliber)) listAmmoName.add(ammo.getName());
        GlobalUtils.setPersistentData(itemArmament, "magazineAmmo", PersistentDataType.STRING, ArmamentUtils.listToString(listAmmoName));
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
        String stringAmmo = (String) GlobalUtils.getPersistenData(item, "magazineAmmo", PersistentDataType.STRING);
        List<BaseAmmo> AmmoBaseList = new ArrayList<>();
        if (stringAmmo != null) {
            for (String armament : ArmamentUtils.stringToList(stringAmmo)) AmmoBaseList.add(ArmamentUtils.getAmmo(armament));
            String finalLore = getFinalLore(setLore, AmmoBaseList);
            if (setLore){
                List<Component> lore = GlobalUtils.stringToLoreComponent(finalLore, true, 1000);
                lore.addAll(GlobalUtils.stringToLoreComponent(Message.MISC_WARING_ANTI_DUPE.getMessageLocaleDefault(), false, TypeMessages.WARNING.getMainColor()));
                meta.lore(lore);
                item.setItemMeta(meta);
            }
            return finalLore;
        }
        return "?";
    }

    private @NotNull String getFinalLore(boolean setLore, List<BaseAmmo> AmmoBaseList) {
        int amountAmmo = AmmoBaseList.size();
        String loreCargador = String.format("""
                %s
                CARGADOR
                Nombre <|%s|>
                Munición <|%s|>
                Munición maxima <|%s|>
                Velocidad de recarga <|%ss|>
                Munición compatible: <|%s|>
                """,
                setLore ? "" : " \n",
                displayName,
                amountAmmo,
                ammoMax,
                (reloadTime/20),
                String.join(", ", this.getCompatibleAmmonList().stream().map(ammo -> ArmamentUtils.getAmmo(ammo.getName()).getDisplayName()).toList())
        );
        StringBuilder loreAmmo = new StringBuilder();
        if (!AmmoBaseList.isEmpty()) {
            Set<BaseAmmo> uniqueAmmo = new HashSet<>(AmmoBaseList);
            for(BaseAmmo ammo : uniqueAmmo){
                if (ammo == null) continue;
                loreAmmo.append(ammo.getProperties());
            }
        }

        return loreCargador + (amountAmmo > 0 ? loreAmmo.toString() : "");
    }

    private static HashMap<UUID, BukkitTask> reloadTask = new HashMap<>();

    @Override
    public boolean reload(Player player){
        ItemStack magazine = player.getInventory().getItemInMainHand();
        if (magazine.getItemMeta() == null) return false;
        String armamentList = (String) GlobalUtils.getPersistenData(magazine, "magazineAmmo", PersistentDataType.STRING);
        String nameMagazine = (String) GlobalUtils.getPersistenData(magazine, "armament", PersistentDataType.STRING);
        if (nameMagazine == null) return false;
        if (armamentList != null && ArmamentUtils.stringToList(armamentList).size() == ammoMax) {
            MessagesManager.sendTitle(player,"", "Cargador lleno", 0, 0,30, TypeMessages.INFO);
            return true;
        }
        if (reloadTask.containsKey(player.getUniqueId())) return false;
        boolean b = false;
        for (ItemStack item : player.getInventory().getContents()) {//busca si tienes al menos una bala en el inventario
            if (item == null) continue;
            String armament = (String) GlobalUtils.getPersistenData(item, "armament", PersistentDataType.STRING);
            if (armament == null) continue;
            BaseAmmo baseAmmo = ArmamentUtils.getAmmo(armament);
            if (baseAmmo == null) continue;
            if (!compatibleAmmonList.contains(baseAmmo)) continue;

            b = true;//se confirma que tiene al menos una bala
            break;
        }
        if (b){

            BukkitTask task = new BukkitRunnable() {

                public void run() {
                    ItemStack magazine = player.getInventory().getItemInMainHand();
                    if (magazine.getItemMeta() != null){
                        String armamentList = (String) GlobalUtils.getPersistenData(magazine, "magazineAmmo", PersistentDataType.STRING);
                        BaseMagazine baseMagazine = ArmamentUtils.getMagazine(magazine);
                        if (baseMagazine != null) {
                            if (nameMagazine.equals(baseMagazine.getName())){
                                if (armamentList != null) {
                                    if (ArmamentUtils.stringToList(armamentList).size() < ammoMax) {
                                        onReload(player);
                                    }else {
                                        MessagesManager.sendTitle(player,"", "Recargado Completada", 0, 0,30, TypeMessages.SUCCESS);
                                        player.removePotionEffect(PotionEffectType.SLOWNESS);
                                        reloadTask.remove(player.getUniqueId());
                                        cancel();
                                    }
                                    return;
                                }
                            }
                        }
                    }
                    MessagesManager.sendTitle(player,"", "Recargado Cancelada", 0, 0,30, TypeMessages.ERROR);
                    player.removePotionEffect(PotionEffectType.SLOWNESS);
                    reloadTask.remove(player.getUniqueId());
                    cancel();
                }
            }.runTaskTimer(AviaTerraCore.getInstance(), 2, 2);
            MessagesManager.sendTitle(player,"", "Recargando...", 0, 0,30, TypeMessages.INFO);
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, Integer.MAX_VALUE, 3, true, false, false));
            reloadTask.put(player.getUniqueId(), task);
        }else {
            MessagesManager.sendTitle(player,"", "No tienes balas en el inventario", 0, 0,80, TypeMessages.ERROR);
        }
        return true;
    }

    private void onReload(@NotNull Player player){
        PlayerInventory inv = player.getInventory();
        ItemStack ItemCharger = inv.getItemInMainHand();
        String s = (String) GlobalUtils.getPersistenData(ItemCharger,"armament", PersistentDataType.STRING);
        if (ItemCharger.getItemMeta() != null && s != null) {//se mira que no sea un arma o que sea aire
            for (ItemStack ItemAmmo : inv.getStorageContents()) {
                if (ItemAmmo == null) continue;
                if (ItemAmmo.getItemMeta() == null) continue;
                BaseMagazine magazine = ArmamentUtils.getMagazine(s);
                if (magazine == null) continue;
                String armament = (String) GlobalUtils.getPersistenData(ItemAmmo, "armament", PersistentDataType.STRING);
                BaseAmmo baseAmmo = ArmamentUtils.getAmmo(armament);
                if (baseAmmo == null) continue;
                if (!compatibleAmmonList.contains(baseAmmo)) continue;
                String armamentList = (String) GlobalUtils.getPersistenData(ItemCharger, "magazineAmmo", PersistentDataType.STRING);
                if (armamentList == null) continue;
                List<String> ammoList = ArmamentUtils.stringToList(armamentList);
                ammoList.add(baseAmmo.getName());
                ItemAmmo.setAmount(ItemAmmo.getAmount() - 1);
                GlobalUtils.setPersistentData(ItemCharger, "magazineAmmo", PersistentDataType.STRING, ArmamentUtils.listToString(ammoList));
                getProperties(ItemCharger, true);
                return;
            }
            reloadTask.get(player.getUniqueId()).cancel();
            MessagesManager.sendTitle(player,"", "Recargado Completada", 0, 0,30, TypeMessages.SUCCESS);
            player.removePotionEffect(PotionEffectType.SLOWNESS);
            reloadTask.remove(player.getUniqueId());
            player.getWorld().playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_IRON, SoundCategory.PLAYERS, 1, 1);
        }else{
            reloadTask.get(player.getUniqueId()).cancel();
            MessagesManager.sendTitle(player,"", "Recargado Cancelada", 0, 0,30, TypeMessages.ERROR);
            player.removePotionEffect(PotionEffectType.SLOWNESS);
            reloadTask.remove(player.getUniqueId());
        }
    }

    @Override
    public boolean outCompartment(Player player, ItemStack ItemCharger){
        if (ItemCharger != null && itemArmament.getItemMeta() != null){
            BaseMagazine baseMagazine = ArmamentUtils.getMagazine(ItemCharger);
            if (baseMagazine != null) {
                String ammonName = (String) GlobalUtils.getPersistenData(ItemCharger, "armament", PersistentDataType.STRING);
                if (ammonName != null) {
                    String stringAmmo = (String) GlobalUtils.getPersistenData(ItemCharger, "magazineAmmo", PersistentDataType.STRING);
                    GlobalUtils.setPersistentData(ItemCharger, "magazineAmmo", PersistentDataType.STRING, "");
                    if (stringAmmo == null)return false;
                    for (String name : ArmamentUtils.stringToList(stringAmmo)){
                        BaseAmmo baseAmmo = ArmamentUtils.getAmmo(name);
                        if (baseAmmo == null) continue;
                        GlobalUtils.addItemPlayer(baseAmmo.getItemArmament(), player, true, false, false);
                    }
                    baseMagazine.getProperties(ItemCharger, true);
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

    private @NotNull List<BaseAmmo> listAmmoClassToListBaseAmmo(@NotNull List<Class<? extends BaseAmmo>> baseAmmoList){
        List<BaseAmmo> listAmmo = new ArrayList<>();
        for (Class<? extends BaseAmmo> ammo : baseAmmoList) listAmmo.add(ArmamentUtils.getAmmo(ammo));
        return listAmmo;
    }
    
    private @NotNull List<BaseAmmo> listAmmoFill(List<Class<? extends BaseAmmo>> ammoList){
        List<Class<? extends BaseAmmo>> listAmmonFill = new ArrayList<>();
        for (int i = 0; i < ammoMax; i++) {
            listAmmonFill.add(ammoList.get(i % ammoList.size()));
        }
        return listAmmoClassToListBaseAmmo(listAmmonFill);
    }

    @Override
    public void onShoot(List<ShootData> shootData){

    }
}
