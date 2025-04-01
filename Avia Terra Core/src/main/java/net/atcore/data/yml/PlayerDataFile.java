package net.atcore.data.yml;

import net.atcore.achievement.BaseAchievement;
import net.atcore.aviaterraplayer.AviaTerraPlayer;
import net.atcore.data.FileYaml;
import net.atcore.utils.GlobalUtils;
import net.minecraft.advancements.AdvancementProgress;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
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

        String displayName = fileYaml.getString("display-name");
        atp.setNameColor(displayName);
        if (displayName != null) atp.getPlayer().displayName(GlobalUtils.chatColorLegacyToComponent(displayName));
        atp.getPlayersBLock().clear();
        atp.getHomes().clear();
        atp.clearAchievementProgress();
        ConfigurationSection csHome = fileYaml.getConfigurationSection("homes");
        if (csHome != null) {
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
        }
        List<?> rawList = fileYaml.getList("block-players");
        if (rawList != null) {
            for (Object raw : rawList) {
                if (raw == null) continue;
                if (raw instanceof String name) atp.getPlayersBLock().add(name);
            }
        }
        ConfigurationSection csAchievement = fileYaml.getConfigurationSection("achievements");
        if (csAchievement != null) {
            for (BaseAchievement<?> achievement : BaseAchievement.getAllAchievement()) {
                AdvancementProgress progressAchievement = atp.getProgress(achievement).getProgress();
                String path = "achievements." + achievement.id.getPath().replace("/", ".");
                List<?> progressComplete = fileYaml.getList(path  + ".complete");
                if (progressComplete == null) continue;
                for (Object raw : progressComplete) {
                    if (raw == null) continue;
                    if (raw instanceof String s) progressAchievement.grantProgress(s);
                }
                if (fileYaml.isDouble(path + ".progress")){
                    atp.getProgressInteger(achievement).setValue(fileYaml.getInt(path + ".progress"));
                }
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
        for (AviaTerraPlayer.DataProgress data : atp.getAllProgress()){
            ArrayList<String> completed = new ArrayList<>();
            data.getProgress().getCompletedCriteria().forEach(completed::add);
            String path = data.getLocationId().getPath().replace("/",".");
            fileYaml.set("achievements." + path + ".complete", completed);
            if (data instanceof AviaTerraPlayer.DataProgressContinuos dataInteger) {
                fileYaml.set("achievements." + path + ".progress", dataInteger.getValue());
            }
        }
        saveConfig();
    }
}
