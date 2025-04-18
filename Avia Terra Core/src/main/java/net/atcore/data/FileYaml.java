package net.atcore.data;

import lombok.Getter;
import net.atcore.AviaTerraCore;
import net.atcore.messages.MessagesManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

@Getter
@SuppressWarnings("ResultOfMethodCallIgnored")
public abstract class FileYaml extends net.atcore.data.File {

    protected FileConfiguration fileYaml = null;
    protected final boolean forceLoad;

    public FileYaml(String fileName, String folderName, boolean copy, boolean forceLoad) {
        super(fileName, "yml", folderName);
        this.forceLoad = forceLoad;
        if (copy) {
            copyDefaultConfig();// Copiar archivo desde resources
            loadConfig(); // Carga los datos en memoria
            loadData();
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
        if (Bukkit.isPrimaryThread() && !(AviaTerraCore.isStarting() || AviaTerraCore.isStopping())) {
            throw new IllegalThreadStateException("No no se puede cargar el archivo en el hilo principal");
        }
        if (file == null) reloadFile();
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
        if (Bukkit.isPrimaryThread() && !(AviaTerraCore.isStarting() || AviaTerraCore.isStopping())){
            throw new IllegalThreadStateException("No no se puede guardar el archivo en el hilo principal");
        }
        try {
            fileYaml.save(Objects.requireNonNullElseGet(file, this::reloadFile));
        } catch (Exception e) {
            MessagesManager.sendWaringException("No se pudo guardar el archivo de configuración " + fileName, e);
        }
    }

    public void reloadConfig() {
        reloadConfig(forceLoad);
    }

    public void reloadConfig(boolean loadData) {
        if (file == null) reloadFile();

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
        if (loadData) loadData();
        /*
        switch (action){
            case LOAD ->
            case SAVE -> saveData();
            case NOTHING -> {}
        }*/
    }

    public abstract void saveData();
}