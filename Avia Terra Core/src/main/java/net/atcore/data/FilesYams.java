package net.atcore.data;

import lombok.Getter;
import net.atcore.AviaTerraCore;

import java.io.File;
import java.util.HashSet;
import java.util.function.Supplier;

public abstract class FilesYams {

    protected String folderName;
    @Getter
    protected HashSet<FileYaml> configFiles;
    private final Class<? extends FileYaml> fileclass;

    public FilesYams(String folderName, Class<? extends FileYaml> fileclass){
        this.folderName = folderName;
        this.configFiles = new HashSet<>();
        this.fileclass = fileclass;
        configure();
    }

    public void configure() {
        createFolder();
        reloadConfigs();
    }

    public void reloadConfigs(){
        this.configFiles = new HashSet<>();
        registerConfigFiles();
        //loadConfigs();
    }

    public void createFolder(){
        File folder;
        try {
            folder = new File(AviaTerraCore.getInstance().getDataFolder() + File.separator + folderName);
            if(!folder.exists()){
                folder.mkdirs();
            }
        } catch(SecurityException e) {
            e.printStackTrace();
        }
    }

//    public void saveConfigFiles() {
//        for (FileYaml configFile : configFiles) {
//            configFile.saveConfig();
//        }
//    }

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

    public FileYaml getConfigFile(String pathName) {
        for (FileYaml configFile : configFiles) {
            if (configFile.getPath().equals(pathName)) {
                return configFile;
            }
        }
        return registerConfigFile(pathName);
    }

    public FileYaml registerConfigFile(String pathName) {
        try {
            FileYaml config = fileclass.getConstructor(String.class, String.class)
                    .newInstance(pathName, folderName);
            config.reloadConfig();
            configFiles.add(config);
            return config;
        } catch (Exception e) {
            throw new RuntimeException("Error al crear instancia de " + fileclass.getName(), e);
        }
    }

    public abstract void loadData(String s);
    public abstract void saveData(String s);
}
