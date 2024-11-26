package net.atcore.data;

import org.bukkit.configuration.file.FileConfiguration;

public class FliesCacheLimbo extends FilesYams{
    public FliesCacheLimbo() {
        super("cacheLimbo", FileCacheLimbo.class);
    }

    @Override
    public void loadData(String s) {
        FileConfiguration fileYaml = getConfigFile(s).getFileYaml();
    }

    @Override
    public void saveData(String s) {

    }
}
