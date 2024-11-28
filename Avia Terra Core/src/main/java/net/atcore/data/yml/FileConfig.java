package net.atcore.data.yml;

import net.atcore.data.FileYaml;
import net.atcore.messages.CategoryMessages;
import net.atcore.messages.ConsoleDiscord;

public class FileConfig extends FileYaml {

    public FileConfig() {
        super("config", null, true);
    }

    @Override
    public void loadData() {
        for (CategoryMessages messages : CategoryMessages.values()) {
            String message = fileYaml.getString("canales-de-discord." + messages.name().toLowerCase());
            messages.setIdChannel(message);
        }
        ConsoleDiscord.consoleId = fileYaml.getString("canales-de-discord.console");
    }

    @Override
    public void saveData() {

    }
}
