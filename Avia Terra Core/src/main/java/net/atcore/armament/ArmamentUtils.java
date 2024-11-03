package net.atcore.armament;

import lombok.experimental.UtilityClass;
import net.atcore.utils.GlobalUtils;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
@UtilityClass
public class ArmamentUtils {

    public static final HashMap<ListCharger, BaseCharger> baseChargers = new HashMap<>();
    public static final HashMap<ListWeaponTarvok, BaseWeaponTarkov> baseWeaponsTarkov = new HashMap<>();
    public static final HashMap<ListWeaponUltraKill, BaseWeaponUltraKill> baseWeaponsUltraKill = new HashMap<>();
    public static final HashMap<ListAmmo, BaseAmmo> baseAmmo = new HashMap<>();

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
        return getCharger(itemStack);
    }

    @Nullable
    public BaseAmmo getAmmon(@Nullable String s){
        ListAmmo list;
        if (s == null){
            return null;
        }
        try{
            list = ListAmmo.valueOf(s);
        }catch (Exception e){
            return null;
        }
        return baseAmmo.get(list);
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
        String chargeName = (String) GlobalUtils.getPersistenData(item, "chargerType", PersistentDataType.STRING);
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
        return baseChargers.get(list);
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
    public BaseWeapon getWeapon(@Nullable String s){
        ListWeaponTarvok list;
        try{
            list = ListWeaponTarvok.valueOf(s);
        }catch (Exception e){
            return null;
        }
        return baseWeaponsTarkov.get(list);
    }

    public void drawParticleLine(Location start, Location end, Color color, boolean impacted, double density) {
        World world = start.getWorld();
        if (world == null || !world.equals(end.getWorld())) {
            return;
        }

        Vector direction = end.toVector().subtract(start.toVector()).normalize();
        double distance = start.distance(end);

        Particle.DustOptions dustOptions = new Particle.DustOptions(color, 0.5F);
        Location point = start;
        for (double d = 0; d < distance; d += density) {
            point = start.clone().add(direction.clone().multiply(d));
            //los ceros representa como de aleatorio aran spawn en el mundo en cada eje, primer numeró es la calidad de particular y el ultimo la velocidad
            world.spawnParticle(Particle.DUST, point, 2, 0, 0, 0,0.3, dustOptions ,false);
        }
        if (impacted)world.spawnParticle(Particle.CRIT, point, 2, 0.1, 0.1, 0.1, 0.5, null, true);
    }

    public Location getPlayerLookLocation(Player player, double maxDistance, double stepSize) {

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
}
