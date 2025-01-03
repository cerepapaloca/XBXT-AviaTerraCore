package net.atcore.data.yml;

import net.atcore.aviaterraplayer.AviaTerraPlayer;
import net.atcore.data.FileYaml;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

import java.util.UUID;

public class PlayerDataFile extends FileYaml {

    public PlayerDataFile(String fileName, String folderName) {
        super(fileName, folderName, false, false);
    }

    @Override
    public void loadData() {
        loadConfig();
        AviaTerraPlayer atp = AviaTerraPlayer.getPlayer(UUID.fromString(fileName.replace(".yml", "")));
        ConfigurationSection cs = fileYaml.getConfigurationSection("homes");
        if (cs == null) return;
        atp.getHomes().clear();
        for (String key : cs.getKeys(false)) {
            String worldName = cs.getString(key + ".world");
            double x = cs.getDouble(key + ".x");
            double y = cs.getDouble(key + ".y");
            double z = cs.getDouble(key + ".z");
            float yaw = (float) cs.getDouble(key + ".yaw");
            float pitch = (float) cs.getDouble(key + ".pitch");

            if (worldName == null) worldName = Bukkit.getWorlds().getFirst().getName();
            World world = Bukkit.getWorld(worldName);
            if (world == null) world = Bukkit.getWorlds().getFirst();
            atp.getHomes().put(key, new Location(world ,x, y, z, yaw, pitch));
        }
    }

    @Override
    public void saveData() {
        AviaTerraPlayer atp = AviaTerraPlayer.getPlayer(UUID.fromString(fileName.replace(".yml", "")));
        fileYaml.set("homes", null);
        for (String key : atp.getHomes().keySet()) {
            Location l = atp.getHomes().get(key);
            if (l == null) continue;
            if (l.getWorld() == null){
                fileYaml.set("homes." + key + ".world",Bukkit.getWorlds().getFirst().getName());
            }else {
                fileYaml.set("homes." + key + ".world",l.getWorld().getName());
            }
            fileYaml.set("homes." + key + ".x",l.getX());
            fileYaml.set("homes." + key + ".y",l.getY());
            fileYaml.set("homes." + key + ".z",l.getZ());
            fileYaml.set("homes." + key + ".yaw",l.getYaw());
            fileYaml.set("homes." + key + ".pitch",l.getPitch());
        }
        saveConfig();
    }
}
