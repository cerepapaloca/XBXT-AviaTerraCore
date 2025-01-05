package net.atcore.data.txt;

import net.atcore.AviaTerraCore;
import net.atcore.data.File;
import net.atcore.messages.MessagesManager;

import java.io.IOException;
import java.nio.file.Files;
import java.util.stream.Stream;

public class MOTDFile extends File {
    public MOTDFile() {
        super("MOTD", "txt", null);
        copyDefaultConfig();
    }

    @Override
    public void loadData() {
        AviaTerraCore.LIST_MOTD.clear();
        try (Stream<String> lineas = Files.lines(file.toPath())) {
            AviaTerraCore.LIST_MOTD.addAll(lineas.toList());
        } catch (IOException e) {
            MessagesManager.sendErrorException("Error al cargar MOTD.txt", e);
        }
    }
}
