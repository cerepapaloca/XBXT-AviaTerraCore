package net.atcore.data.yml;

import net.atcore.data.FileYaml;
import net.atcore.security.Login.model.LimboData;
import net.atcore.security.Login.model.LoginData;
import net.atcore.security.Login.LoginManager;
import org.bukkit.*;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class CacheLimboFile extends FileYaml {
    public CacheLimboFile(String fileName, String folderName) {
        super(fileName, folderName, false, false);
    }

    @Override
    public void loadData() {
        loadConfig();
        LoginData loginData = LoginManager.getDataLogin(UUID.fromString(fileName.replace(".yml", "")));
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


        LimboData limboData = new LimboData(
                GameMode.valueOf(fileYaml.getString("game-mode", "SURVIVAL").toUpperCase()),
                inventory,
                location,
                fileYaml.getBoolean("op", false),
                fileYaml.getInt("level-xp", 0)
        );
        loginData.setLimbo(limboData);
    }

    @Override
    public void saveData() {
        LimboData limboData = LoginManager.getDataLogin(UUID.fromString(fileName.replace(".yml", ""))).getLimbo();
        fileYaml.set("location.world", Objects.requireNonNull(limboData.getLocation().getWorld()).getName());
        fileYaml.set("location.x", limboData.getLocation().getX());
        fileYaml.set("location.y", limboData.getLocation().getY());
        fileYaml.set("location.z", limboData.getLocation().getZ());
        fileYaml.set("location.yaw", limboData.getLocation().getYaw());
        fileYaml.set("location.pitch", limboData.getLocation().getPitch());
        fileYaml.set("game-mode", limboData.getGameMode().name());
        fileYaml.set("level-xp", limboData.getLevel());
        fileYaml.set("op", limboData.isOp());
        fileYaml.set("inventory", limboData.getItems());
        saveConfig();
    }

    public boolean isRestored() {
        loadConfig();
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
