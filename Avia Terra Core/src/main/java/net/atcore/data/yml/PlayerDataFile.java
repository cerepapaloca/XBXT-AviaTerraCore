package net.atcore.data.yml;

import net.atcore.aviaterraplayer.AviaTerraPlayer;
import net.atcore.data.FileYaml;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;
import java.util.UUID;

public class PlayerDataFile extends FileYaml {

    public PlayerDataFile(String fileName, String folderName) {
        super(fileName, folderName, false, false);
    }

    @Override
    public void loadData() {
        loadConfig();
        AviaTerraPlayer atp = AviaTerraPlayer.getPlayer(UUID.fromString(fileName.replace(".yml", "")));
        ConfigurationSection csHome = fileYaml.getConfigurationSection("homes");
        atp.setNameColor(fileYaml.getString("display-name"));
        if (csHome == null) return;
        atp.getPlayersBLock().clear();
        atp.getHomes().clear();
        for (String key : csHome.getKeys(false)) {
            String worldName = csHome.getString(key + ".world");
            double x = csHome.getDouble(key + ".x");
            double y = csHome.getDouble(key + ".y");
            double z = csHome.getDouble(key + ".z");
            float yaw = (float) csHome.getDouble(key + ".yaw");
            float pitch = (float) csHome.getDouble(key + ".pitch");

            if (worldName == null) worldName = Bukkit.getWorlds().getFirst().getName();
            World world = Bukkit.getWorld(worldName);
            if (world == null) world = Bukkit.getWorlds().getFirst();
            atp.getHomes().put(key, new Location(world ,x, y, z, yaw, pitch));
        }
        List<?> rawList = fileYaml.getList("block-players");
        if (rawList != null) {
            for (Object raw : rawList) {
                if (raw == null) continue;
                if (raw instanceof String name) atp.getPlayersBLock().add(name);
            }
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
        fileYaml.set("display-name", atp.getNameColor());
        fileYaml.set("block-players", atp.getPlayersBLock().stream().toList());
        saveConfig();
    }
}
