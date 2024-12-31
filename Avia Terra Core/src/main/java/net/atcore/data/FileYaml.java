package net.atcore.data;

import lombok.Getter;
import net.atcore.data.yml.ActionInReloadYaml;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

@Getter
@SuppressWarnings("ResultOfMethodCallIgnored")
public abstract class FileYaml extends net.atcore.data.File {


    protected FileConfiguration fileYaml = null;

    public FileYaml(String fileName, String folderName, boolean copy) {
        super(fileName, "yml", folderName);
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

    /**
     * Lee los datos del yml. Esto se debería poner al inicio
     * del {@link #loadData()} para que pueda usar los datos
     * actualizados del yml escrito en la memoria ROM
     */

    protected void loadConfig() {
        fileYaml = YamlConfiguration.loadConfiguration(file);
    }

    public FileConfiguration getConfig() {
        return fileYaml;
    }

    /**
     * Escribe los datos del {@link #fileYaml} en la memoria ROM.
     * Esto tiene que estar al final del {@link #saveData()} para
     * que guarde los datos modificados
     */

    public void saveConfig() {
        try {
            fileYaml.save(file);
        } catch (IOException e) {
            throw new RuntimeException("No se pudo guardar el archivo de configuración " + fileName, e);
        }
    }

    public void reloadConfig(ActionInReloadYaml action) {
        if (fileYaml == null) {
            reloadFile();
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
    }

    public abstract void saveData();
}