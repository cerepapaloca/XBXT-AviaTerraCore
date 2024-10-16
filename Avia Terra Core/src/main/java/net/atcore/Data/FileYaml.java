package net.atcore.Data;

import lombok.Getter;
import net.atcore.AviaTerraCore;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public abstract class FileYaml {

    private final String fileName;
    protected FileConfiguration fileConfiguration = null;
    @Getter
    private File file = null;
    private final String folderName;

    public FileYaml(String fileName, String folderName){
        this.fileName = fileName;
        this.folderName = folderName;
    }

    public String getPath(){
        return this.fileName;
    }

    public void registerConfig(){
        if(folderName != null){
            file = new File(AviaTerraCore.getInstance().getDataFolder() + File.separator + folderName,fileName);
        }else{
            file = new File(AviaTerraCore.getInstance().getDataFolder(), fileName);
        }

        if(!file.exists()){
            try{
                file.createNewFile();
            }catch(IOException e){
                throw new RuntimeException(e);
            }
        }

        fileConfiguration = new YamlConfiguration();
        try {
            fileConfiguration.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveConfig() {
        try {
            fileConfiguration.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void reloadConfig() {
        if (fileConfiguration == null) {
            if(folderName != null){
                file = new File(AviaTerraCore.getInstance().getDataFolder()+File.separator + folderName, fileName);
            }else{
                file = new File(AviaTerraCore.getInstance().getDataFolder(), fileName);
            }

        }
        fileConfiguration = YamlConfiguration.loadConfiguration(file);

        if(file != null) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(file);
            fileConfiguration.setDefaults(defConfig);
        }
    }

    public abstract void loadData();

    public abstract void saveData();
}