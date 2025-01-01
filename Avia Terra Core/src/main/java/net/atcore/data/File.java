package net.atcore.data;

import com.google.common.io.Files;
import lombok.Getter;
import net.atcore.AviaTerraCore;
import net.atcore.messages.MessagesManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.charset.StandardCharsets;

public abstract class File {

    @Getter
    protected java.io.File file = null;
    protected final String fileName;
    protected final String folderName;
    protected final String fileExtension;

    protected File(@NotNull String fileName, @NotNull String fileExtension, @Nullable String folderName) {
        if (fileExtension.startsWith(".")) {
            this.fileExtension = fileExtension.toLowerCase();
        }else {
            this.fileExtension = "." + fileExtension.toLowerCase();
        }
        this.folderName = folderName;
        if (fileName.endsWith(this.fileExtension)) {
            this.fileName = fileName;
        }else {
            this.fileName = fileName + this.fileExtension;
        }
        addFile();
    }

    protected void addFile() {
        DataSection.FILES.add(this);
    }

    @Override
    public String toString() {
        return file.getPath();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    protected void copyDefaultConfig() {
        java.io.File dataFolder = AviaTerraCore.getInstance().getDataFolder();

        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }

        java.io.File targetFolder = dataFolder;
        if (folderName != null) {
            targetFolder = new java.io.File(dataFolder, folderName);
            if (!targetFolder.exists()) {
                targetFolder.mkdirs();
            }
        }

        file = new java.io.File(targetFolder, fileName);

        if (!file.exists()) {
            try (InputStream inputStream = AviaTerraCore.getInstance().getResource(fileName);
                 OutputStream outputStream = new FileOutputStream(file)) {

                if (inputStream != null) {
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = inputStream.read(buffer)) > 0) {
                        outputStream.write(buffer, 0, length);
                    }
                }
            } catch (IOException e) {
                MessagesManager.sendErrorException("No se pudo copiar de resources el archivo " + fileName, e);
            }
        }
    }

    protected String readFile() {
        if (file == null) reloadFile();
        try {
            return Files.asCharSource(file, StandardCharsets.UTF_8).read();
        } catch (IOException e) {
            MessagesManager.sendErrorException("Error al copiar o leer el archivo", e);
            return "";
        }
    }

    protected void reloadFile() {
        if(folderName != null){
            file = new java.io.File(AviaTerraCore.getInstance().getDataFolder() + java.io.File.separator + folderName, fileName);
        }else{
            file = new java.io.File(AviaTerraCore.getInstance().getDataFolder(), fileName);
        }
    }

    public abstract void loadData();
}
