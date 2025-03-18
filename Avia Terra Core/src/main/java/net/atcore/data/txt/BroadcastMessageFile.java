package net.atcore.data.txt;

import net.atcore.AviaTerraCore;
import net.atcore.data.File;
import net.atcore.messages.MessagesManager;

import java.io.IOException;
import java.nio.file.Files;
import java.util.stream.Stream;

public class BroadcastMessageFile  extends File {

    public BroadcastMessageFile() {
        super("broadcastMessage", "txt", null);
        copyDefaultConfig();
        loadData();
    }

    @Override
    public void loadData() {
        AviaTerraCore.LIST_BROADCAST.clear();
        try (Stream<String> lineas = Files.lines(file.toPath())) {
            AviaTerraCore.LIST_BROADCAST.addAll(lineas.toList());
        } catch (IOException e) {
            MessagesManager.sendErrorException("Error al cargar broadcastMessage.txt", e);
        }
    }
}
