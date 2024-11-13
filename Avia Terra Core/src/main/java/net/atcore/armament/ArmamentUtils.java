package net.atcore.armament;

import lombok.experimental.UtilityClass;
import net.atcore.AviaTerraCore;
import net.atcore.utils.GlobalUtils;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
@UtilityClass
public class ArmamentUtils {

    public String listToString(List<String> list){
        return list.toString().replace(" ", "").replace("[", "").replace("]", "");
    }

    public @NotNull List<String> stringToList(String list){
        List<String> finalList = new ArrayList<>(Arrays.asList(list.split(",")));
        if (finalList.getFirst().isBlank()){
            return new ArrayList<>();
        }else{
            return finalList;
        }
    }

    @Nullable
    public Compartment getCompartment(ItemStack itemStack){
        BaseWeapon s = getWeapon(itemStack);//es muy feo pero funciona
        if (s != null){
            if (s instanceof Compartment compartment){
                return compartment;
            }else{
                return null;
            }
        }else {
            return getCharger(itemStack);
        }
    }

    public BaseAmmo getAmmo(Player player){
        return getAmmo((String) GlobalUtils.getPersistenData(player.getInventory().getItemInMainHand(), "ammoName", PersistentDataType.STRING));
    }

    @Nullable
    public BaseAmmo getAmmo(@Nullable String s){
        ListAmmo list;
        if (s == null){
            return null;
        }
        try{
            list = ListAmmo.valueOf(s);
        }catch (Exception e){
            return null;
        }
        return list.getAmmo();
    }

    public BaseCharger getCharger(@NotNull Player player){
        return getCharger(player.getInventory().getItemInMainHand());
    }

    /**
     * Obtienes la clase {@link BaseCharger} si es un arma si no regresa
     * nulo en caso de que no
     */

    public BaseCharger getCharger(@NotNull ItemStack item) {
        if (item.getItemMeta() == null) return null;
        String chargeName = (String) GlobalUtils.getPersistenData(item, "chargerName", PersistentDataType.STRING);
        if (chargeName == null) return null;
        return getCharger(chargeName);
    }

    @Nullable
    public BaseCharger getCharger(@Nullable String s){
        ListCharger list;
        try{
            list = ListCharger.valueOf(s);
        }catch (Exception e){
            return null;
        }
        return list.getCharger();
    }

    public BaseWeapon getWeapon(@NotNull Player player){
        return getWeapon(player.getInventory().getItemInMainHand());
    }

    /**
     * Obtienes la clase {@link BaseWeapon} si es un arma si no regresa
     * nulo en caso de que no
     */

    public BaseWeapon getWeapon(@NotNull ItemStack item) {
        if (item.getItemMeta() == null) return null;
        String weaponName = (String) GlobalUtils.getPersistenData(item, "weaponName", PersistentDataType.STRING);
        if (weaponName == null) return null;
        return getWeapon(weaponName);
    }

    @Nullable
    public BaseWeapon getWeapon(@Nullable String s) {
        try {
            return ListWeaponTarvok.valueOf(s).getWeapon();
        } catch (Exception e) {
            try {
                return ListWeaponUltraKill.valueOf(s).getWeapon();
            } catch (Exception i) {
                return null;
            }
        }
    }

    public void drawParticleLine(@NotNull Location start, Location end, Color color, boolean impacted, double density) {
        World world = start.getWorld();
        if (world == null || !world.equals(end.getWorld())) {
            return;
        }

        Vector direction = end.toVector().subtract(start.toVector()).normalize();
        double distance = start.distance(end);
        Particle.DustOptions dustOptions = new Particle.DustOptions(color, 0.6F);
        //Location point = start;
        new BukkitRunnable() {
            Location point = start;
            double d = 0;
            public void run() {
                for (int i = 0; i < 3; i++){// crear las partículas en 3 en 3
                    d += density;
                    point = start.clone().add(direction.clone().multiply(d));
                    //los ceros representa como de aleatorio aran spawn en el mundo en cada eje, primer numeró es la calidad de particular y el ultimo la velocidad
                    world.spawnParticle(Particle.DUST, point, 2, 0, 0, 0,0.3, dustOptions ,false);
                    if (d > distance){
                        if (impacted)world.spawnParticle(Particle.CRIT, point, 4, 0.3, 0.3, 0.3, 0.2, null, true);
                        cancel();
                        break;
                    }
                }
            }
        }.runTaskTimer(AviaTerraCore.getInstance(), 1, 1);//TODO optimiza esto por favor. El problema es el envió del paquete
    }

    public Location getLookLocation(Vector direction, Location location, double maxDistance, double stepSize) {
        Location currentLocation = location.clone();
        if (stepSize <= 0) throw new IllegalArgumentException("No puede ser menor que cero");
        for (double i = 0; i < maxDistance; i += stepSize) {
            currentLocation.add(direction.clone().multiply(stepSize));
        }

        return location.add(direction.multiply(maxDistance));
    }
}
