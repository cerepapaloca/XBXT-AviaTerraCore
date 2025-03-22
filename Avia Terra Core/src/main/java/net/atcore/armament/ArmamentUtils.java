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
import java.util.HashSet;
import java.util.List;
@UtilityClass
public class ArmamentUtils {

    public static final HashSet<BaseArmament> ARMAMENTS = new HashSet<>();

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
    public Compartment getCompartment(@NotNull ItemStack item){
        for (BaseArmament armament : ARMAMENTS){
            if (armament.getName().equals(GlobalUtils.getPersistenData(item, "armament", PersistentDataType.STRING))){
                if (armament instanceof Compartment compartment){
                    return compartment;
                }
            }
        }
        return null;
    }

    public BaseMagazine getMagazine(@NotNull ItemStack item) {
        if (item.getItemMeta() == null) return null;
        String chargeName = (String) GlobalUtils.getPersistenData(item, "armament", PersistentDataType.STRING);
        if (chargeName == null) return null;
        return getMagazine(chargeName);
    }

    public BaseWeapon getWeapon(@NotNull ItemStack item) {
        if (item.getItemMeta() == null) return null;
        String armament = (String) GlobalUtils.getPersistenData(item, "armament", PersistentDataType.STRING);
        if (armament == null) return null;
        return getWeapon(armament);
    }

    public BaseAmmo getAmmo(Player player){
        return getAmmo((String) GlobalUtils.getPersistenData(player.getInventory().getItemInMainHand(), "armament", PersistentDataType.STRING));
    }

    public @Nullable BaseAmmo getAmmo(Class<? extends BaseAmmo> clazz) {
        for (BaseArmament armament : ARMAMENTS) {
            if (armament instanceof BaseAmmo ammo) {
                if (clazz.isAssignableFrom(ammo.getClass())) {
                    return ammo;
                }
            }
        }
        return null;
    }

    public BaseMagazine getMagazine(@NotNull Player player){
        return getMagazine(player.getInventory().getItemInMainHand());
    }

    public BaseWeapon getWeapon(@NotNull Player player){
        return getWeapon(player.getInventory().getItemInMainHand());
    }

    /**
     * Obtienes la clase {@link BaseMagazine} si es un arma si no regresa
     * nulo en caso de que no
     */

    @Nullable
    public BaseAmmo getAmmo(@Nullable String s){
        for (BaseArmament armament : ARMAMENTS){
            if (armament instanceof BaseAmmo ammo){
                if (ammo.getClass().getName().equals(s)){
                    return ammo;
                }
            }
        }
        return null;
    }

    @Nullable
    public BaseMagazine getMagazine(@Nullable String s){
        for (BaseArmament armament : ARMAMENTS){
            if (armament instanceof BaseMagazine magazine){
                if (magazine.getClass().getName().equals(s)){
                    return magazine;
                }
            }
        }
        return null;
    }

    @Nullable
    public BaseWeapon getWeapon(@Nullable String s) {
        for (BaseArmament armament : ARMAMENTS){
            if (armament instanceof BaseWeapon weapon){
                if (weapon.getClass().getName().equals(s)){
                    return weapon;
                }
            }
        }
        return null;
    }
    
    public @Nullable BaseArmament getArmament(@NotNull ItemStack item) {
        String name = (String) GlobalUtils.getPersistenData(item, "armament", PersistentDataType.STRING);
        for (BaseArmament armament : ARMAMENTS){
            if (armament.getClass().getName().equals(name)){
                return armament;
            }
        }
        return null;
    }

    public void drawParticleLine(@NotNull Location start, Location end, Color color, double density) {
        World world = start.getWorld();
        if (density <= 0) return; // Evita divisiones por cero

        double distance = start.distance(end);
        int steps = (int) (distance / density); // Calcula cuántos puntos se necesitan

        Particle.DustOptions dustOptions = new Particle.DustOptions(color, 0.6F);

        // Vector dirección normalizado
        double dx = (end.getX() - start.getX()) / distance;
        double dy = (end.getY() - start.getY()) / distance;
        double dz = (end.getZ() - start.getZ()) / distance;

        for (int i = 0; i <= steps; i++) {
            Location point = start.clone().add(dx * density * i, dy * density * i, dz * density * i);
            world.spawnParticle(Particle.DUST, point, 2, 0, 0, 0,0.3, dustOptions ,false);
        }
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
