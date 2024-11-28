package net.atcore.data;

import lombok.Getter;
import net.atcore.AviaTerraCore;
import net.atcore.messages.MessagesManager;
import net.atcore.messages.TypeMessages;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

import java.io.*;

@SuppressWarnings("ResultOfMethodCallIgnored")
public abstract class FileYaml {

    protected final String fileName;
    @Getter
    protected FileConfiguration fileYaml = null;
    @Getter
    private File file = null;
    private final String folderName;

    public FileYaml(String fileName, String folderName, boolean copy) {
        if (fileName.endsWith(".yml")) {
            this.fileName = fileName;
        }else {
            this.fileName = fileName + ".yml";
        }
        this.folderName = folderName;
        if (copy) {
            copyDefaultConfig();// Copiar archivo desde resources
            loadConfig(); // Carga los datos en memoria
            loadData(); // Aplica los datos
        }

    }

    public String getPath(){
        if (folderName == null){
            return fileName;
        }else {
            return folderName + File.separator + fileName;
        }

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

        file = new File(targetFolder, fileName);

        if (!file.exists()) {
            try (InputStream inputStream = AviaTerraCore.getInstance().getResource(fileName);
                 OutputStream outputStream = new FileOutputStream(file)) {

                if (inputStream == null) {
                    throw new FileNotFoundException("El archivo " + fileName + " no se encontró en resources.");
                }

                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, length);
                }

            } catch (IOException e) {
                throw new RuntimeException("No se pudo copiar el archivo de configuración " + fileName, e);
            }
        }
    }

    private void loadConfig() {
        fileYaml = YamlConfiguration.loadConfiguration(file);
    }

    public FileConfiguration getConfig() {
        return fileYaml;
    }

    public void saveConfig() {
        try {
            fileYaml.save(file);
        } catch (IOException e) {
            throw new RuntimeException("No se pudo guardar el archivo de configuración " + fileName, e);
        }
    }


    public void reloadConfig(ActionInReloadYaml action) {

        if (fileYaml == null) {
            if(folderName != null){
                file = new File(AviaTerraCore.getInstance().getDataFolder() + File.separator + folderName, fileName);
            }else{
                file = new File(AviaTerraCore.getInstance().getDataFolder(), fileName);
            }
        }

        fileYaml = YamlConfiguration.loadConfiguration(file);
        /*if(file != null || fileYaml != null) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(file);
            fileYaml.setDefaults(defConfig);
        }*/
        if(!file.exists()){
            try{
                file.createNewFile();
            }catch(IOException e){
                throw new RuntimeException(e);
            }
        }
        switch (action){
            case LOAD -> loadData();
            case SAVE -> saveData();
        }

        if (!AviaTerraCore.isStarting()) MessagesManager.sendMessageConsole(String.format("Archivo %s recargador exitosamente", file), TypeMessages.SUCCESS);
    }

    public abstract void loadData();

    public abstract void saveData();
}