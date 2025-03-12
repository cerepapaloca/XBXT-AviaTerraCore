package net.atcore.data.yml;

import net.atcore.data.FileYaml;
import net.atcore.messages.MessagesManager;
import net.atcore.messages.TypeMessages;
import net.atcore.security.login.model.LimboData;
import net.atcore.security.login.model.LoginData;
import net.atcore.security.login.LoginManager;
import org.bukkit.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.*;

public class CacheLimboFile extends FileYaml {
    public CacheLimboFile(String fileName, String folderName) {
        super(fileName, folderName, false, false);
    }

    @Override
    public void loadData() {
        loadConfig();
        List<?> rawList = fileYaml.getList("inventory", null);
        ItemStack[] inventory;
        if (rawList != null) {
            inventory = new ItemStack[rawList.size()];
            int i = 0;
            for (Object obj : rawList) {
                if (obj == null) {
                    inventory[i] = (new ItemStack(Material.AIR));
                }else if (obj instanceof ItemStack) {
                    inventory[i] = ((ItemStack) obj);
                }
                i++;
            }
        }else {
            inventory = new ItemStack[0];
        }
        Location location;
        if (null == fileYaml.getString("location.world", null)){
            location = Bukkit.getWorlds().getFirst().getSpawnLocation();
        }else {
            World world = Bukkit.getWorld(fileYaml.getString("world", "world"));
            location = new Location(world,
                    fileYaml.getDouble("location.x"),
                    fileYaml.getDouble("location.y"),
                    fileYaml.getDouble("location.z"),
                    (float) fileYaml.getDouble("location.yaw"),
                    (float) fileYaml.getDouble("location.pitch")
            );
        }

        List<?> raw = fileYaml.getList("effects");
        List<PotionEffect> effects = new ArrayList<>();
        if (raw != null) {
            for (Object obj : raw) {
                if (obj != null) {
                    if (obj instanceof PotionEffect effect) {
                        effects.add(effect);
                    }
                }
            }
        }

        LimboData limboData = new LimboData(
                GameMode.valueOf(fileYaml.getString("game-mode", "SURVIVAL").toUpperCase()),
                inventory,
                location,
                fileYaml.getBoolean("op", false),
                fileYaml.getInt("level-xp", 0),
                fileYaml.getDouble("health", 10),
                fileYaml.getInt("food-level"),
                (float) fileYaml.getDouble("exhaustion"),
                (float) fileYaml.getDouble("saturation"),
                fileYaml.getInt("fire-tick"),
                effects
        );
        LoginData loginData = LoginManager.getDataLogin(UUID.fromString(fileName.replace(".yml", "")));
        loginData.setLimbo(limboData);
    }

    @Override
    public void saveData() {
        UUID userUUID = UUID.fromString(fileName.replace(".yml", ""));
        LimboData limboData = LoginManager.getDataLogin(userUUID).getLimbo();
        if (limboData == null) {
            MessagesManager.logConsole("El limbo data dio nulo para la uuid " + userUUID, TypeMessages.WARNING);
            return;
        }
        fileYaml.set("location.world", Objects.requireNonNull(limboData.getLocation().getWorld()).getName());
        fileYaml.set("location.x", limboData.getLocation().getX());
        fileYaml.set("location.y", limboData.getLocation().getY());
        fileYaml.set("location.z", limboData.getLocation().getZ());
        fileYaml.set("location.yaw", limboData.getLocation().getYaw());
        fileYaml.set("location.pitch", limboData.getLocation().getPitch());
        fileYaml.set("game-mode", limboData.getGameMode().name());
        fileYaml.set("level-xp", limboData.getLevel());
        fileYaml.set("op", limboData.isOp());
        fileYaml.set("food-level", limboData.getFoodLevel());
        fileYaml.set("inventory", limboData.getItems());
        fileYaml.set("effects", limboData.getEffects());
        fileYaml.set("health", limboData.getHealth());
        fileYaml.set("exhaustion", limboData.getExhaustion());
        fileYaml.set("fire-tick", limboData.getFireTicks());
        fileYaml.set("saturation", limboData.getSaturation());
        saveConfig();
    }

    public boolean isRestored() {
        return fileYaml.getBoolean("is-restored");
    }

    public void setRestored(boolean isRestored) {
        fileYaml.set("is-restored", isRestored);
        saveConfig();
    }

    public void removeLimbo() {
        LoginData login = LoginManager.getDataLogin(UUID.fromString(fileName.replace(".yml", "")));
        login.setLimbo(null);
    }

    @Override
    protected void addFile(){

    }
}
