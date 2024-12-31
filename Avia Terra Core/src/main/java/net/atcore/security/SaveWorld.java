package net.atcore.security;

import jdk.jfr.Experimental;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Comparator;

@Experimental
public class SaveWorld {

    private static final int MAX_BACKUPS = 3;

    public static void main(String[] args) {
        File sourceDir = new File("C:/Users/Cagut/Desktop/AviaTerra/config");
        File backupsBaseDir  = new File("C:/Users/Cagut/Desktop/AviaTerra/copia");

        if (!sourceDir.exists() || !sourceDir.isDirectory()) {
            System.out.println("La carpeta de origen no existe o no es un directorio.");
            return;
        }

        String backupName = getNextBackupName(sourceDir.getName(), backupsBaseDir);
        File targetDir = new File(backupsBaseDir, backupName);

        try {
            File deleteFile = findOldestFile(backupsBaseDir);
            if (deleteFile != null)deleteDirectory(deleteFile);
            copyDirectory(sourceDir, targetDir);
            System.out.println("Copia creada con éxito: " + targetDir.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Error al copiar la carpeta: " + e.getMessage());
        }
    }

    private static String getNextBackupName(String baseName, File backupsBaseDir) {
        int counter = 1;
        File candidate;
        do {
            candidate = new File(backupsBaseDir, baseName + "_backup" + counter);
            counter++;
        } while (candidate.exists());
        return candidate.getName();
    }

    private static void copyDirectory(File source, File target) throws IOException {
        if (source.isDirectory()) {
            if (!target.exists()) {
                target.mkdirs();
            }

            File[] files = source.listFiles();
            if (files != null) {
                for (File file : files) {
                    File newTarget = new File(target, file.getName());
                    copyDirectory(file, newTarget);
                }
            }
        } else {
            Files.copy(source.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private static void deleteDirectory(File dir) {
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null) for (File file : files) deleteDirectory(file);
        }
        dir.delete();
    }

    private static File findOldestFile(File directory) {
        File[] files = directory.listFiles();
        if (files == null || files.length < MAX_BACKUPS) {
            return null; // Directorio vacío
        }
        return Arrays.stream(files)
                .min(Comparator.comparingLong(File::lastModified))
                .orElse(null);
    }
}
