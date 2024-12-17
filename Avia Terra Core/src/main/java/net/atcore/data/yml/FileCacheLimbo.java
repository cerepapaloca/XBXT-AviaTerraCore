package net.atcore.data.yml;

import net.atcore.data.FileYaml;
import net.atcore.security.Login.DataLimbo;
import net.atcore.security.Login.DataLogin;
import net.atcore.security.Login.LoginManager;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class FileCacheLimbo extends FileYaml {
    public FileCacheLimbo(String fileName, String folderName) {
        super(fileName, folderName, false);
    }

    @Override
    public void loadData() {
        DataLogin dataLogin = LoginManager.getDataLogin(UUID.fromString(fileName.replace(".yml", "")));
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
                    fileYaml.getDouble("location.z")
            );
        }


        DataLimbo dataLimbo = new DataLimbo(
                GameMode.valueOf(fileYaml.getString("game-mode", "SURVIVAL").toUpperCase()),
                inventory,
                location,
                fileYaml.getBoolean("op", false),
                fileYaml.getInt("level-xp", 0)
        );
        dataLogin.setLimbo(dataLimbo);
    }

    @Override
    public void saveData() {
        DataLimbo dataLimbo = LoginManager.getDataLogin(UUID.fromString(fileName.replace(".yml", ""))).getLimbo();
        fileYaml.set("location.world", Objects.requireNonNull(dataLimbo.getLocation().getWorld()).getName());
        fileYaml.set("location.x", dataLimbo.getLocation().getX());
        fileYaml.set("location.y", dataLimbo.getLocation().getY());
        fileYaml.set("location.z", dataLimbo.getLocation().getZ());
        fileYaml.set("game-mode", dataLimbo.getGameMode().name());
        fileYaml.set("level-xp", dataLimbo.getLevel());
        fileYaml.set("op", dataLimbo.isOp());
        fileYaml.set("inventory", dataLimbo.getItems());
        fileYaml.set("is-restored", false);
        saveConfig();
    }

    public boolean isRestored() {
        return fileYaml.getBoolean("is-restored");
    }

    public void setRestored(boolean isRestored) {
        fileYaml.set("is-restored", isRestored);
        saveConfig();
    }
}
