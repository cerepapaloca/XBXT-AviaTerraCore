package net.atcore.data;

import lombok.Getter;
import net.atcore.AviaTerraCore;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

import java.io.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public abstract class FileYaml {

    private final String fileName;
    protected FileConfiguration fileConfiguration = null;
    @Getter
    private File file = null;
    private final String folderName;

    public FileYaml(String fileName, String folderName) {
        this.fileName = fileName;
        this.folderName = folderName;
        copyDefaultConfig(); // Copiar archivo desde resources
        loadConfig(); // Cargar la configuración
    }

    private void copyDefaultConfig() {
        File dataFolder = AviaTerraCore.getInstance().getDataFolder();

        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }

        File targetFolder = dataFolder;
        if (folderName != null) {
            targetFolder = new File(dataFolder, folderName);
            if (!targetFolder.exists()) {
                targetFolder.mkdirs();
            }
        }

        file = new File(targetFolder, fileName + ".yml");

        if (!file.exists()) {
            try (InputStream inputStream = AviaTerraCore.getInstance().getResource(fileName + ".yml");
                 OutputStream outputStream = new FileOutputStream(file)) {

                if (inputStream == null) {
                    throw new FileNotFoundException("El archivo " + fileName + ".yml no se encontró en resources.");
                }

                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, length);
                }

            } catch (IOException e) {
                throw new RuntimeException("No se pudo copiar el archivo de configuración " + fileName + ".yml", e);
            }
        }
    }

    private void loadConfig() {
        fileConfiguration = YamlConfiguration.loadConfiguration(file);
    }

    public FileConfiguration getConfig() {
        return fileConfiguration;
    }

    public void saveConfig() {
        try {
            fileConfiguration.save(file);
        } catch (IOException e) {
            throw new RuntimeException("No se pudo guardar el archivo de configuración " + fileName + ".yml", e);
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