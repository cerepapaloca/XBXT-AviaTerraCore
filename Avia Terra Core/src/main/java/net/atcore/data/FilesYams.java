package net.atcore.data;

import lombok.Getter;
import net.atcore.AviaTerraCore;
import org.bukkit.Bukkit;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;

@SuppressWarnings("unused")
public abstract class FilesYams {

    protected String folderName;
    @Getter
    protected HashSet<FileYaml> configFiles;
    private final Class<? extends FileYaml> fileclass;

    public FilesYams(String folderName, Class<? extends FileYaml> fileclass, boolean configure){
        this.folderName = folderName;
        this.configFiles = new HashSet<>();
        this.fileclass = fileclass;
        if (configure) configure();
    }

    public void configure() {
        createFolder();
        reloadConfigsFiles();
    }

    public void reloadConfigsFiles(){
        this.configFiles = new HashSet<>();
        registerConfigFiles();
    }

    public void createFolder(){
        File folder;
        try {
            folder = new File(AviaTerraCore.getInstance().getDataFolder() + File.separator + folderName);
            if(!folder.exists()){
                folder.mkdirs();
            }
        } catch(SecurityException e) {
            throw new SecurityException(e);
        }
    }

    public void saveConfigFiles() {
        for (FileYaml configFile : configFiles) {
            configFile.saveConfig();
        }
    }

    public void registerConfigFiles(){
        String path = AviaTerraCore.getInstance().getDataFolder() + File.separator + folderName;
        File folder = new File(path);
        File[] listOfFiles = folder.listFiles();
        if (listOfFiles != null) {
            for (File file : listOfFiles) {
                if (file.isFile()) {
                    registerConfigFile(file.getName());
                }
            }
        }
    }

    public FileYaml getConfigFile(String pathName, boolean createNew) {
        for (FileYaml configFile : configFiles) {
            if (configFile.fileName.equals(pathName + ".yml")) {
                return configFile;
            }
        }
        if (createNew){
            return registerConfigFile(pathName + ".yml");
        }else {
            return null;
        }
    }

    public void unloadConfigFile(String pathName) {
        if (!pathName.endsWith(".yml")) pathName = pathName + ".yml";
        for (FileYaml configFile : configFiles) {
            if (configFile.fileName.equals(pathName)) {
                configFiles.remove(configFile);
                DataSection.FILES.remove(configFile);
                return;
            }
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void deleteConfigFile(String nameFile) {
        if (!nameFile.endsWith(".yml")) nameFile = nameFile + ".yml";
        for (FileYaml configFile : configFiles) {
            if (folderName != null){
                nameFile = folderName + File.separator + nameFile;
            }
            if (configFile.getPath().equals(nameFile)) {
                configFiles.remove(configFile);
                configFile.getFile().delete();
                return;
            }
        }
    }

    public FileYaml registerConfigFile(String fileName) {
        try {
            FileYaml config = fileclass.getConstructor(String.class, String.class)
                    .newInstance(fileName, folderName);
            configFiles.add(config);
            config.reloadConfig();
            return config;
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
