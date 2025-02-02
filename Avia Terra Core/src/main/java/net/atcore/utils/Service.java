package net.atcore.utils;

import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;

import java.io.File;
import java.util.UUID;

@UtilityClass
public class Service {

    public boolean removeStatsPlayer(UUID uuid) {
        File statsFolder = new File(Bukkit.getWorlds().getFirst().getWorldFolder(), "stats");
        File stats = new File(statsFolder, uuid + ".json");

        return stats.exists() && stats.delete();
    }

    public boolean removePlayerData(UUID uuid) {
        File playerDataFolder = new File(Bukkit.getWorlds().getFirst().getWorldFolder(), "playerdata");
        if (!playerDataFolder.exists()) return false;

        File playerDat = new File(playerDataFolder, uuid + ".dat");
        File playerDatOld = new File(playerDataFolder, uuid + ".dat_old");

        boolean deletedDat = playerDat.exists() && playerDat.delete();
        boolean deletedDatOld = playerDatOld.exists() && playerDatOld.delete();

        return deletedDat || deletedDatOld;
    }

}
